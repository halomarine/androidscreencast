package net.srcz.android.screencast.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

import com.android.ddmlib.Device;
import com.android.ddmlib.RawImage;

public class JPanelScreen extends JPanel {


	private BufferedImage image;
	private Dimension size;
	private Device device;
	public float coef = 1;
	double origX;
	double origY;
	
	public JPanelScreen(Device device) {
		image = null;
		size = new Dimension();
		this.device = device;
		
	}
	public Dimension getPreferredSize() {
		return size;
	}


	public void pollForever() {
		do {
			try {
				fetchImage();
			} catch (IOException e) {
				System.err.println((new StringBuilder()).append(
						"Exception fetching image: ").append(e.toString())
						.toString());
			}
			/*
			try {
				Thread.sleep(0L);
			} catch (Throwable t) {
			}
			*/
		} while (true);
	}


	
	private void fetchImage() throws IOException {
		RawImage rawImage = device.getScreenshot();

		if (rawImage != null && rawImage.bpp == 16) {
				/*
			 PaletteData palette = new PaletteData(0xf800, 0x07e0, 0x001f);
			 ImageData imageData = new ImageData(rawImage.width, rawImage.height,rawImage.bpp, palette, 1, rawImage.data);
			 return new Image(getParent().getDisplay(), imageData);
			 */
			 //int scanline[] = new int[rawImage.width];
			/*
			byte buffer[] = rawImage.data;
			int index = 0;
			for (int y = 0; y < rawImage.height; y++) {
				for (int x = 0; x < rawImage.width; x++) {
					int value = buffer[index++] & 0xff;
					value |= buffer[index++] << 8 & 0xff00;
					int r = (value >> 11 & 0x1f) << 3;
					int g = (value >> 5 & 0x3f) << 2;
					int b = (value & 0x1f) << 3;
					value = 0xFF << 24 | r << 16 | g << 8 | b;
					//scanline[x] = 0xff000000 | r << 16 | g << 8 | b;
					image.setRGB(x, y, value);
				}
			}
			*/
			display(rawImage.data,rawImage.width,rawImage.height);
		} else {
			System.err.println("Received invalid image");
		}
	}

	public void display(byte[] buffer, int width, int height) {
		if(image == null)
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		size.setSize(image.getWidth(), image.getHeight());
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int value = buffer[index++] & 0xff;
				value |= buffer[index++] << 8 & 0xff00;
				/*
				int r = ( value & 0xf800) >> 8;
				int g = (value & 0x7e0) >> 3;
				int b = (value & 0x1f) << 3;
				*/
				int r = (value >> 11 & 0x1f) << 3;
				int g = (value >> 5 & 0x3f) << 2;
				int b = (value & 0x1f) << 3;
				
				value = 0xFF << 24 | r << 16 | g << 8 | b;
				//scanline[x] = 0xff000000 | r << 16 | g << 8 | b;
				image.setRGB(x, y, value);
			}
		}
		repaint();
	}
	
	public Point getRawPoint(Point p1) {
		Point p2 = new Point();
		p2.x = (int)((p1.x - origX)/coef);
		p2.y = (int)((p1.y - origY)/coef);
		return p2;
	}
	
	protected void paintComponent(Graphics g) {
		if(size.height == 0)
			return;
		double width = Math.min(getWidth(), size.width*getHeight()/size.height);
		coef = (float)width / (float)size.width;
		//System.out.println(getWidth() + " / " + width);
		double height = width*size.height/size.width;
		origX = (getWidth() - width) / 2;
		origY = (getHeight() - height) / 2;
		g.drawImage(image, (int)origX, (int)origY, (int)width, (int)height, this);
	}
	
	
}