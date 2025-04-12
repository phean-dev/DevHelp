package panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;

import font.DynamicFont;
import style.RoundedBorder;

public class MessagePanel extends JPanel {
	 /**
	 * -- field description (LogicalName :: Physical Name) --
	 */
	private static final long serialVersionUID = 1L;
	private JTextField messageText;

	    public MessagePanel(String message, int r1, int r2) {
	        setLayout(new BorderLayout());
	        messageText = new JTextField(message);
	        messageText.setOpaque(false);
	        messageText.setFont( DynamicFont.getFont( "NotoSansKhmer-Regular").deriveFont( 12f ));
	        messageText.setBorder( new RoundedBorder( 8 ));
	        messageText.setBackground( this.getBackground() );
	        messageText.setForeground(new Color(126,186,211));
	        messageText.setEditable( false );
	        add(messageText, BorderLayout.CENTER);
	        setOpaque(false);
	    }

	    @Override
	    public Dimension getPreferredSize() {
	        return new Dimension(messageText.getPreferredSize().width, messageText.getPreferredSize().height);
	    }
	    
	    public void setTextColor(Color color) {
	    	messageText.setForeground( color );
	    }
	    
	    public void setUnderscoreLabel() {
	    	messageText.setText( "<html><u>"+ messageText.getText() +"</u></html>");
	    }
	    public void removeUnserScroceLabel() {
	    	String message = messageText.getText();
	    	message = message.replaceAll( "<html><u>", "" );
	    	message = message.replaceAll( "</u></html>", "" );
	    	messageText.setText( message );
	    }
	    
	    public JTextField getMessageText() {
	    	return messageText;
	    }
}
