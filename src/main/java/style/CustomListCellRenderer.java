package style;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class CustomListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value.toString().contains("High")) {
            component.setForeground(Color.RED);
        } else if (value.toString().contains("Medium")) {
            component.setForeground(Color.ORANGE);
        } else if (value.toString().contains("Low")) {
            component.setForeground(Color.GREEN);
        } else {
            component.setForeground(Color.BLACK);
        }
        return component;
    }
}