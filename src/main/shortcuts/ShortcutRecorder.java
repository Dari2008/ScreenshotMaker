package main.shortcuts;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ShortcutRecorder extends JDialog implements KeyListener{

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel currentTyped = new JLabel("Nothing recorded yet");
	private ArrayList<String> recordedKeys = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ShortcutRecorder dialog = new ShortcutRecorder(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ShortcutRecorder(OnShortcutRecorded onRecorded) {
		setBounds(100, 100, 450, 164);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.addKeyListener(this);
		contentPanel.requestFocusInWindow();
		contentPanel.setFocusable(true);
		
		JLabel lblNewLabel = new JLabel("Press keys");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
				.addComponent(currentTyped, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
		);
		currentTyped.setFont(new Font("Tahoma", Font.PLAIN, 20));
		currentTyped.setHorizontalAlignment(SwingConstants.CENTER);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(currentTyped, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
						if(onRecorded != null)onRecorded.onRecorded(getKeyboardShortcutString());
					}
				});
				{
					JButton btnNewButton = new JButton("Clear");
					btnNewButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							recordedKeys.clear();
							currentTyped.setText("Nothing recorded yet");
						}
					});
					buttonPane.add(btnNewButton);
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public static interface OnShortcutRecorded{
		void onRecorded(String shortcut);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				recordedKeys.add("Shift");
				break;
			case KeyEvent.VK_CONTROL:
				recordedKeys.add("Ctrl");
				break;
			case KeyEvent.VK_ALT:
				recordedKeys.add("Alt");
				break;
			case KeyEvent.VK_META:
				recordedKeys.add("Meta");
				break;
			default:
				recordedKeys.add(KeyEvent.getKeyText(e.getKeyCode()));
		}
		currentTyped.setText(getKeyboardShortcutString());
	}
	
	private String getKeyboardShortcutString() {
		ArrayList<String> tempKeys = new ArrayList<>();
		for(String key : recordedKeys) {
			if(tempKeys.indexOf(key) == -1) {
				tempKeys.add(key);
			}
		}
		
		recordedKeys = tempKeys;
		
		
		String[] order = new String[] {"Ctrl", "Shift", "Alt", "Meta"};
		String alphaOrder = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		recordedKeys.sort((a, b)->{
			int indexA = Arrays.asList(order).indexOf(a);
			int indexB = Arrays.asList(order).indexOf(b);
			if(indexA == -1)indexA = order.length + alphaOrder.indexOf(a.charAt(0));
			if(indexB == -1)indexB = order.length + alphaOrder.indexOf(a.charAt(0));
			return Integer.compare(indexA,  indexB);
		});
		return String.join("+", recordedKeys);
	}
	
	public static String sortKeyPress(String shortcute) {
		ArrayList<String> recordedKeys = new ArrayList<>(Arrays.asList(shortcute.split("\\+")));
		ArrayList<String> tempKeys = new ArrayList<>();
		for(String key : recordedKeys) {
			if(tempKeys.indexOf(key) == -1) {
				tempKeys.add(key);
			}
		}
		
		recordedKeys = tempKeys;
		
		
		String[] order = new String[] {"Ctrl", "Shift", "Alt", "Meta"};
		String alphaOrder = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		recordedKeys.sort((a, b)->{
			int indexA = Arrays.asList(order).indexOf(a);
			int indexB = Arrays.asList(order).indexOf(b);
			if(indexA == -1)indexA = order.length + alphaOrder.indexOf(a.charAt(0));
			if(indexB == -1)indexB = order.length + alphaOrder.indexOf(a.charAt(0));
			return Integer.compare(indexA,  indexB);
		});
		return String.join("+", recordedKeys);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
