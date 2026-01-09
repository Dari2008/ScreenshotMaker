package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatDarkLaf;

import jnafilechooser.api.JnaFileChooser;
import jnafilechooser.api.JnaFileChooser.Mode;
import main.ImageEntry.ImageFormat;
import main.ImageEntry.ImageSize;
import main.ScreenshotOverlay.OnCaptureCallback;
import main.shortcuts.ShortcutManager;
import main.shortcuts.ShortcutManager.ShortcutAction;
import main.shortcuts.ShortcutSaver;
import main.shortcuts.ShortcutSettings;
import main.table.editors.ImageEntryCopyButtonCellEditor;
import main.table.editors.ImageEntryImageTypeCellEditor;
import main.table.editors.ImageEntryNameCellEditor;
import main.table.editors.ImageEntrySizeCellEditor;
import main.table.renderers.ImageEntryCopyButtonRenderer;
import main.table.renderers.ImageEntryImageTypeRenderer;
import main.table.renderers.ImageEntryNameRenderer;
import main.table.renderers.ImageEntrySizeRenderer;

public class Main extends JFrame implements ListSelectionListener, ComponentListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JCheckBox askForNameDirectly = new JCheckBox("Ask for name after taking screenshot");
	private JButton retake = new JButton("Retake");
	private JButton take = new JButton("Take screenshot");
	private JButton saveAll = new JButton("Save all");
	private JButton save = new JButton("Save");
	private JButton remove = new JButton("Remove");
	private ImageEntry lastAddedEntry = null;
	
	private ShortcutManager shortcutManager = new ShortcutManager(new ShortcutAction() {
		
		@Override
		public void onAction() {
			takeScreenshot();
			System.out.println("Shortcut triggered: Take Screenshot");
		}
	}, new ShortcutAction() {
		
		@Override
		public void onAction() {
			if(lastAddedEntry != null) {
				ScreenshotOverlay.openScreenCaptureForAllScreens(new OnCaptureCallback() {
					
					@Override
					public void capturedImage(BufferedImage img) {
						if(lastAddedEntry == null)return;
						lastAddedEntry.setImage(img);
						openSelectedHoverPanel();
					}
					
					@Override
					public void canceled() {
					}
				});
			}
		}
	});
//	private JPanel listPanel = new JPanel();
	
	private ArrayList<ImageEntry> entrys = new ArrayList<>();
	private final JTable list = new JTable() {
		public java.lang.Class<?> getColumnClass(int column) {
			switch (column) {
				case 0:
					return ImageEntry.class;
				case 1:
					return ImageSize.class;
				case 2:
					return ImageFormat.class;
				case 3:
					return JButton.class;
				default:
					return Object.class;
			}
		}
	};
	
	private ImageEntry currentSelectedEntry = null;
	private JDialog hoverImagePanel = new JDialog(this) {
		private static final long serialVersionUID = 5270446739151211852L;

		@Override
		public void paint(java.awt.Graphics gg) {
			super.paint(gg);
			Graphics2D g = (Graphics2D) gg;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			if(currentSelectedEntry != null && currentSelectedEntry.getImage() != null) {
				BufferedImage img = currentSelectedEntry.getImage();
				
				int pw = getWidth();
				int ph = getHeight();

				float scale = Math.min(
					pw / (float) img.getWidth(),
					ph / (float) img.getHeight()
				);

				int w = (int) (img.getWidth() * scale);
				int h = (int) (img.getHeight() * scale);

				int x = (pw - w) / 2;
				int y = (ph - h) / 2;

				g.drawImage(img, x, y, w, h, null);
				System.out.println("Drawing hover image: " + w + "x" + h);
			}
			
			g.setColor(new Color(100, 100, 100, 128));
			
			float width = 3f;
			g.setStroke(new BasicStroke(width));
			g.drawRect(0, 0, ((int)(getWidth() - width)), ((int)(getHeight() - width)));
			
		};
	};
	
	static {
		FlatDarkLaf.setup();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		list.setRowHeight(30);
		
		list.setModel(new DefaultTableModel(new Object[] {"Name", "Size", "Image Format", ""}, 0));
		list.setDefaultRenderer(ImageEntry.class, new ImageEntryNameRenderer());
		list.setDefaultEditor(ImageEntry.class, new ImageEntryNameCellEditor(list));

		list.setDefaultRenderer(ImageSize.class, new ImageEntrySizeRenderer());
		ImageEntrySizeCellEditor sizeEditor = new ImageEntrySizeCellEditor(list);
		sizeEditor.addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent e) {
				openSelectedHoverPanel();
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
				openSelectedHoverPanel();
			}
		});
		list.setDefaultEditor(ImageSize.class, sizeEditor);

		list.setDefaultRenderer(ImageFormat.class, new ImageEntryImageTypeRenderer());
		list.setDefaultEditor(ImageFormat.class, new ImageEntryImageTypeCellEditor(list));
		

		list.setDefaultRenderer(JButton.class, new ImageEntryCopyButtonRenderer());
		list.setDefaultEditor(JButton.class, new ImageEntryCopyButtonCellEditor(list, this::copyToClipboard));
		
		list.getSelectionModel().addListSelectionListener(this);
		
		hoverImagePanel.setUndecorated(true);
		hoverImagePanel.setResizable(false);
		
		addComponentListener(this);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JPanel actionPanel = new JPanel();
//		listPanel.setLayout(new MigLayout("", "[][grow]", "[grow]"));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(actionPanel, GroupLayout.DEFAULT_SIZE, 886, Short.MAX_VALUE)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addContainerGap(2, Short.MAX_VALUE)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 551, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(actionPanel, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
		);
		
		scrollPane.setViewportView(list);
		
		askForNameDirectly.setSelected(true);
		
		JPanel panel = new JPanel();
		
		JButton shortcutSettings = new JButton("Shortcut Settings");
		shortcutSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShortcutSettings settings = new ShortcutSettings();
				settings.setVisible(true);
				settings.addWindowListener(new java.awt.event.WindowAdapter() {
				    @Override
				    public void windowClosed(WindowEvent windowEvent) {
				        ShortcutSaver.loadData();
				    }
				});
			}
		});
		GroupLayout gl_actionPanel = new GroupLayout(actionPanel);
		gl_actionPanel.setHorizontalGroup(
			gl_actionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_actionPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_actionPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
						.addGroup(gl_actionPanel.createSequentialGroup()
							.addComponent(saveAll)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(shortcutSettings)
							.addGap(109)
							.addComponent(take, GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
							.addGap(28)
							.addComponent(askForNameDirectly, GroupLayout.PREFERRED_SIZE, 238, GroupLayout.PREFERRED_SIZE)
							.addGap(10)))
					.addContainerGap())
		);
		gl_actionPanel.setVerticalGroup(
			gl_actionPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_actionPanel.createSequentialGroup()
					.addGap(8)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_actionPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_actionPanel.createSequentialGroup()
							.addGap(26)
							.addGroup(gl_actionPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(askForNameDirectly)
								.addGroup(gl_actionPanel.createParallelGroup(Alignment.BASELINE)
									.addComponent(saveAll)
									.addComponent(shortcutSettings))))
						.addGroup(gl_actionPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(take, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		panel.add(retake);
		panel.add(save);
		panel.add(remove);
		remove.addActionListener((e)->removeScreenshot());
		save.addActionListener((e)->saveSelected());
		retake.addActionListener((e)->retakeScreenshot());
		actionPanel.setLayout(gl_actionPanel);
				take.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
				take.addActionListener((e)->takeScreenshot());
		
		contentPane.setLayout(gl_contentPane);
		saveAll.addActionListener((e)->saveAll());
		
		hasSelected(false);
	}
	
	private void copyToClipboard(ActionEvent e) {
		ImageEntry entry = (ImageEntry) list.getValueAt(list.getSelectedRow(), 0);
		if(entry == null)return;
		entry.copyToClipboard();
	}
	
	public void saveSelected() {
		ImageEntry entry = (ImageEntry) list.getValueAt(list.getSelectedRow(), 0);
		if(entry == null)return;
		entry.save(null, this, true);
	}
	
	public void previewSelectedImage() {
		ImageEntry entry = (ImageEntry) list.getValueAt(list.getSelectedRow(), 0);
		if(entry == null)return;
		entry.previewImage();
	}
	
	public void retakeScreenshot() {
		ScreenshotOverlay.openScreenCaptureForAllScreens(new OnCaptureCallback() {
			
			@Override
			public void capturedImage(BufferedImage img) {
				ImageEntry entry = (ImageEntry) list.getValueAt(list.getSelectedRow(), 0);
				if(entry == null)return;
				entry.setImage(img);
				openSelectedHoverPanel();
			}
			
			@Override
			public void canceled() {
			}
		});
	}
	
	public void takeScreenshot() {
		ScreenshotOverlay.openScreenCaptureForAllScreens(new OnCaptureCallback() {
			
			@Override
			public void capturedImage(BufferedImage img) {
				ImageEntry entry = new ImageEntry(img);
				
				if(askForNameDirectly.isSelected()) {
					NameInputDialog nInput = new NameInputDialog((name)->{
						entry.setFileName(name);
						addEntry(entry);
					});
					nInput.setVisible(true);
				}
				
			}
			
			@Override
			public void canceled() {
			}
		});
	}
	
	public void removeScreenshot() {
		ImageEntry entry = (ImageEntry) list.getValueAt(list.getSelectedRow(), 0);
		if(entry == null)return;
		removeEntry(entry);
	}
	
	public void saveAll() {
		
		JnaFileChooser ch = new JnaFileChooser();
		ch.setMultiSelectionEnabled(false);
		ch.setMode(Mode.Directories);
		boolean success = ch.showSaveDialog(this);
		if(!success)return;
		
		File selected = ch.getSelectedFile();
		if(selected == null)return;
		
		if(!selected.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Please select a valid directory!", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(!selected.exists()) {
			selected.mkdirs();
		}
		
		System.out.println(selected);
		
		for(ImageEntry entry : entrys) {
			entry.save(selected, this, false);
		}
		
		JOptionPane.showMessageDialog(this, "Successfully saved all images!");
		
	}
	
	private void hasSelected(boolean has) {
		retake.setEnabled(has);
		save.setEnabled(has);
		remove.setEnabled(has);
	}
	
	private HashMap<ImageEntry, Object[]> entryObjects = new HashMap<>();
	
	public void addEntry(ImageEntry entry) {
		entrys.add(entry);
		Object[] rowData = new Object[] {entry, entry.getImageSize(), entry.getImageFormat(), new JButton("Copy to Clipboard")};
		((DefaultTableModel)list.getModel()).addRow(rowData);
		entryObjects.put(entry, rowData);
		revalidate();
		repaint();
	}

	@SuppressWarnings("unlikely-arg-type")
	public void removeEntry(ImageEntry entry) {
		entrys.remove(entry);
		((DefaultTableModel)list.getModel()).getDataVector().remove(entryObjects.get(entry));
		revalidate();
		repaint();
	}
	
	public void clear() {
		entrys.clear();
	}

	public static enum SaveFormat{
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Object val = list.getModel().getValueAt(list.getSelectedRow(), 0);
		if(val instanceof ImageEntry) {
			ImageEntry entry = (ImageEntry)val;
			currentSelectedEntry = entry;
			openSelectedHoverPanel();
			hasSelected(true);
			repaint();
		}else {
			currentSelectedEntry = null;
			closeSelectedHoverPanel();
			hasSelected(false);
		}
		hoverImagePanel.repaint();
	}
	
	private void openSelectedHoverPanel() {
		if(currentSelectedEntry == null)return;
		BufferedImage img = currentSelectedEntry.getImage();
		
		int ph = Math.min(getHeight(), img.getHeight());

		float scale = ph / (float) img.getHeight();

		int w = (int) (img.getWidth() * scale);
		int h = (int) (img.getHeight() * scale);
		
		hoverImagePanel.setLocation(getX() + getWidth(), getY());
		hoverImagePanel.setSize(w, h);
		hoverImagePanel.setVisible(true);
	}
	
	private void closeSelectedHoverPanel() {
		hoverImagePanel.setVisible(false);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		openSelectedHoverPanel();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		openSelectedHoverPanel();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		openSelectedHoverPanel();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		closeSelectedHoverPanel();
	}
}
