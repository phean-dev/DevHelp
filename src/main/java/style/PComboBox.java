package style;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PComboBox<E> extends JComboBox<E> {
    /**
	 * -- field description (LogicalName :: Physical Name) --
	 */
	private static final long serialVersionUID = 1L;

	public PComboBox(E[] items) {
        super(items);
        setOpaque(false);
        setBorder(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10);
        return shape.contains(x, y);
    }
}
