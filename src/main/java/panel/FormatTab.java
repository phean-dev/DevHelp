package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import font.DynamicFont;
import style.RoundedBorder;
import util.FormatSQL;

public class FormatTab extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JEditorPane formatArea;
	private JScrollPane jScollJson;
	private JPanel savePanel;
	private JButton saveButton;
	private JButton openFile;
	private String strDocumentType;
	private JLabel supportFormat;
	private UndoManager undoManager;
	
	
	private enum DocumentType {
		XML( "XML", "XML" ), JSON( "JSON", "JSON" ), JAVASCRIPT( "JAVASCRIPT", "Javascript" ), SQL("SQL","SQL");
		
		private String value;
		private String description;
		
		DocumentType( String value, String description ) {
			this.value = value;
			this.description = description;
		}
		
		public String getValue() {
			return value;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	public FormatTab() {
		setLayout( new BorderLayout() );
		formatArea = new JEditorPane();
		formatArea.setLayout( new BorderLayout( 5, 5 ) );
		formatArea.setBorder( new RoundedBorder( 1 ) );
		formatArea.setFont(new Font("Courier New", Font.PLAIN, 14) );
		formatArea.setContentType( "text/plain" );
		jScollJson = new JScrollPane( formatArea );
		jScollJson.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		jScollJson.getVerticalScrollBar().setUnitIncrement( 16 );
		savePanel = new JPanel();
		savePanel.setLayout( new BorderLayout() );
		openFile = new JButton( "បើក" );
		openFile.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular" ).deriveFont( 13f ) );
		savePanel.add( openFile, BorderLayout.WEST );
		supportFormat = new JLabel("   គាំទ្រ: JSON, XML ,SQL  ");
		supportFormat.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular" ).deriveFont( 13f ) );
		savePanel.add( supportFormat, BorderLayout.CENTER );
		saveButton = new JButton( "រក្សាទុក" );
		saveButton.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular" ).deriveFont( 13f ) );
		savePanel.add( saveButton, BorderLayout.EAST );
		savePanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) );
		add( jScollJson, BorderLayout.CENTER );
		add( savePanel, BorderLayout.SOUTH );
		formatArea.getDocument().addDocumentListener( new DocumentListener() {
			
			@Override
			public void removeUpdate( DocumentEvent arg0 ) {
				detectDocumentType();
			}
			
			@Override
			public void insertUpdate( DocumentEvent arg0 ) {
				detectDocumentType();
			}
			
			@Override
			public void changedUpdate( DocumentEvent arg0 ) {
				detectDocumentType();
			}
		} );
		formatArea.addKeyListener( new KeyAdapter() {
			
			@Override
			public void keyPressed( KeyEvent e ) {
				if ( e.isControlDown() ) {
					if ( e.getKeyCode() == KeyEvent.VK_S ) {
						if ( DocumentType.JSON.getValue().equals( strDocumentType ) ) {
							saveJsonToFile();
						}
					}
					if ( e.getKeyCode() == KeyEvent.VK_O ) {
						openFile();
					}
				}
			};
		} );
		
		formatArea.getInputMap(JComponent.WHEN_FOCUSED).put(
		        KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
		        "format"
		    );
		
		formatArea.getActionMap().put("format", new AbstractAction() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (DocumentType.JSON.getValue().equals(strDocumentType)) {
	            	String sJson = formatArea.getText().trim();
	            	try {
	            	    JsonParser parser = new JsonParser();
	            	    JsonElement jsonElement = parser.parse(sJson);
	            	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	            	    formatArea.setText(gson.toJson(jsonElement));
	            	} catch (Exception ex) {
	            	    JOptionPane.showMessageDialog(formatArea, "Invalid Json.", "Error", JOptionPane.ERROR_MESSAGE);
	            	}
	            } else if (DocumentType.XML.getValue().equals(strDocumentType)) {
	                formatXML();
	            } else if (DocumentType.SQL.getValue().equals(strDocumentType)) {
	                formatSQL();
	            }
	        }
	    });
		
		formatArea.getInputMap(JComponent.WHEN_FOCUSED).put(
		        KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
		        "findText");
		    formatArea.getActionMap().put("findText", new AbstractAction() {
		        @Override
		        public void actionPerformed(ActionEvent e) {
		        	String word = JOptionPane.showInputDialog(formatArea,"Insert word to find:");
			        if (word != null && !word.isEmpty()) {
			            highlight(formatArea, word);
			        }
		        }
		    });
		
		saveButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				saveJsonToFile();
			}
		} );
		openFile.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				openFile();
			}
		} );
		formatArea.setDropTarget( new DropTarget() {
			
			public synchronized void drop( DropTargetDropEvent evt ) {
				try {
					evt.acceptDrop( DnDConstants.ACTION_COPY );
					java.util.List<File> droppedFiles = (java.util.List<File>) evt.getTransferable()
							.getTransferData( DataFlavor.javaFileListFlavor );
					for ( File file : droppedFiles ) {
						if ( file.getAbsolutePath().endsWith( ".xlsx" ) ) {
							excelFileToJson( file );
						} else if ( file.getAbsolutePath().endsWith( ".txt" ) ) {
							textFileToJson( file );
						} else if ( file.getAbsolutePath().endsWith( ".json" ) ) {
							textFileToJson( file );
						}
					}
				} catch ( Exception ex ) {
					ex.printStackTrace();
				}
			}
		} );
		
		undoManager = new UndoManager();
		setDoOrUndo(formatArea);
	}
	
	private void formatSQL() {
		formatArea.setText( FormatSQL.format( formatArea.getText(), 0 ));
	}
	
	public void highlight(JEditorPane formatArea, String word) {
        try {
            Highlighter highlighter = formatArea.getHighlighter();
            javax.swing.text.Document doc = formatArea.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            highlighter.removeAllHighlights();

            while ((pos = text.indexOf(word, pos)) >= 0) {
                highlighter.addHighlight(pos, pos + word.length(), new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
                pos += word.length();
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
	
	private void detectDocumentType() {
		String strText = formatArea.getText();
		if ( strText.startsWith( "<" ) ) {
			strDocumentType = DocumentType.XML.getValue();
		} else if ( strText.trim().startsWith( "{" ) || strText.trim().startsWith( "[" ) ) {
			strDocumentType = DocumentType.JSON.getValue();
		} else if ( strText.trim().startsWith( "function" ) || strText.trim().contains( "var " ) ) {
			strDocumentType = DocumentType.JAVASCRIPT.getValue();
		} else if ( isSQL(strText) ) {
			strDocumentType = DocumentType.SQL.getValue();
		}
	}
	
	private boolean isSQL(String sText) {
		sText = sText.trim();
		sText = sText.toUpperCase();
		String[] sqlPrefixes = {"SELECT", "UPDATE", "INSERT"};
		 for (String prefix : sqlPrefixes) {
	            if (sText.startsWith(prefix)) {
	                return true;
	            }
	        }
		 return false;
	}
	
	public void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle( "Open File" );
		FileNameExtensionFilter excelFilter = new FileNameExtensionFilter( "Excel Files (.xlsx)", "xlsx" );
		FileNameExtensionFilter textFilter = new FileNameExtensionFilter( "Text Files (.txt)", "txt" );
		FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter( "Json Files (.json)", "json" );
		fileChooser.addChoosableFileFilter( excelFilter );
		fileChooser.addChoosableFileFilter( textFilter );
		fileChooser.addChoosableFileFilter( jsonFilter );
		int userSelection = fileChooser.showOpenDialog( this );
		if ( userSelection == JFileChooser.APPROVE_OPTION ) {
			File fileToOpen = fileChooser.getSelectedFile();
			if ( fileToOpen.getAbsolutePath().endsWith( ".xlsx" ) ) {
				excelFileToJson( fileToOpen );
			} else if ( fileToOpen.getAbsolutePath().endsWith( ".txt" ) ) {
				textFileToJson( fileToOpen );
			} else if ( fileToOpen.getAbsolutePath().endsWith( ".json" ) ) {
				textFileToJson( fileToOpen );
			}
		}
	}
	
	private void setDoOrUndo( JEditorPane textArea1 ) {
		textArea1.getDocument().addUndoableEditListener( undoManager );
		textArea1.getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "control Z" ), "Undo" );
		textArea1.getActionMap().put( "Undo", new AbstractAction() {
			
			@Override
			public void actionPerformed( ActionEvent e ) {
				if ( undoManager.canUndo() ) {
					undoManager.undo();
				}
			}
		} );
		textArea1.getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "control Y" ), "Redo" );
		textArea1.getActionMap().put( "Redo", new AbstractAction() {
			
			@Override
			public void actionPerformed( ActionEvent e ) {
				if ( undoManager.canRedo() ) {
					undoManager.redo();
				}
			}
		} );
	}
	
	public void formatXML() {
		try {
			String xmlString = formatArea.getText().trim();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( new InputSource( new StringReader( xmlString ) ) );
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );
			StringWriter writer = new StringWriter();
			transformer.transform( new DOMSource( doc ), new StreamResult( writer ) );
			formatArea.setText( writer.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private void excelFileToJson( File fileToOpen ) {
		try ( FileInputStream fis = new FileInputStream( fileToOpen ) ; Workbook workbook = new XSSFWorkbook( fis )) {
			Sheet sheet = workbook.getSheetAt( 0 );
			Iterator<Row> rowIterator = sheet.iterator();
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode arrayNode = mapper.createArrayNode();
			Row headerRow = rowIterator.next();
			Iterator<Cell> headerCellIterator = headerRow.cellIterator();
			String[] headers = new String[headerRow.getPhysicalNumberOfCells()];
			int headerIndex = 0;
			while ( headerCellIterator.hasNext() ) {
				headers[headerIndex++] = headerCellIterator.next().getStringCellValue();
			}
			while ( rowIterator.hasNext() ) {
				Row row = rowIterator.next();
				ObjectNode objectNode = mapper.createObjectNode();
				for ( int i = 0; i < headers.length; i++ ) {
					Cell cell = row.getCell( i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK );
					switch ( cell.getCellType() ) {
						case STRING :
							objectNode.put( headers[i], cell.getStringCellValue() );
							break;
						case NUMERIC :
							if ( DateUtil.isCellDateFormatted( cell ) ) {
								objectNode.put( headers[i], cell.getDateCellValue().toString() );
							} else {
								objectNode.put( headers[i], cell.getNumericCellValue() );
							}
							break;
						case BOOLEAN :
							objectNode.put( headers[i], cell.getBooleanCellValue() );
							break;
						case BLANK :
							objectNode.put( headers[i], "" );
							break;
						default :
							objectNode.put( headers[i], cell.toString() );
							break;
					}
				}
				arrayNode.add( objectNode );
			}
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( arrayNode );
			formatArea.setText( jsonString );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	public void textFileToJson( File openFile ) {
		try ( BufferedReader br = new BufferedReader( new FileReader( openFile ) )) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ( ( line = br.readLine() ) != null ) {
				sb.append( line ).append( "\n" );
			}
			formatArea.setText( sb.toString() );
		} catch ( IOException e ) {
			e.printStackTrace();
			formatArea.setText( "Error reading text file: " + e.getMessage() );
		}
	}
	
	public void saveJsonToFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle( "Specify a file to save" );
		FileNameExtensionFilter excelFilter = new FileNameExtensionFilter( "Excel Files (.xlsx)", "xlsx" );
		FileNameExtensionFilter textFilter = new FileNameExtensionFilter( "Text Files (.txt)", "txt" );
		FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter( "Json Files (.json)", "json" );
		fileChooser.addChoosableFileFilter( excelFilter );
		fileChooser.addChoosableFileFilter( textFilter );
		fileChooser.addChoosableFileFilter( jsonFilter );
		int userSelection = fileChooser.showSaveDialog( this );
		if ( userSelection == JFileChooser.APPROVE_OPTION ) {
			File fileToSave = fileChooser.getSelectedFile();
			String extension = "";
			if ( fileChooser.getFileFilter() == textFilter ) {
				extension = ".txt";
			} else if ( fileChooser.getFileFilter() == excelFilter ) {
				extension = ".xlsx";
			} else if ( fileChooser.getFileFilter() == jsonFilter ) {
				extension = ".json";
			}
			if ( !fileToSave.getAbsolutePath().endsWith( extension ) ) {
				fileToSave = new File( fileToSave + extension );
			}
			if ( fileToSave.getAbsolutePath().endsWith( ".xlsx" ) ) {
				toExcelFile( fileToSave );
			} else if ( fileToSave.getAbsolutePath().endsWith( ".txt" ) ) {
				toTextFile( fileToSave );
			} else if ( fileToSave.getAbsolutePath().endsWith( ".json" ) ) {
				toTextFile( fileToSave );
			}
		}
	}
	
	private void toTextFile( File fileToSave ) {
		try ( FileWriter fileWriter = new FileWriter( fileToSave )) {
			String jsonContent = formatArea.getText();
			fileWriter.write( jsonContent );
			JOptionPane.showMessageDialog( this, "File saved successfully!" );
		} catch ( IOException ex ) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog( this, "Error saving file: " + ex.getMessage() );
		}
	}
	
	private void toExcelFile( File fileToSave ) {
		try ( FileOutputStream fileOut = new FileOutputStream( fileToSave )) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree( formatArea.getText() );
			Workbook workbook = new XSSFWorkbook();
			recursiveNode( workbook, rootNode, "main" );
			workbook.write( fileOut );
			workbook.close();
			JOptionPane.showMessageDialog( this, "File saved successfully!" );
		} catch ( IOException ex ) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog( this, "Error saving file: " + ex.getMessage() );
		}
	}
	
	private void recursiveNode( Workbook workbook, JsonNode jsonNode, String sheetName ) {
		if ( jsonNode.isObject() ) {
			Sheet sheet = workbook.createSheet( sheetName );
			Row headerRow = sheet.createRow( 0 );
			Row valueRow = sheet.createRow( 1 );
			int columnIndex = 0;
			for ( Iterator<String> it = jsonNode.fieldNames(); it.hasNext(); ) {
				String next = it.next();
				if ( jsonNode.get( next ).isObject() || jsonNode.get( next ).isArray() ) {
					recursiveNode( workbook, jsonNode.get( next ), next );
				} else {
					String fieldName = next;
					JsonNode fieldValue = jsonNode.get( fieldName );
					Cell headerCell = headerRow.createCell( columnIndex );
					Cell valueCell = valueRow.createCell( columnIndex );
					headerCell.setCellValue( fieldName );
					valueCell.setCellValue( fieldValue.asText() );
					columnIndex++;
				}
			}
		} else if ( jsonNode.isArray() ) {
			int rowIndex = 1;
			Sheet sheet = workbook.createSheet( sheetName );
			Row headerRow = sheet.createRow( 0 );
			if ( jsonNode.size() <= 0 ) {
				return;
			}
			JsonNode firstElement = jsonNode.get( 0 );
			Iterator<Map.Entry<String, JsonNode>> fields = firstElement.fields();
			int headerCellIndex = 0;
			while ( fields.hasNext() ) {
				Map.Entry<String, JsonNode> field = fields.next();
				Cell cell = headerRow.createCell( headerCellIndex++ );
				cell.setCellValue( field.getKey() );
			}
			for ( JsonNode element : jsonNode ) {
				Row row = sheet.createRow( rowIndex++ );
				int cellIndex = 0;
				Iterator<Map.Entry<String, JsonNode>> elementFields = element.fields();
				while ( elementFields.hasNext() ) {
					Map.Entry<String, JsonNode> field = elementFields.next();
					Cell cell = row.createCell( cellIndex++ );
					if ( field.getValue().isObject() || field.getValue().isArray() ) {
						recursiveNode( workbook, field.getValue(), field.getKey() );
					} else if ( field.getValue().isNumber() ) {
						cell.setCellValue( field.getValue().asDouble() );
					} else {
						cell.setCellValue( field.getValue().asText() );
					}
				}
			}
		} else {
			Sheet sheet = workbook.createSheet( sheetName );
			Row headerRow = sheet.createRow( 0 );
			Row valueRow = sheet.createRow( 1 );
			int columnIndex = 0;
			for ( Iterator<String> it = jsonNode.fieldNames(); it.hasNext(); ) {
				String fieldName = it.next();
				JsonNode fieldValue = jsonNode.get( fieldName );
				Cell headerCell = headerRow.createCell( columnIndex );
				Cell valueCell = valueRow.createCell( columnIndex );
				headerCell.setCellValue( fieldName );
				valueCell.setCellValue( fieldValue.asText() );
				columnIndex++;
			}
		}
	}
}
