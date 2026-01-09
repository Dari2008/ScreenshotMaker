package main.table.editors;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import main.ImageEntry;
import main.ImageEntry.ImageFormat;

public class ImageEntryImageTypeCellEditor extends DefaultCellEditor{
	private JTable table;

	
	public ImageEntryImageTypeCellEditor(JTable table) {
		super(new JComboBox<ImageFormat>(ImageFormat.values()));
		this.table = table;
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent)anEvent).getClickCount() >= 2;
        }
        return true;
	}
	
	@Override
	public Object getCellEditorValue() {
		Object value = super.getCellEditorValue();
		if(value instanceof ImageFormat) {
			ImageFormat entry = (ImageFormat) value;
			value = entry.getDisplayString();
		}else {
			return value;
		}
		
		if(value instanceof String) {
		    int row = table.getEditingRow();
		    ImageEntry entry = (ImageEntry) table.getValueAt(row, 0);
		    entry.setImageFormat(ImageFormat.valueOf(value.toString().toUpperCase()));
			return value;
		}
		return "";
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if(value instanceof ImageFormat) {
			ImageFormat entry = (ImageFormat) value;
			value = entry.getDisplayString();
		}
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

}
