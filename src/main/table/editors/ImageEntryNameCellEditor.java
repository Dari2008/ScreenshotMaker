package main.table.editors;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import main.ImageEntry;

public class ImageEntryNameCellEditor extends DefaultCellEditor{
	private JTable table;
	
	public ImageEntryNameCellEditor(JTable table) {
		super(new JTextField());
		this.table = table;
	}
	
	@Override
	public Object getCellEditorValue() {
		Object value = super.getCellEditorValue();
		if(value instanceof ImageEntry) {
			ImageEntry entry = (ImageEntry) value;
			value = entry.getFileName();
		}else {
			return value;
		}
		
		if(value instanceof String) {
		    int row = table.getEditingRow();
		    ImageEntry entry = (ImageEntry) table.getValueAt(row, 0);
		    entry.setFileName(value.toString());
			return value;
		}
		return "";
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if(value instanceof ImageEntry) {
			ImageEntry entry = (ImageEntry) value;
			value = entry.getFileName();
		}
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

}
