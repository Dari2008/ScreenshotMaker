package main.table.editors;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ImageEntryCopyButtonCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener{
	private JTable table;
	private int clickCountToStart = 1;
	private JButton button = new JButton("Copy to Clipboard");
	
	public ImageEntryCopyButtonCellEditor(JTable table, ActionListener listener) {
		this.table = table;
		button.addActionListener(listener);
	}
	
	@Override
	public Object getCellEditorValue() {
		return button;
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if(value instanceof JButton) {
			return button;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
        return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	@Override
	public void cancelCellEditing() {
		fireEditingCanceled();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() != KeyEvent.VK_ENTER) {
			return;
		}
		
		fireEditingStopped();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
