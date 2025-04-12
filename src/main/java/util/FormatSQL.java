package util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.Offset;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

public class FormatSQL {
	
	public static String format( String sqlString, int appendLeft ) {
		int leftSpace = 9 * appendLeft;
		StringBuilder sb = new StringBuilder();
		try {
			Statement statement = CCJSqlParserUtil.parse( sqlString );
			if ( statement instanceof Select ) {
				sb.append( "SELECT " );
				PlainSelect plainSelect = (PlainSelect) ( (Select) statement ).getSelectBody();
				List<SelectItem> selectItems = plainSelect.getSelectItems();
				for ( int i = 0; i < selectItems.size(); i++ ) {
					if ( i == 0 ) {
						selectItems.get( i ).accept( new SelectItemVisitorAdapter() {
							
							int selectMaxLength = getMaxSelectItem( selectItems );
							
							@Override
							public void visit( SelectExpressionItem item ) {
								if ( item.getExpression() instanceof SubSelect ) {
									SubSelect subSelect = (SubSelect) item.getExpression();
									String sSelect = "( "
											+ format( subSelect.getSelectBody().toString(), ( leftSpace + 9 ) / 9 )
											+ StringUtils.leftPad( " )", "       )".length() + leftSpace );
									sb.append( StringUtils.leftPad( sSelect, sSelect.length() + leftSpace ) );
								} else if ( item.getExpression() instanceof CaseExpression ) {
									CaseExpression caseExpr = (CaseExpression) item.getExpression();
									List<WhenClause> listWhen = caseExpr.getWhenClauses();
									sb.append(innerWhen(listWhen, leftSpace, caseExpr));
									sb.append( item.getAlias() );
								} else if ( item.getExpression() instanceof Function && "COALESCE"
										.equalsIgnoreCase( ( (Function) item.getExpression() ).getName() ) ) {
									sb.append( StringUtils.left( ( (Function) item.getExpression() ).getName() +"(",
											"(".length() + ( (Function) item.getExpression() ).getName().length() ) );
									Function function = ( (Function) item.getExpression() );
									ExpressionList parameters = function.getParameters();
									List<Expression> expressions = parameters.getExpressions();
									
									sb.append(innerExpression( expressions, leftSpace, item ));
								} else {
									if(item.getAlias() != null ) {
									sb.append( item.getExpression().toString() + StringUtils
											.leftPad( item.getAlias().toString(), item.getAlias().toString().length()
													+ selectMaxLength - item.getExpression().toString().length() ) );
									} else {
										sb.append( item.getExpression().toString() );
									}
								}
								sb.append( System.lineSeparator() );
							}
						} );
					} else {
						selectItems.get( i ).accept( new SelectItemVisitorAdapter() {
							
							int selectMaxLength = getMaxSelectItem( selectItems );
							
							@Override
							public void visit( SelectExpressionItem item ) {
								if ( item.getExpression() instanceof SubSelect ) {
									SubSelect subSelect = (SubSelect) item.getExpression();
									String sSelect = "     , ( "
											+ format( subSelect.getSelectBody().toString(), ( leftSpace + 9 ) / 9 )
											+ StringUtils.leftPad( " )", "       )".length() + leftSpace );
									sb.append( StringUtils.leftPad( sSelect, sSelect.length() + leftSpace ) );
								} else if ( item.getExpression() instanceof CaseExpression ) {
									CaseExpression caseExpr = (CaseExpression) item.getExpression();
									sb.append( StringUtils.leftPad( "     , ",
											"     , ".length() + leftSpace ) );
									List<WhenClause> listWhen = caseExpr.getWhenClauses();
									sb.append(innerWhen(listWhen, leftSpace, caseExpr));
									sb.append(item.getAlias() );
								} else if ( item.getExpression() instanceof Function && "COALESCE"
										.equalsIgnoreCase( ( (Function) item.getExpression() ).getName() ) ) {
									sb.append( StringUtils.left( "     , " + ( (Function) item.getExpression() ).getName() +"(",
											"     , (".length() + ( (Function) item.getExpression() ).getName().length() ) );
									Function function = ( (Function) item.getExpression() );
									ExpressionList parameters = function.getParameters();
									List<Expression> expressions = parameters.getExpressions();
									sb.append(innerExpression(expressions, leftSpace, item));
								} else {
									String selectExpresion = StringUtils.leftPad(
											"     , " + item.getExpression().toString(),
											( "     , " + item.getExpression().toString() ).length() + leftSpace );
									String selectAlias = "";
									if(item.getAlias() != null) {
										selectAlias = StringUtils.leftPad( item.getAlias().toString(),
											selectMaxLength - item.getExpression().toString().length()
													+ item.getAlias().toString().length() );
									}
									sb.append( selectExpresion + selectAlias );
								}
							}
						} );
						sb.append( System.lineSeparator() );
					}
				}
				FromItem fromItem = plainSelect.getFromItem();
				String from = "  FROM " + fromItem.toString();
				if ( fromItem instanceof SubSelect ) {
					from = "  FROM ( "
							+ format( ( (SubSelect) fromItem ).getSelectBody().toString(), ( leftSpace + 9 ) / 9 )
							+ StringUtils.leftPad( " )", "       )".length() + leftSpace );
				}
				sb.append( StringUtils.leftPad( from, from.length() + leftSpace ) );
				sb.append( System.lineSeparator() );
				List<Join> joinItem = plainSelect.getJoins();
				if ( joinItem != null && !joinItem.isEmpty() ) {
					for ( int i = 0; i < joinItem.size(); i++ ) {
						Join join = joinItem.get( i );
						if ( join.isRight() ) {
							String rightJoin = " RIGHT JOIN";
							sb.append( StringUtils.leftPad( rightJoin, rightJoin.length() + leftSpace ) );
						} else if ( join.isLeft() ) {
							String leftJoin = "  LEFT JOIN";
							sb.append( StringUtils.leftPad( leftJoin, leftJoin.length() + leftSpace ) );
						} else if ( join.isInner() ) {
							String innerJoin = " INNER JOIN";
							sb.append( StringUtils.leftPad( innerJoin, innerJoin.length() + leftSpace ) );
						}
						sb.append( System.lineSeparator() );
						String sJoinItem = StringUtils.leftPad( "       " + join.getRightItem().toString(),
								( join.getRightItem().toString() ).length() - ( "( ".length() * appendLeft ) - 7
										+ leftSpace );
						if ( join.getRightItem() instanceof SubSelect ) {
							sJoinItem = "       ( "
									+ format( ( (SubSelect) join.getRightItem() ).getSelectBody().toString(),
											( leftSpace + 9 ) / 9 )
									+ StringUtils.leftPad( "       )" + ( (SubSelect) join.getRightItem() ).getAlias(),
											"       )".length() + ( "( ".length() * appendLeft ) + leftSpace );
						}
						sb.append( StringUtils.leftPad( sJoinItem, sJoinItem.length() + leftSpace ) );
						sb.append( System.lineSeparator() );
						Expression onExpressions = join.getOnExpression();
						LinkedHashMap<String, Object> splitExpressions = splitAndExpression( onExpressions, "ON" );
						for ( Entry<String, Object> entry : splitExpressions.entrySet() ) {
							String sCondition = "";
							if ( "AND".equals( entry.getValue() ) ) {
								sCondition = "   AND ";
							} else if ( "OR".equals( entry.getValue() ) ) {
								sCondition = "    OR ";
							} else if ( "ON".equals( entry.getValue() ) ) {
								sCondition = "    ON ";
							}
							Expression onExpression = CCJSqlParserUtil.parseExpression( entry.getKey() );
							Expression rightExpression = null;
							Expression leftExpression = null;
							if ( onExpression instanceof EqualsTo ) {
								EqualsTo equalsTo = (EqualsTo) onExpression;
								rightExpression = equalsTo.getRightExpression();
								leftExpression = equalsTo.getLeftExpression();
							} else if ( onExpression instanceof GreaterThan ) {
								GreaterThan greaterThan = (GreaterThan) onExpression;
								rightExpression = greaterThan.getRightExpression();
								leftExpression = greaterThan.getLeftExpression();
							} else if ( onExpression instanceof GreaterThanEquals ) {
								GreaterThanEquals greaterThanEquals = (GreaterThanEquals) onExpression;
								rightExpression = greaterThanEquals.getRightExpression();
								leftExpression = greaterThanEquals.getLeftExpression();
							} else if ( onExpression instanceof MinorThan ) {
								MinorThan minorThan = (MinorThan) onExpression;
								rightExpression = minorThan.getRightExpression();
								leftExpression = minorThan.getLeftExpression();
							} else if ( onExpression instanceof MinorThanEquals ) {
								MinorThanEquals minorThanEquals = (MinorThanEquals) onExpression;
								rightExpression = minorThanEquals.getRightExpression();
								leftExpression = minorThanEquals.getLeftExpression();
							}
							if ( rightExpression instanceof SubSelect ) {
								SubSelect subSelect = (SubSelect) rightExpression;
								sCondition += leftExpression + " = ";
								sCondition += "( "
										+ format( subSelect.getSelectBody().toString(), ( leftSpace + 9 ) / 9 )
										+ StringUtils.leftPad( " )", "       )".length() + leftSpace );
							} else {
								sCondition += entry.getKey();
							}
							sb.append( StringUtils.leftPad( sCondition, sCondition.length() + leftSpace ) );
							sb.append( System.lineSeparator() );
						}
					}
				}
				Expression where = plainSelect.getWhere();
				if ( where != null ) {
					LinkedHashMap<String, Object> splitExpressions = splitAndExpression( where, "WHERE" );
					for ( Entry<String, Object> entry : splitExpressions.entrySet() ) {
						String sCondition = "";
						if ( "AND".equals( entry.getValue() ) ) {
							sCondition = "   AND ";
						} else if ( "OR".equals( entry.getValue() ) ) {
							sCondition = "    OR ";
						} else if ( "WHERE".equals( entry.getValue() ) ) {
							sCondition = " WHERE ";
						}
						Expression onExpression = CCJSqlParserUtil.parseExpression( entry.getKey() );
						Expression rightExpression = null;
						Expression leftExpression = null;
						if ( onExpression instanceof EqualsTo ) {
							EqualsTo equalsTo = (EqualsTo) onExpression;
							rightExpression = equalsTo.getRightExpression();
							leftExpression = equalsTo.getLeftExpression();
						} else if ( onExpression instanceof GreaterThan ) {
							GreaterThan greaterThan = (GreaterThan) onExpression;
							rightExpression = greaterThan.getRightExpression();
							leftExpression = greaterThan.getLeftExpression();
						} else if ( onExpression instanceof GreaterThanEquals ) {
							GreaterThanEquals greaterThanEquals = (GreaterThanEquals) onExpression;
							rightExpression = greaterThanEquals.getRightExpression();
							leftExpression = greaterThanEquals.getLeftExpression();
						} else if ( onExpression instanceof MinorThan ) {
							MinorThan minorThan = (MinorThan) onExpression;
							rightExpression = minorThan.getRightExpression();
							leftExpression = minorThan.getLeftExpression();
						} else if ( onExpression instanceof MinorThanEquals ) {
							MinorThanEquals minorThanEquals = (MinorThanEquals) onExpression;
							rightExpression = minorThanEquals.getRightExpression();
							leftExpression = minorThanEquals.getLeftExpression();
						}
						if ( rightExpression instanceof SubSelect ) {
							SubSelect subSelect = (SubSelect) rightExpression;
							sCondition += leftExpression;
							sCondition += "( " + format( subSelect.getSelectBody().toString(), ( leftSpace + 9 ) / 9 )
									+ StringUtils.leftPad( " )", "       )".length() + leftSpace );
						} else {
							sCondition += entry.getKey();
						}
						sb.append( StringUtils.leftPad( sCondition, sCondition.length() + leftSpace ) );
						sb.append( System.lineSeparator() );
					}
				}
				GroupByElement groupByElement = plainSelect.getGroupBy();
				if ( groupByElement != null ) {
					List<Expression> groupByExpressions = groupByElement.getGroupByExpressions();
					sb.append( StringUtils.leftPad( " GROUP BY ", " GROUP BY ".length() + leftSpace ) );
					for ( int j = 0; j < groupByExpressions.size(); j++ ) {
						sb.append( groupByExpressions.get( j ) );
						if ( ( j + 1 ) < groupByExpressions.size() ) {
							sb.append( ", " );
						}
					}
					sb.append( System.lineSeparator() );
				}
				List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
				if ( orderByElements != null ) {
					sb.append( StringUtils.leftPad( " ORDER BY ", " ORDER BY ".length() + leftSpace ) );
					for ( int j = 0; j < orderByElements.size(); j++ ) {
						sb.append( orderByElements.get( j ) );
						if ( ( j + 1 ) < orderByElements.size() ) {
							sb.append( ", " );
						}
					}
					sb.append( System.lineSeparator() );
				}
				Limit limit = plainSelect.getLimit();
				Offset offset = plainSelect.getOffset();
				if ( limit != null || offset != null ) {
					sb.append( limit.toString() );
					sb.append( offset.toString() );
				}
			} else if ( statement instanceof Update ) {
			} else if ( statement instanceof Delete ) {
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private static String innerWhen(List<WhenClause> listWhen, int leftSpace , CaseExpression caseExpr) {
		StringBuilder sb = new StringBuilder();
		for ( int j = 0; j < listWhen.size(); j++ ) {
			
			if ( j == 0 ) {
				Expression thenExpression = listWhen.get( j ).getThenExpression();
				sb.append("CASE WHEN " + listWhen.get( j ).getWhenExpression().toString());
				if (thenExpression instanceof CaseExpression) {
                    List<WhenClause> caseExpression = ( (CaseExpression) thenExpression ).getWhenClauses();
                    sb.append( " THEN " );
                    sb.append(innerWhen(caseExpression, leftSpace +  "     , CASE WHEN".length() + listWhen.get( j ).getWhenExpression().toString().length() , (CaseExpression) thenExpression  ));;
                } else if (thenExpression instanceof SubSelect) {
                	sb.append( " THEN " );
                	String sSelect = " ("+ format(( (SubSelect) thenExpression ).getSelectBody().toString(), ( leftSpace + 9 ) / 9) + StringUtils.leftPad( " )", "       )".length() + leftSpace );
                	sb.append( StringUtils.leftPad( sSelect, sSelect.length() + leftSpace ) );
                } else {
                   sb.append( " THEN " );
                   sb.append(  thenExpression.toString() );
                }
			} else {
				Expression thenExpression = listWhen.get( j ).getThenExpression();
				sb.append( System.lineSeparator() );
				sb.append(StringUtils.leftPad(" WHEN ","      , CASE WHEN".length() + leftSpace) +  listWhen.get( j ).getWhenExpression().toString());
				if (thenExpression instanceof CaseExpression) {
                    List<WhenClause> caseExpression = ( (CaseExpression) thenExpression ).getWhenClauses();
                    sb.append( " THEN " );
                    sb.append( innerWhen(caseExpression, leftSpace +  "     , CASE WHEN".length() +  listWhen.get( j ).getWhenExpression().toString().length(), (CaseExpression) thenExpression  ));
                } else if (thenExpression instanceof SubSelect) {
                	sb.append( " THEN " );
                	String sSelect = "("+ format(( (SubSelect) thenExpression ).getSelectBody().toString(), ( leftSpace + 9 ) / 9) + StringUtils.leftPad( " )", "            )".length() + leftSpace );
                	sb.append( StringUtils.leftPad( sSelect, sSelect.length() + leftSpace ) );
                } else {
                	sb.append( " THEN " );
                	sb.append(thenExpression.toString());
                }
			}
		}
		if ( caseExpr.getElseExpression() != null ) {
			sb.append( " ELSE " + caseExpr.getElseExpression());
		}
		sb.append( " END " );
		return sb.toString();
	}
	
	private static String innerExpression(List<Expression> expressions, int leftSpace, SelectExpressionItem item) {
		StringBuilder sb = new StringBuilder();
		for(int j =0; j< expressions.size(); j++) {
			Expression param = expressions.get( j );
			if ( param instanceof SubSelect ) {
				SubSelect subSelect = (SubSelect) param;
				String sSelect = "( "
						+ format( subSelect.getSelectBody().toString(),
								( leftSpace + 18 ) / 9 )
						+ StringUtils.leftPad( " )", "       )".length() + 9 + leftSpace );
				sb.append( StringUtils.leftPad( sSelect, sSelect.length() + leftSpace ) );
				if((j + 1) < expressions.size()) {
					sb.append( ", " );
				} else {
					sb.append( System.lineSeparator() );
					sb.append( StringUtils.leftPad("              )" + item.getAlias().toString(), ("              ) " + item.getAlias().toString()).length() + leftSpace ));
				}
			} else if ( param instanceof CaseExpression ) {
				sb.append( StringUtils.leftPad( " CASE ", "CASE ".length() + leftSpace ) );
				CaseExpression caseExpr = (CaseExpression) param;
				List<WhenClause> listWhen = caseExpr.getWhenClauses();
				for ( int k = 0; k < listWhen.size(); k++ ) {
					if ( k == 0 ) {
						sb.append( StringUtils.leftPad( listWhen.get( j ).toString(),
								( listWhen.get( k ).toString() ).length() ) );
					} else {
						sb.append( StringUtils.leftPad(
								"" + listWhen.get( k ).toString(),
								( "                      " + listWhen.get( k ).toString() ).length()
										+ leftSpace ) );
					}
					if ( k + 1 < listWhen.size() || caseExpr.getElseExpression() != null ) {
						sb.append( System.lineSeparator() );
					}
				}
				if ( caseExpr.getElseExpression() != null ) {
					sb.append( StringUtils.leftPad(
							"ELSE " + caseExpr.getElseExpression(),
							( "                      ELSE " + caseExpr.getElseExpression() ).length()
									+ leftSpace ) );
				}
				sb.append( " END " );
				if((j + 1) < expressions.size()) {
					sb.append( ", " );
				} else {
					sb.append( System.lineSeparator() );
					sb.append( StringUtils.leftPad(")" + item.getAlias().toString(), ("              ) " + item.getAlias().toString()).length() + leftSpace ));
				}
			} else {
				sb.append( param.toString() );
				if((j + 1) < expressions.size()) {
					sb.append( ", " );
				} else {
					sb.append( System.lineSeparator() );
					sb.append( StringUtils.leftPad(")" + item.getAlias().toString(), ("              ) " + item.getAlias().toString()).length() + leftSpace ));
				}
			}
		}
		
		return sb.toString();
	}
	
	private static int getMaxSelectItem( List<SelectItem> listSelectItems ) {
		int maxLength = 0;
		for ( int i = 0; i < listSelectItems.size(); i++ ) {
			SelectExpressionItem item = (SelectExpressionItem) listSelectItems.get( i );
			if ( item.getExpression().toString().length() > 50 ) {
				continue;
			} else if ( maxLength < item.getExpression().toString().length() ) {
				maxLength = item.getExpression().toString().length();
			}
		}
		return maxLength;
	}
	
	private static LinkedHashMap<String, Object> splitAndExpression( Expression expression, String operator ) {
		LinkedHashMap<String, Object> expressions = new LinkedHashMap<String, Object>();
		if ( expression instanceof AndExpression ) {
			AndExpression andExpression = (AndExpression) expression;
			expressions.putAll( splitAndExpression( andExpression.getLeftExpression(),
					!operator.matches( "AND||OR" ) ? operator : "" ) );
			expressions.putAll( splitAndExpression( andExpression.getRightExpression(), "AND" ) );
		} else if ( expression instanceof OrExpression ) {
			OrExpression orExpression = (OrExpression) expression;
			expressions.putAll( splitAndExpression( orExpression.getLeftExpression(),
					!operator.matches( "AND||OR" ) ? operator : "" ) );
			expressions.putAll( splitAndExpression( orExpression.getRightExpression(), "OR" ) );
		} else {
			expressions.put( expression.toString(), operator );
		}
		return expressions;
	}
}
