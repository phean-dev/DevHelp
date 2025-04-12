package event;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class DragListener extends MouseAdapter {
    private Point initialClick;
    private JPanel parent;

    public DragListener(JPanel parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        initialClick = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Get location of Window
        int thisX = parent.getLocation().x;
        int thisY = parent.getLocation().y;

        // Determine how much the mouse moved since the initial click
        int xMoved = e.getX() - initialClick.x;
        int yMoved = e.getY() - initialClick.y;

        // Move window to this position
        int X = thisX + xMoved;
        int Y = thisY + yMoved;
        parent.setLocation(X, Y);
    }
}
