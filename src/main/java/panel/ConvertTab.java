package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import font.DynamicFont;

public class ConvertTab extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JComboBox<String> convertType;
	private JTextArea textArea1;
	private JTextArea textArea2;
	private LinkedHashMap<String, Object> khmerNumeric;
	private LinkedHashMap<String, Object> khmerWord;
	private LinkedHashMap<String, Object> khmer2UnitsWord;
	private LinkedHashMap<String, Object> khmerUnitsWord;
	private String[] khmerNumber = { "០", "១", "២", "៣", "៤", "៥", "៦", "៧", "៨", "៩" };
	private String[] KhmerWord = { "សូន្យ", "មួយ", "ពីរ", "បី", "បួន", "ប្រាំ", "ប្រាំមួយ", "ប្រាំពីរ", "ប្រាំបី",
			"ប្រាំបួន" };
	private String[] number = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	private String[] word2Units = { "", "ដប់", "ម្ភៃ", "សាមសិប", "សែសិប", "ហាសិប", "ហុកសិប", "ចិតសិប", "ប៊ែតសិប",
			"កៅសិប" };
	private String[] wordUnits = { "", "", "", "រយ", "ពាន់", "ម៉ឺន", "សែន", "លាន", "អត់ដឹង", "អត់ដឹង", "អត់ដឹង",
			"អត់ដឹង" };
	private String[] wordPress = { "ចុច", "និង" };
	private static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };
	private static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };
	private static final String[] thousands = { "", "Thousand", "Million", "Billion", "Trillion", "Quadrillion",
			"Quintillion" };
	
	public ConvertTab() {
		initKhmerNumeric();
		setLayout( new BorderLayout() );
		convertType = new JComboBox<String>();
		convertType.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular" ).deriveFont( 13f ) );
		convertType.addItem( "លេខទៅជាអក្សរខ្មែរ" );
		convertType.addItem( "លេខទៅជាលេខខ្មែរ" );
		convertType.addItem( "លេខទៅជាអក្សរ" );
		convertType.addItem( "អក្សរទៅជា Sha512" );
		convertType.addItem( "អក្សរទៅជា Sha256" );
		convertType.addItem( "អក្សរទៅជា Binary" );
		convertType.addItem( "អក្សរទៅជា Base64String" );
		convertType.addItem( "Base64String ទៅជាអក្ស" );
		textArea1 = new JTextArea( 20, 40 );
		textArea2 = new JTextArea( 20, 40 );
		textArea1.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular" ).deriveFont( 13f ) );
		textArea2.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular" ).deriveFont( 13f ) );
		textArea2.setEditable( false );
		JPanel pnlArea = new JPanel( new GridLayout( 1, 2, 5, 5 ) );
		JScrollPane scrollPane1 = new JScrollPane( textArea1 );
		scrollPane1.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		scrollPane1.getVerticalScrollBar().setUnitIncrement( 16 );
		JScrollPane scrollPane2 = new JScrollPane( textArea2 );
		scrollPane2.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		scrollPane2.getVerticalScrollBar().setUnitIncrement( 16 );
		pnlArea.add( scrollPane1 );
		pnlArea.add( scrollPane2 );
		add( convertType, BorderLayout.NORTH );
		add( pnlArea, BorderLayout.CENTER );
		
		convertType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                   textArea1.requestFocusInWindow();
                }
            }
        });
		
		textArea1.getDocument().addDocumentListener( new DocumentListener() {
			
			@Override
			public void removeUpdate( DocumentEvent arg0 ) {
				textChanged();
			}
			
			@Override
			public void insertUpdate( DocumentEvent arg0 ) {
				textChanged();
			}
			
			@Override
			public void changedUpdate( DocumentEvent arg0 ) {
				textChanged();
			}
			
			private void textChanged() {
				String sText = textArea1.getText().trim();
				sText = sText.replace( ",", "" );
				sText = sText.replace( "\n", "" );
				sText = sText.replace( " ", "" );
				if ( convertType.getSelectedIndex() == 0 ) {
					textArea2.setText( convertNumToWord( sText ) );
				} else if ( convertType.getSelectedIndex() == 1 ) {
					textArea2.setText( toKhmerNumber( sText ) + " (" + convertNumToWord( sText ) + ")" );
				} else if ( convertType.getSelectedIndex() == 2 ) {
					textArea2.setText( numberToText( sText ) );
				} else if ( convertType.getSelectedIndex() == 3 ) {
					textArea2.setText( sha512( sText ) );
				} else if ( convertType.getSelectedIndex() == 4 ) {
					textArea2.setText( sha256( sText ) );
				} else if ( convertType.getSelectedIndex() == 5 ) {
					textArea2.setText( toBinary( sText ) );
				} else if ( convertType.getSelectedIndex() == 6 ) {
					sText = textArea1.getText().trim();
					textArea2.setText( toBase64String( sText ) );
				} else if ( convertType.getSelectedIndex() == 7 ) {
					sText = textArea1.getText().trim();
					textArea2.setText( Base64ToString( sText ) );
				}
			}
		} );
	}
	
	private String Base64ToString(String sText) {
      byte[] decodedBytes = Base64.getDecoder().decode(sText);
      String decodedString = new String(decodedBytes);
      return decodedString;
	}
	
	private String toBase64String(String sText) {
		String encodedString = Base64.getEncoder().encodeToString(sText.getBytes());
		return encodedString;
	}
	private String toBinary(String sText) {
		 StringBuilder sb = new StringBuilder();
	        byte[] bytes = sText.getBytes();
	        for (byte b : bytes) {
	            for (int i = 7; i >= 0; i--) {
	                sb.append((b >> i) & 1);
	            }
	            sb.append(" ");
	        }
	        return sb.toString().trim();
	}
	private String sha512( String sText ) {
		StringBuilder sb = new StringBuilder();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance( "SHA-512" );
			byte[] hashBytes = md.digest( sText.getBytes() );
			for ( byte b : hashBytes ) {
				sb.append( String.format( "%02x", b ) );
			}
		} catch ( NoSuchAlgorithmException e ) {
		}
		return sb.toString();
	}
	
	private String sha256( String sText ) {
		 StringBuilder sb = new StringBuilder();
		 MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			 byte[] hashBytes = md.digest(sText.getBytes());
	         for (byte b : hashBytes) {
	             sb.append(String.format("%02x", b));
	         }
		} catch ( NoSuchAlgorithmException e ) {
			
		}
        
         return sb.toString();
	}
	private String convert( long number ) {
		if ( number == 0 ) {
			return "Zero";
		}
		String words = "";
		int thousandCounter = 0;
		while ( number > 0 ) {
			if ( number % 1000 != 0 ) {
				words = convertLessThanThousand( (int) ( number % 1000 ) ) + " " + thousands[thousandCounter] + " "
						+ words;
			}
			number /= 1000;
			thousandCounter++;
		}
		return words.trim();
	}
	
	private String convertLessThanThousand( int number ) {
		if ( number < 20 ) {
			return units[number];
		} else if ( number < 100 ) {
			return tens[number / 10] + ( ( number % 10 != 0 ) ? " " + units[number % 10] : "" );
		} else {
			return units[number / 100] + " Hundred"
					+ ( ( number % 100 != 0 ) ? " and " + convertLessThanThousand( number % 100 ) : "" );
		}
	}
	
	private String numberToText( String sNumber ) {
		try {
			double number = Double.valueOf( sNumber );
			String[] arrNumber = String.valueOf( number ).split( "\\." );
			if ( arrNumber.length == 0 ) {
				return "";
			}
			String result = convert( Integer.parseInt( arrNumber[0] ) );
			if ( arrNumber.length > 1 ) {
				result += " Point" + " " + convert( Integer.parseInt( arrNumber[1] ) );
			}
			return result;
		} catch ( Exception e ) {
			e.printStackTrace();
			return "";
		}
	}
	
	private void initKhmerNumeric() {
		khmerNumeric = new LinkedHashMap<String, Object>();
		khmerWord = new LinkedHashMap<String, Object>();
		khmer2UnitsWord = new LinkedHashMap<String, Object>();
		khmerUnitsWord = new LinkedHashMap<String, Object>();
		for ( int i = 0; i < 10; i++ ) {
			khmerNumeric.put( String.valueOf( i ), khmerNumber[i] );
			khmerWord.put( String.valueOf( i ), KhmerWord[i] );
			khmer2UnitsWord.put( String.valueOf( i ), word2Units[i] );
			khmerUnitsWord.put( String.valueOf( i ), wordUnits[i] );
		}
	}
	
	private String toKhmerNumber( String sText ) {
		char[] arrCharText = sText.toCharArray();
		for ( int i = 0; i < khmerNumber.length; i++ ) {
			for ( int j = 0; j < arrCharText.length; j++ ) {
				if ( number[i].equals( String.valueOf( arrCharText[j] ) ) ) {
					arrCharText[j] = khmerNumber[i].charAt( 0 );
				}
			}
		}
		sText = String.valueOf( arrCharText );
		return sText;
	}
	
	private String convertNumToWord( String sText ) {
		char[] arrCharText = sText.toCharArray();
		for ( int i = 0; i < khmerNumber.length; i++ ) {
			for ( int j = 0; j < arrCharText.length; j++ ) {
				if ( khmerNumber[i].equals( String.valueOf( arrCharText[j] ) ) ) {
					arrCharText[j] = number[i].charAt( 0 );
				}
			}
		}
		sText = String.valueOf( arrCharText );
		StringBuilder sb = new StringBuilder();
		if ( sText.isEmpty() ) {
			return "";
		}
		String dot = ".";
		String[] splitPoint = { sText };
		if ( sText.contains( dot ) ) {
			splitPoint = sText.split( "\\" + dot );
		}
		char[] arrChar = splitPoint[0].toCharArray();
		if ( arrChar.length > 7 ) {
			char[] subArray = new char[arrChar.length - 6];
			System.arraycopy( arrChar, 0, subArray, 0, subArray.length );
			sb.append( convertNumToWord( new String( subArray ) ) );
			sb.append( khmerUnitsWord.get( "7" ) );
			for ( int i = subArray.length; i < arrChar.length; i++ ) {
				int position = arrChar.length - i;
				if ( arrChar[i] != '0' ) {
					if ( position == 2 ) {
						sb.append( khmer2UnitsWord.get( String.valueOf( arrChar[i] ) ) );
					} else {
						sb.append( khmerWord.get( String.valueOf( arrChar[i] ) ) );
						sb.append( khmerUnitsWord.get( String.valueOf( position ) ) );
					}
				}
			}
		} else {
			for ( int i = 0; i < arrChar.length; i++ ) {
				int position = arrChar.length - i;
				if ( arrChar[i] != '0' ) {
					if ( position == 2 ) {
						sb.append( khmer2UnitsWord.get( String.valueOf( arrChar[i] ) ) );
					} else {
						sb.append( khmerWord.get( String.valueOf( arrChar[i] ) ) );
						sb.append( khmerUnitsWord.get( String.valueOf( position ) ) );
					}
				}
			}
		}
		if ( splitPoint.length == 2 ) {
			sb.append( wordPress[0] );
			sb.append( convertNumToWord( splitPoint[1] ) );
		}
		return sb.toString();
	}
}
