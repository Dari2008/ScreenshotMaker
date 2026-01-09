package main.table.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

import main.ImageEntry;
import main.ImageEntry.ImageSize;

public class ImageEntrySizeCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener{
	private JTable table;
	JPanel panel = new JPanel();
	private JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
	private JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));

	private int clickCountToStart = 2;
	
	public ImageEntrySizeCellEditor(JTable table) {
		this.table = table;
		
		panel.add(new JLabel("Width:"));
		panel.add(widthSpinner);
		panel.add(new JLabel("Height:"));
		panel.add(heightSpinner);
		panel.setPreferredSize(new Dimension(200, 50));
		
		widthSpinner.addKeyListener(this);
		heightSpinner.addKeyListener(this);
	}
	
	@Override
	public Object getCellEditorValue() {
		String value = widthSpinner.getValue() + " x " + heightSpinner.getValue();
		
		if(value instanceof String) {
		    int row = table.getEditingRow();
		    ImageEntry entry = (ImageEntry) table.getValueAt(row, 0);
		    ImageSize newSize = ImageSize.fromString((String) value);
		    
		    
		    int keepaspect = JOptionPane.showConfirmDialog(table, "Keep aspect ratio when resizing?", "Resize Image", JOptionPane.YES_NO_OPTION);
		    if(keepaspect == JOptionPane.CLOSED_OPTION) {
		    	return entry.getImageSize();
		    }
		    
		    boolean keepAspectRatio = (keepaspect == JOptionPane.YES_OPTION);
		    
		    ImageSize original = entry.getImageSize();
		    
		    if(keepAspectRatio) {
		    	if(newSize.getWidth() != original.getWidth()) {
		    		int calculatedHeight = (newSize.getWidth() * original.getHeight()) / original.getWidth();
		    		newSize = new ImageSize(newSize.getWidth(), calculatedHeight);
		    		heightSpinner.getModel().setValue(calculatedHeight);
		    	} else if(newSize.getHeight() != original.getHeight()) {
		    		int calculatedWidth = (newSize.getHeight() * original.getWidth()) / original.getHeight();
		    		newSize = new ImageSize(calculatedWidth, newSize.getHeight());
		    		widthSpinner.getModel().setValue(calculatedWidth);
		    	}
		    }
		    
		    entry.resizeTo(newSize, keepAspectRatio);
			return newSize;
		}
		return value;
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if(value instanceof ImageSize) {
			ImageSize entry = (ImageSize) value;
			value = entry.toString();
			widthSpinner.getModel().setValue(entry.getWidth());
			heightSpinner.getModel().setValue(entry.getHeight());
			return panel;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
        };
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
