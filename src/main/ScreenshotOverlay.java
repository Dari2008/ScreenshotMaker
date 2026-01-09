package main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ScreenshotOverlay extends JFrame implements MouseMotionListener, MouseListener, KeyListener{
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private BufferedImage img;
	private int currentX = -1;
	private int currentY = -1;
	private int currentW = -1;
	private int currentH = -1;
	private int startX = -1;
	private int startY = -1;
	private OnCaptureCallback callback;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScreenshotOverlay frame = new ScreenshotOverlay(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, null);
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
	public ScreenshotOverlay(int x, int y, int w, int h, OnCaptureCallback callback) {
		this.callback = callback;
		setBounds(x, y, w, h);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setUndecorated(true);
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
//		setAlwaysOnTop(true);
		
		imagePanel.setBounds(0, 0, getWidth(), getHeight());
		add(imagePanel);
		
		try {
			Robot robot = new Robot();
			img = robot.createScreenCapture(new Rectangle(x, y, w, h));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private JPanel imagePanel = new JPanel() {
			protected void paintComponent(Graphics gg) {
				super.paintComponent(gg);
				Graphics2D g = (Graphics2D)gg;
				g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

				if(currentX == -1 || currentY == -1 || currentW == -1 || currentH == -1) {
					g.setColor(new Color(0, 0, 0, 100));
					g.fillRect(0, 0, getWidth(), getHeight());
					return;
				}
				
				Rectangle rect1 = new Rectangle(0, 0, currentX, getHeight());
				Rectangle rect2 = new Rectangle(currentX, 0, getWidth() - currentX, currentY);
				Rectangle rect3 = new Rectangle(currentX + currentW, currentY, getWidth() - (currentX + currentW), getHeight() - currentY);
				Rectangle rect4 = new Rectangle(currentX, currentY + currentH, currentW, getHeight() - (currentY + currentH));

				g.setColor(new Color(0, 0, 0, 100));
				g.fill(rect1);
				g.fill(rect2);
				g.fill(rect3);
				g.fill(rect4);
				
			};
	};

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		currentX = Math.min(startX, x);
		currentY = Math.min(startY, y);
		currentW = Math.abs(x - startX);
		currentH = Math.abs(y - startY);

		imagePanel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startX = e.getX();
		startY = e.getY();

		currentX = startX;
		currentY = startY;
		currentW = 0;
		currentH = 0;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		startX = -1;
		startY = -1;
		if(currentW <= 10 && currentH <= 10) {
			currentX = -1;
			currentY = -1;
			currentW = -1;
			currentH = -1;
			imagePanel.repaint();
			return;
		}
		if(callback != null) {
			callback.capturedImage(img.getSubimage(currentX, currentY, currentW, currentH));
		}
		dispose();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	public static void openScreenCaptureForAllScreens(OnCaptureCallback callback) {
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		ScreenshotOverlay[] overlays = new ScreenshotOverlay[devices.length];
		int i = 0;
		
		OnCaptureCallback c = new OnCaptureCallback() {
			
			@Override
			public void capturedImage(BufferedImage img) {
				callback.capturedImage(img);
				for(ScreenshotOverlay o : overlays)o.dispose();
			}

			@Override
			public void canceled() {
				callback.canceled();
				for(ScreenshotOverlay o : overlays)o.dispose();
			}
			
			
		};
		
		for(GraphicsDevice device : devices) {
			 Rectangle r = device.getDefaultConfiguration().getBounds();
			 overlays[i] = new ScreenshotOverlay(r.x, r.y, r.width, r.height, c);
			 overlays[i].setVisible(true);
			 i++;
		}
	}
	
	public static interface OnCaptureCallback {
		public void capturedImage(BufferedImage img);
		public void canceled();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(callback != null)callback.canceled();
			dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
