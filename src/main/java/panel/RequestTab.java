package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import font.DynamicFont;

public class RequestTab extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JPanel inputPanel;
	private JTextField textUrl;
	private JComboBox<String> requestMethod;
	private String[] method = { "GET","POST" };
	private JButton send;
	private JSplitPane bodyPanel;
	private JEditorPane  sendArea;
	private JEditorPane  receiveArea;
	private JScrollPane sendAreaScroll;
	private JScrollPane receiveScroll;
	private JPanel propertyPanel;
	private JComboBox<String> contentType;
	private JComboBox<String> acceptType;
	private JTextField authorization;
	public RequestTab() {
		setLayout( new BorderLayout( 1, 1 ) );
		inputPanel = new JPanel( new BorderLayout(1 ,1) );
		textUrl = new JTextField("https://jsonplaceholder.typicode.com/todos/1");
		requestMethod = new JComboBox<String>( method );
		send = new JButton( "send");
		inputPanel.add( requestMethod, BorderLayout.WEST );
		inputPanel.add( textUrl, BorderLayout.CENTER );
		inputPanel.add( send, BorderLayout.EAST );
		sendArea = new JEditorPane ();
		sendArea.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 12f ) );
		
		String[] strContentType = new String[] {"application/json","text/plain","text/html","text/css","text/javascript","application/xml","application/pdf","application/octet-stream","multipart/form-data"};
		
		propertyPanel = new JPanel(new GridLayout(0,1));
		contentType = new JComboBox<String>(strContentType);
		propertyPanel.add( propertyPanel("ContentType", contentType ));
		
		String[] strAcceptType = new String[] {"application/json","application/xml","text/html","text/plain","application/x-www-form-urlencoded","multipart/form-data"};
		acceptType = new JComboBox<>(strAcceptType);
		propertyPanel.add( propertyPanel("Accept", acceptType ));
		
		authorization = new JTextField();
		propertyPanel.add( propertyPanel("Authorization",authorization ));
		
		inputPanel.add( propertyPanel, BorderLayout.SOUTH );
		
		JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        
        popupMenu.add(cutMenuItem);
        popupMenu.add(copyMenuItem);
        popupMenu.add(pasteMenuItem);
        
        cutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendArea.cut();
            }
        });

        copyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendArea.copy();
            }
        });

        pasteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	sendArea.paste();
            }
        });
        
        
        sendArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        
		receiveArea = new JEditorPane ();
		receiveArea.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 12f ) );
		sendAreaScroll = new JScrollPane( sendArea );
		sendAreaScroll.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		sendAreaScroll.getVerticalScrollBar().setUnitIncrement( 16 );
		receiveScroll = new JScrollPane( receiveArea );
		receiveScroll.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		receiveScroll.getVerticalScrollBar().setUnitIncrement( 16 );
		bodyPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sendAreaScroll, receiveScroll);
		bodyPanel.setResizeWeight( 0.5 );
		send.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed( ActionEvent arg0 ) {
				String strReqMethod = (String) requestMethod.getSelectedItem();
				if ( strReqMethod.equals( "GET" ) ) {
					sendGet();
				} else if ( strReqMethod.equals( "POST" ) ) {
					sendPost();
				}
			}
		} );
		
		sendArea.addKeyListener( new KeyAdapter() {
			
			@Override
			public void keyPressed( KeyEvent e ) {
				if ( e.isControlDown() ) {
					if ( e.getKeyCode() == KeyEvent.VK_F ) {
						String sJson = sendArea.getText().trim();
						JsonParser parser = new JsonParser();
						JsonObject jsonObject = parser.parse( sJson ).getAsJsonObject();
						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						sJson = gson.toJson( jsonObject );
						sendArea.setText( sJson );
					}
				}
			};
		} );
		
		Action send = new AbstractAction("Close Action") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String strReqMethod = (String) requestMethod.getSelectedItem();
				if ( strReqMethod.equals( "GET" ) ) {
					sendGet();
				} else if ( strReqMethod.equals( "POST" ) ) {
					sendPost();
				}
			}
		};
		
		add( inputPanel, BorderLayout.NORTH );
		add( bodyPanel, BorderLayout.CENTER );
	}
	
	private JPanel propertyPanel(String labelName, JComponent comValue) {
		JPanel propPanel = new JPanel(new GridLayout(1,2));
		JLabel lbl = new JLabel();
		lbl.setText( labelName );
		propPanel.add( lbl );
		propPanel.add( comValue );
		
		return propPanel;
	}
	
	private void sendGet() {
		String strUrl = textUrl.getText().trim();
		String strReqMethod = (String) requestMethod.getSelectedItem();
		try {
			URL url = new URL( strUrl );
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod( strReqMethod );
			int status = con.getResponseCode();
			BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ( ( inputLine = in.readLine() ) != null ) {
				content.append( inputLine );
			}
			in.close();
			con.disconnect();
			String contentType = con.getContentType();
			if ( contentType.equals( "text/html;charset=UTF-8" ) ) {
				receiveArea.setContentType("text/html");
				receiveArea.setText( content.toString() );
			} else {
				receiveArea.setContentType("text/plain");
				String sJson = content.toString().trim();
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse( sJson ).getAsJsonObject();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				sJson = gson.toJson( jsonObject );
				receiveArea.setText( sJson );
			}
		} catch ( IOException e ) {
			e.printStackTrace();
			JOptionPane.showMessageDialog( this, e.getMessage() );
		}
	}
	
	private void sendPost() {
		String strUrl = textUrl.getText().trim();
		URL url;
		try {
			
			String strContentType = (String) contentType.getSelectedItem();
			String strAccept = (String) acceptType.getSelectedItem();
			String strAuthorization = authorization.getText();
			url = new URL( strUrl );
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod( "POST" );
			con.setRequestProperty( "Content-Type", strContentType );
			con.setRequestProperty( "Accept", strAccept );
			con.setRequestProperty( "Authorization", strAuthorization );
			con.setDoOutput( true );
			String jsonInputString = sendArea.getText();
			if(jsonInputString.isEmpty()) {
				return;
			}
			String sJson = jsonInputString.trim();
			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse( sJson ).getAsJsonObject();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			sJson = gson.toJson( jsonObject );
			sendArea.setText( sJson );
			try ( OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes( "utf-8" );
				os.write( input, 0, input.length );
			}
			int code = con.getResponseCode();
			String contentType = con.getContentType();
			if ( 200 == code ) {
				BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream(), "utf-8" ) );
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ( ( inputLine = in.readLine() ) != null ) {
					content.append( inputLine );
				}
				receiveArea.setText( content.toString() );
				if ( contentType.equals( "text/html;charset=UTF-8" ) ) {
					receiveArea.setContentType("text/html");
					receiveArea.setText( content.toString() );
				} else {
					receiveArea.setContentType("text/plain");
					sJson = content.toString().trim();
					jsonObject = parser.parse( sJson ).getAsJsonObject();
					sJson = gson.toJson( jsonObject );
					receiveArea.setText( sJson );
				}
				in.close();
			} else {
				JOptionPane.showMessageDialog( this, con.getResponseMessage() );
			}
			con.disconnect();
		} catch ( JsonSyntaxException e ) {
			JOptionPane.showMessageDialog( this, e.getMessage() );
		} catch ( MalformedJsonException e ) {
			JOptionPane.showMessageDialog( this, e.getMessage() );
		} catch ( IOException e ) {
			e.printStackTrace();
			JOptionPane.showMessageDialog( this, e.getMessage() );
		}
	}
}
