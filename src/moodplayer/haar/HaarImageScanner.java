package moodplayer.haar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.InputStream;
import java.util.List;

import jjil.algorithm.Gray8Rgb;
import jjil.algorithm.RgbAvgGray;
import jjil.core.Error;
import jjil.core.Image;
import jjil.core.Rect;
import jjil.core.RgbImage;
import jjil.j2se.RgbImageJ2se;
import moodplayer.data.DefaultParams;

public class HaarImageScanner {
	
	private static InputStream haarCascades(int n){
		switch(n){
		case 0:return HaarData.HCSB();
		case 1:return HaarData.frontalFaceDefault();
		case 2:return HaarData.frontalFaceAlt();
		default:return HaarData.frontalFaceAlt2();
		}
	}
	
	public static int findFaces(BufferedImage bi){
		int minScale=DefaultParams.HAAR_FACE_DETECT_MIN;
		int maxScale=DefaultParams.HAAR_FACE_DETECT_MAX;
		
		try{
			InputStream haarCascade = HaarData.HCSB();
			Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(
					haarCascade, minScale, maxScale);
			
			List<Rect> results=find(detectHaar,bi);
			return results.size();
		}catch(Throwable e){
			return -1;
		}
	}

	/**
	 * @author Ionicle
	 * @param bi The image to be scanned for faces.
	 * @param overlay true => Box will be placed around detected faces, false => Boxes only returned.</p>
	 * @return
	 */
	public static BufferedImage findFaces(BufferedImage bi,boolean overlay) {
		
		Gray8DetectHaarMultiScale detector = null;
		List<Rect> results = null;
		try {
			for(int n=0;n<4;n++){
				InputStream haarCascade = haarCascades(n);
				int minScale=DefaultParams.HAAR_FACE_DETECT_MIN;
				int maxScale=DefaultParams.HAAR_FACE_DETECT_MAX;
				detector = new Gray8DetectHaarMultiScale(
					haarCascade, minScale, maxScale);
				results=find(detector,bi);
				if(results.size()>0) break;
			}
			if (results.size() == 0)
				return null;
			Image i = detector.getFront();
			Gray8Rgb g2rgb = new Gray8Rgb();
			g2rgb.push(i);
			if(overlay){//Put A Rectangle Around Found Faces
				return overlay(bi,results);
			}
			//Return the overlay of face rectangles only
			return toImage((RgbImage) g2rgb.getFront());
		} catch (Throwable e) {
			return null;
		}
	}
	
	private static BufferedImage overlay(BufferedImage img,List<Rect> floc){
		
		Graphics2D g=(Graphics2D)img.getGraphics();
		g.setFont(new Font("CourierNew", Font.BOLD, 50));
		g.setColor(Color.green);
		int i=1;
		for(Rect r:floc){
			for(int j=-5;j<5;j++){
				Rectangle sh=new Rectangle(
					r.getTopLeft().getX()+j,
					r.getTopLeft().getY()+j,
					r.getWidth(),
					r.getHeight()
				);
				
				g.draw(sh);
				g.drawString(""+i,
					r.getTopLeft().getX()+(r.getWidth()-10)/2,
					r.getTopLeft().getY()+(r.getHeight()-10)/2
				);
			}
			i++;
		}
		return img;
	}
	
	@SuppressWarnings("unused")
	private static BufferedImage overlay(BufferedImage img,BufferedImage top){
		BufferedImage a = img.getSubimage(0, 0, img.getWidth(),
				img.getHeight());

		final int width = a.getWidth();
		int[] imgData = new int[width];
		int[] maskData = new int[width];

		for (int y = 0; y < a.getHeight(); y++) {
			a.getRGB(0, y, width, 1, imgData, 0, 1);
			top.getRGB(0, y, width, 1, maskData, 0, 1);
			for (int x = 0; x < width; x++) {
				if(imgData[x]!=0xFF000000){
					int color = imgData[x] & 0x00FFFFFF;
					int maskColor = (maskData[x] & 0x00FF00FF) << 8;
					color |= maskColor;
					imgData[x] = color;
				}
			}
			
			a.setRGB(0, y, width, 1, imgData, 0, 1);
		}
		return a;
	}
	
	private static List<Rect> find(Gray8DetectHaarMultiScale detector, BufferedImage bi) throws Error{

		RgbImage im = RgbImageJ2se.toRgbImage(bi);
		RgbAvgGray toGray = new RgbAvgGray();
		toGray.push(im);
		List<Rect> results = detector.pushAndReturn(toGray.getFront());
		return results;
	}

	private static BufferedImage toImage(RgbImage rgb) {
		BufferedImage im = new BufferedImage(rgb.getWidth(), rgb.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		DataBufferInt dbi = new DataBufferInt(rgb.getData(), rgb.getHeight()
				* rgb.getWidth());
		Raster r = Raster.createRaster(im.getSampleModel(), dbi, null);
		im.setData(r);
		return im;
	}

}