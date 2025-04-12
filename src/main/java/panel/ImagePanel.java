package panel;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    /**
	 * -- field description (LogicalName :: Physical Name) --
	 */
	private static final long serialVersionUID = 1L;
	private Image image;

    public ImagePanel(String imagePath) {
        // Load the image
        image = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the image
        g.drawImage(image, 0, 0, this);
    }
}
