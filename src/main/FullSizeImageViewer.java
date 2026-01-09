package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FullSizeImageViewer extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage img;
	
	private final JPanel contentPanel = new JPanel() {
		protected void paintComponent(java.awt.Graphics g) {
			super.paintComponent(g);
			
			if(img == null)return;
			
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
			
		};
	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FullSizeImageViewer dialog = new FullSizeImageViewer(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FullSizeImageViewer(BufferedImage img) {
		this.img = img;
		setBounds(0, 0, img.getWidth(), img.getHeight() + 40);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setUndecorated(true);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setSize(buttonPane.getWidth(), 40);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener((e)->dispose());
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		contentPanel.setSize(img.getWidth(), img.getHeight());
		contentPanel.setMinimumSize(contentPanel.getSize());
		
		setLocationRelativeTo(null);
		contentPanel.repaint();
	}

}
