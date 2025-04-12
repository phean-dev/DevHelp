package style;

import java.awt.Graphics;

import javax.swing.JLabel;

public class PLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	public PLabel(String text) {
		super(text);
	}
	
	@Override
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );
	}
}
