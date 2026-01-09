package main.table.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import main.ImageEntry;
import main.ImageEntry.ImageFormat;
import main.ImageEntry.ImageSize;

public class ImageEntryImageTypeRenderer extends DefaultTableCellRenderer{

	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		if(value instanceof ImageFormat) {
			ImageFormat entry = (ImageFormat) value;
			value = entry.getDisplayString();
			
			JLabel textField = new JLabel((String) value);
			textField.setFont(table.getFont());
			if(isSelected) {
				textField.setBackground(table.getSelectionBackground());
				textField.setForeground(table.getSelectionForeground());
			} else {
				textField.setBackground(table.getBackground());
				textField.setForeground(table.getForeground());
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
	
	
}
