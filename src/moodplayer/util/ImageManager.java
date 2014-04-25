package moodplayer.util;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class ImageManager{
	
	public static BufferedImage scaleImage(BufferedImage img,float percent){
		int scaleX =(int)(img.getWidth()*(percent/100f));
		int scaleY =(int)(img.getHeight()*(percent/100f));
		Image newImg = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(scaleX, scaleY, BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(newImg, 0, 0 , null);
		return buffered;
	}
	
	public static BufferedImage resizeImage(BufferedImage img,int width,int height){
		Image newImg=img.getScaledInstance(width,height,Image.SCALE_SMOOTH);
		BufferedImage buffered=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(newImg,0,0,null);
		return buffered;
	}
	
	public static boolean saveImage(BufferedImage buffered,String destpath){
		try {
			ImageIO.write(buffered,"jpg",new File(destpath));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static BufferedImage imageToBufferedImage(Image im){
		BufferedImage bi = new BufferedImage
	    		 (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}
	
	public static void openInPaint(String image){
		try {
			Runtime.getRuntime().exec(new String[]{"C:\\Windows\\system32\\mspaint.exe",image});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
