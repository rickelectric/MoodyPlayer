package moodplayer;

import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import moodplayer.api.EchoAPI;
import moodplayer.api.MP3_Interface;
import moodplayer.api.RequestCache;
import moodplayer.api.StreamDownloader;
import moodplayer.data.DefaultParams;
import moodplayer.library.MediaLibrary;
import moodplayer.models.Emotion;
import moodplayer.monitor.FaceMonitor;
import moodplayer.monitor.GlobalMoodModel;
import moodplayer.monitor.KeyboardActivity;
import moodplayer.monitor.MouseActivity;
import moodplayer.player.MediaStreamer;
import moodplayer.player.playlist.MPlaylist2;
import moodplayer.user.UserData;
import moodplayer.util.SettingsManager;
import moodplayer.views.Splash;
import moodplayer.views.windows.GlobalMoodMonitorWindow;
import moodplayer.views.windows.PlayerWindow;
import moodplayer.views.windows.SettingsWindow;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

/**
 * Main Application Entry Point For Moody Player
 */
public class MoodyPlayer {

	private static GlobalScreen globalScreen;
	
	private static Splash splashFrame=null;
	private static GlobalMoodMonitorWindow em=null;
	private static PlayerWindow pWin=null;
	private static SettingsWindow stWin=null;
	
	private static MoodyTrayIcon mIcon=null;
	
	private static boolean splashWait=true;
	private static boolean started=false;

	public static void main(String[] args) {
		try {
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	    } catch (Exception evt) {}
		
		showSplash();
		
		MediaStreamer.init();

		splashFrame.setText("Loading Settings...");
		SettingsManager.init();
		UserData.init();
		
		// Test Connection To ReKognition & Echonest
		try {
			splashFrame.setText("Checking Connection To Echonest...");
			StreamDownloader.ping("http://echonest.com");
			splashFrame.setText("Checking Connection To ReKognition...");
			StreamDownloader.ping("http://rekognition.com");

		} catch (IOException e) {
			int i=5;
			while(i>0){
				splashFrame.setText("Error: Unable To Connect To The Internet. Exiting in "+i+"...");
				try{Thread.sleep(1000);}catch(InterruptedException x){}
				i--;
			}
			splashFrame.setVisible(false);
			while(splashFrame.isVisible()) try{Thread.sleep(100);}catch(Exception x){}
			exit();
		}
		
		//Initialize Echonest API Connection
		EchoAPI.init(DefaultParams.ECHONEST_API_KEY);
		
		//Load Initial Playlists
		try{
			MPlaylist2.echoLoad();
		}catch(Exception e){
			e.printStackTrace();
			splashFrame.setText("Error");
			exit();
		}
		
		// Check Connection To MP3 Sites
		splashFrame.setText("Checking Media Streaming Capabilities...");
		
		if (!MP3_Interface.checkSites()) {
			splashFrame.showChoice(
				"Music Streaming Is Unavilable. Only Local Music Will Be Played.",
				"Do You Wish To Continue?",
				"Yes",new Runnable(){
					public void run(){
						MoodyPlayer.splashWait=false;
					}
				},
				"No",new Runnable(){
					public void run(){
						splashFrame.setVisible(false);
						while(splashFrame.isVisible()) try{Thread.sleep(100);}catch(Exception x){}
						exit();
					}
				}
			);
			
			while(splashWait==true);
		}
		
		splashFrame.setText("Loading Music Metadata...");
		MediaLibrary.init();
		
		splashFrame.setText("Accessing Cache...");
		RequestCache.init();
		
		splashFrame.setText("Starting Mood Monitor...");
		GlobalMoodModel.init();
		
		splashFrame.setText("Facial Expression Monitor: Initial Scan...");
		splashFrame.getCameraPanel().setIcon(new ImageIcon(
			MoodyPlayer.class.getResource("/moodplayer/img/ajax-loader-48.gif"))
		);
		FaceMonitor.init(splashFrame.getCameraPanel());
		
		FaceMonitor.captureAndDetect(true);
		splashFrame.setText("Initial Face Scan Complete");
		
		FaceMonitor.startCapture();
		
		try {
			splashFrame.setText("Starting Keyboard & Mouse Monitor");
			GlobalScreen.registerNativeHook();
			globalScreen = GlobalScreen.getInstance();
			KeyboardActivity.init(globalScreen);
			MouseActivity.init(globalScreen);
		} catch (NativeHookException e) {
			e.printStackTrace();
			splashFrame.setText("Unable To Start Keyboard/Mouse Monitor");
			try{Thread.sleep(1000);}catch(InterruptedException x){}
		}
		
		splashFrame.setText("Launching Moody Player. Please Wait...");
		
		mIcon=new MoodyTrayIcon();
		mIcon.loading();
		showPlayer();
		showMeter();
		splashFrame.setVisible(false);
		mIcon.stopLoading();
		started=true;
	}

	public static void showSplash() {
		new Thread(new Runnable(){
			public void run(){
				splashFrame=new Splash();
				splashFrame.setVisible(true);
			}
		}).start();
		while(splashFrame==null||splashFrame.getOpacity()<0.8)
			try{Thread.sleep(50);}catch(Exception e){}
	}
	
	public static void showMeter(){
		if(em==null){
			em=new GlobalMoodMonitorWindow();
			em.setCamImage(splashFrame.getCameraPanel());
			GlobalMoodModel.addObserver(em);
		}
		em.setVisible(true);
	}
	
	public static void showSettings(){
		if(stWin==null){
			stWin=new SettingsWindow();
		}
		stWin.setVisible(true);
	}

	public static boolean showPlayer() {
		if(pWin==null){
			pWin=new PlayerWindow();
			GlobalMoodModel.addObserver(pWin);
			if(splashFrame.isVisible()) splashFrame.setVisible(false);
			pWin.setVisible(true);
			
			Emotion dMood=GlobalMoodModel.getDominantMood();
			pWin.load(dMood);
			return false;
		}
		else{
			pWin.setVisible(true);
			pWin.toFront();
			return true;
		}
	}

	public static boolean exit() {
		trayAlert(TRAY_MESSAGE,"Shutting Down","Exiting. Please Wait.",null);
		RequestCache.save();
		FaceMonitor.closeAllCams();
		if(started){
			try{
				MediaLibrary.completeSave();
			}catch(Exception e){
				int sel=JOptionPane.showConfirmDialog(null, "Failed To Save Library State\nExit?", "State Save Error", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(sel==JOptionPane.NO_OPTION) return false;
			}
			SettingsManager.save();
		}
		System.exit(0);
		return true;
		
	}
	
	private static enum AlertType {
		MESSAGE, WARNING, ERROR, INFO
	}

	public static final AlertType TRAY_MESSAGE = AlertType.MESSAGE,
			TRAY_WARNING = AlertType.WARNING, TRAY_ERROR = AlertType.ERROR,
			TRAY_INFO = AlertType.INFO;

	public static void trayAlert(AlertType type, String title, String msg,
			Runnable action) {
		if (mIcon == null)
			return;
		if (type == null)
			return;
		if (type == TRAY_ERROR)
			mIcon.popupError(title, msg, action);
		else if (type == TRAY_WARNING)
			mIcon.popupWarning(title, msg, action);
		else if (type == TRAY_INFO)
			mIcon.popupInfo(title, msg, action);
		else
			mIcon.popupMessage(title, msg, action);
	}

	public static void alert(String s) {
		if (mIcon != null)
			mIcon.popupMessage("Alert", s, null);
		else
			JOptionPane.showMessageDialog(null, s);
	}
	
}
