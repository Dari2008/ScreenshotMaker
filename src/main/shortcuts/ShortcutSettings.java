package main.shortcuts;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import main.shortcuts.ShortcutRecorder.OnShortcutRecorded;
import net.miginfocom.swing.MigLayout;

public class ShortcutSettings extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ShortcutSettings dialog = new ShortcutSettings();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ShortcutSettings() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[50%][50%]", "[][]"));
		{
			JLabel lblNewLabel = new JLabel("Teake Screenshot");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel, "cell 0 0,growx");
		}
		{
			JButton takeScreenshot = new JButton(ShortcutSaver.getKeyCodeFor("takeScreenshot", "Ctrl+Shift+S"));
			takeScreenshot.setName("takeScreenshot");
			takeScreenshot.addActionListener(this);
			contentPanel.add(takeScreenshot, "cell 1 0,growx");
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Retake last taken Screenshot");
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel_1, "cell 0 1,growx");
		}
		{
			JButton retakeButton = new JButton(ShortcutSaver.getKeyCodeFor("retakeButton", "Ctrl+R"));
			retakeButton.setName("retakeButton");
			retakeButton.addActionListener(this);
			contentPanel.add(retakeButton, "cell 1 1,growx");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ShortcutRecorder recorder1 = new ShortcutRecorder(new OnShortcutRecorded() {
			@Override
			public void onRecorded(String shortcut) {
				((JButton)e.getSource()).setText(shortcut);
				switch(((JButton)e.getSource()).getName()) {
					case "takeScreenshot":
						ShortcutSaver.setKeyCodeFor("takeScreenshot", shortcut);
						break;
					case "retakeButton":
						ShortcutSaver.setKeyCodeFor("retakeButton", shortcut);
						break;
				}
			}
		});
		recorder1.setModal(true);
		recorder1.setVisible(true);
	}

}
