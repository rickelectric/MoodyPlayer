package moodplayer.monitor;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import moodplayer.api.ReKogAPI;
import moodplayer.haar.HaarImageScanner;
import moodplayer.models.APIFace;
import moodplayer.util.SettingsManager;

import com.github.sarxos.webcam.Webcam;

public class FaceMonitor {

	private static BufferedImage img;

	private static JFrame frame;
	private static JLabel panel;

	private static boolean running = false;
	private static long capTimeout;
	private static int numRequests;

	private static boolean scanner;

	private static Webcam w;
	private static Thread webcamthread;
	private static Thread capThread = null;

	private static int numFails = 0;

	public static void main(String[] args) {
		SettingsManager.init();
		init();
		//new EmotionMeter();
		showWin();
		startCapture();
	}
	
	public static void closeAllCams(){
		for(Webcam w:Webcam.getWebcams()){
			if(w.isOpen())
				w.close();
		}
	}

	public static boolean init(final JLabel l) {
		boolean i = init();
		if (i) {
			webcamthread=new Thread(webcamstream);
			webcamthread.start();
			new Thread(new Runnable() {
				public void run() {
					while(true){
						BufferedImage im=img;
						while(img==null||im==img)
							try{Thread.sleep(100);}catch(Exception e){}
						im=img;
						l.setIcon(new ImageIcon(im));
					}
					
				}
			}).start();
		}
		return i;
	}

	public static boolean init() {

		List<Webcam> wcams = Webcam.getWebcams();
		if (SettingsManager.webcamSelection() != null
				&& wcams.contains(SettingsManager.webcamSelection()))
			w = SettingsManager.webcamSelection();
		else {
			w = Webcam.getDefault();
			SettingsManager.webcamSelection(w);
			SettingsManager.save();
		}
		if (w == null) {
			JOptionPane.showMessageDialog(null,
					"Default Webcam Not Found.\nExiting...", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		Dimension[] dims = w.getViewSizes();// Get All Camera Resolutions
		Dimension max = new Dimension(500, 500);
		for (int i = dims.length - 1; i >= 0; i--) {
			w.setViewSize(dims[i]);
			if (dims[i].width <= max.width && dims[i].height <= max.height) {
				break;
			}
		}

		w.open();

		capTimeout = 1500;
		numRequests = 0;

		return true;

	}

	public static void close() {
		w.close();
	}

	public static void showWin() {
		if (frame == null) {
			frame = new JFrame();
			frame.setBounds(100, 100, 700, 500);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					hideWin();
				}
			});

			panel = new JLabel();
			img = w.getImage();
			panel.setSize(img.getWidth(), img.getHeight());
			panel.setIcon(new ImageIcon(img));

			frame.add(panel);

			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
		frame.setVisible(true);

		webcamthread = new Thread(webcamstream);
		webcamthread.start();
	}

	public static void hideWin() {
		if (frame == null)
			return;
		frame.setVisible(false);
		webcamthread.interrupt();
	}

	public static boolean isVisible() {
		return frame.isVisible();
	}

	private static Runnable webcamstream = new Runnable() {
		public void run() {
			int i = 0;
			int j = 0;
			while (true) {
				try {
					img = w.getImage();
					if (scanner) {
						img.getGraphics().drawLine(0, j, img.getWidth(), j);
						img.getGraphics().drawLine(i, 0, i, img.getWidth());

						img.getGraphics().drawLine(0, img.getHeight() - j,
								img.getWidth(), img.getHeight() - j);
						img.getGraphics().drawLine(img.getWidth() - i, 0,
								img.getWidth() - i, img.getWidth());
					}

					panel.setSize(img.getWidth(), img.getHeight());
					panel.setIcon(new ImageIcon(img));
				} catch (Exception e) {
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException iex) {
					break;
				}
				i += 5;
				i = i % img.getWidth();
				j += 5;
				j = j % img.getHeight();
			}
		}
	};

	// @SuppressWarnings("deprecation")
	public static void captureAndDetect(boolean init) {
		if (running)
			return;
		running = true;
		numRequests++;

		try {
			if (init) {
				for(int i=0;i<3;i++){
					APIFace[] f = null;
					while (f == null) {
						Thread.sleep(1000);
						BufferedImage tmp = HaarImageScanner.findFaces(img, true);
						if(tmp!=null){
							scanner=true;
							img = w.getImage();
							f = ReKogAPI.reKogFaceDetect(img);
							scanner=false;
						}
					}
					GlobalMoodModel.send(f);
				}
			} else {
				scanner = true;
				img = w.getImage();
				//Haar Cascade Classifier ensures there is a face in the image before sending to ReKognition.
				BufferedImage tmp = HaarImageScanner.findFaces(img, true);
				if (tmp != null) {
					// Adjust Scan Interval Based On Number Of Requests Actually Sent To ReKognition So Far.
					if (numRequests < 5)
						capTimeout = 5000;// 5 sec delay for the first 5 requests.
					else if (numRequests < 10)
						capTimeout = 15000;// 15 sec delay for the next 5 requests.
					else if (numRequests < 20)
						capTimeout = 5000;// 5 sec delay for the next 10 requests.
					else if (numRequests < 30){
						capTimeout = 30000;//Stop looking at face for 30 seconds,
						numRequests = 0;   //Then Reset.
					}
					//2 minute cycle + sum of times taken to send requests => approximate time taken to play a song.
					
					numFails = 0;
					
					// Display Image With Face Position on Output Panel (In The Monitor Window)
					if (panel != null)
						panel.setIcon(new ImageIcon(tmp));
					Thread.sleep(500);
					
					APIFace[] f = ReKogAPI.reKogFaceDetect(img); //ReKognition API Request
					if (f != null)
						GlobalMoodModel.send(f); //Send Result To The Mood Model
				} else {
					if (numFails > 10 && img != null) {
						numFails = 0;
						
						APIFace[] f = ReKogAPI.reKogFaceDetect(img);
						GlobalMoodModel.send(f);
					} else {
						numFails++;
					}
				}
				scanner = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		running = false;
	}

	public static void startCapture() {
		if (capThread == null)
			capThread = new Thread(capture);
		if (capThread.isAlive())
			return;
		numRequests = 0;
		capThread.start();
	}

	public static void stopCapture() {
		if (capThread == null || !capThread.isAlive())
			return;
		capThread.interrupt();
		capThread=null;
	}

	private static Runnable capture = new Runnable() {
		public void run() {
			while (!Thread.interrupted()) {
				captureAndDetect(false);
				try {
					Thread.sleep(capTimeout);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	};

}
