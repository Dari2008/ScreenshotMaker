package main;

import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import jnafilechooser.api.JnaFileChooser;
import jnafilechooser.api.JnaFileChooser.Mode;
import main.copy.TransferableImage;

public class ImageEntry{

	private BufferedImage image;
	private ImageFormat imageFormat = ImageFormat.JPG;
	private String fileName = "";
	
	public ImageEntry() {
		this("", null);
	}
	
	public ImageEntry(BufferedImage img) {
		this("", img);
	}
	
	public ImageEntry(String name) {
		this(name, null);
	}
	
	public ImageEntry(String name, BufferedImage img) {
		this.image = img;
		this.fileName = name;
	}
	
	
	public void setImage(BufferedImage img) {
		image = img;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	
	public void setFileName(String name) {
		fileName = name;
	}
	
	public void setImageFormat(ImageFormat format) {
		this.imageFormat = format;
	}
	
	public void resizeTo(ImageSize size, boolean keepAspectRatio) {
		setImage(ImageResizer.resize(image, size.getWidth(), size.getHeight(), keepAspectRatio));
	}
	
	public ImageFormat getImageFormat() {
		return imageFormat;
	}
	
	public ImageSize getImageSize() {
		if(image == null)return new ImageSize(0, 0);
		return new ImageSize(image.getWidth(), image.getHeight());
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void copyToClipboard() {
		if(image == null)return;
		TransferableImage imgSel = new TransferableImage(image);
		java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
	}
	
	public void save(File file, Window owner, boolean alert) {
		if(image == null)return;
		if(!fileName.isBlank() && !fileName.isEmpty() && file == null) {
			JnaFileChooser ch = new JnaFileChooser(new File(fileName + "." + imageFormat.getFormat()));
			ch.setMode(Mode.Directories);
			ch.setMultiSelectionEnabled(false);
			ch.setTitle("Export to");
			
			boolean success = ch.showSaveDialog(owner);
			if(!success)return;
			
			File selected = ch.getSelectedFile();
			if(selected.isFile()) {
				selected = selected.getParentFile();
			}
			file = new File(selected, fileName + "." + imageFormat.getFormat());
		}else if(file == null) {
			JnaFileChooser ch = new JnaFileChooser(new File(fileName + "." + imageFormat.getFormat()));
			ch.setMode(Mode.Files);
			ch.setMultiSelectionEnabled(false);
			ch.addFilter("All Images", "jpg", "jpeg", "png");
			ch.addFilter("JPEG", "jpg", "jpeg");
			ch.addFilter("PNG", "png");
			ch.setTitle("Save Image As");
			
			boolean success = ch.showSaveDialog(owner);
			if(!success)return;
			
			File selected = ch.getSelectedFile();
			String ext = imageFormat.getFormat().toLowerCase();
			if (!selected.getName().toLowerCase().endsWith("." + ext)) {
			    selected = new File(selected.getAbsolutePath() + "." + ext);
			}
			file = selected;
		}
		
		try {
			ImageIO.write(image, imageFormat.getFormat(), new File(file, fileName + "." + imageFormat.getFormat()));
			if(alert)JOptionPane.showMessageDialog(null, "Successfully saved Image!");
		} catch (IOException e) {
			e.printStackTrace();
			if(alert)JOptionPane.showMessageDialog(null, "An error happened saving the Image!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean saveWithFormat(File outputDir, String name, ImageFormat format) {
		if(image == null)return false;
		
		try {
			ImageIO.write(image, format.getFormat(), new File(outputDir, name + "." + format.getFormat()));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static enum ImageFormat{
		PNG("png"), JPG("jpg"), JPEG("jpeg");
		
		private String format;
		
		private ImageFormat(String format) {
			this.format = format;
		}
		
		public static ImageFormat fromFileName(String name) {
			if(name.endsWith("jpg"))return JPG;
			if(name.endsWith("jpeg"))return JPEG;
			if(name.endsWith("png"))return PNG;
			return null;
		}
		
		public String getFormat() {
			return format;
		}
		
		public String getDisplayString() {
			return format.toUpperCase();
		}
		
	}
	
	public void previewImage() {
		FullSizeImageViewer imgV = new FullSizeImageViewer(image);
		imgV.setVisible(true);
	}
	
	public static class ImageSize{
		private int width;
		private int height;
		
		public ImageSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		@Override
		public String toString() {
			return width + "x" + height;
		}
		
		public static ImageSize fromString(String str) {
			String[] parts = str.split("x");
			if(parts.length != 2)return new ImageSize(0, 0);
			try {
				int w = Integer.parseInt(parts[0].trim());
				int h = Integer.parseInt(parts[1].trim());
				return new ImageSize(w, h);
			}catch(NumberFormatException e) {
				return new ImageSize(0, 0);
			}
		}
		
	}
	
}
