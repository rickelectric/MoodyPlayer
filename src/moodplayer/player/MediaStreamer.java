package moodplayer.player;

import javax.swing.JFrame;

import moodplayer.data.DefaultParams;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.NativeLibrary;

public class MediaStreamer extends JFrame {

	private static final long serialVersionUID = 1L;

	private static AudioMediaPlayerComponent audioPlayer = null;
	private static MediaPlayer player = null;

	private static MediaPlayerFactory factory = null;
	private static EmbeddedMediaPlayer vplayer = null;

	private static AudioPlayerWin audioWin;
	private static VideoPlayerWin videoWin;

	public static void main(String[] args) {
		MediaStreamer.init();
		MediaStreamer.playVideo("http://ia600807.us.archive.org/23/items/NewWaveSelections/OingoBoingo-WeCloseOurEyesalbumVersion.mp3");
	}
	
	public static void init() {

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				DefaultParams.LIBVLC_PATH);

		if (audioPlayer == null)
			audioPlayer = new AudioMediaPlayerComponent();
		player = audioPlayer.getMediaPlayer();

		if (factory == null)
			factory = new MediaPlayerFactory();
		vplayer = factory.newEmbeddedMediaPlayer();

	}

	public static void playVideo(String mrl) {
		if(videoWin!=null){
			vplayer.stop();
			videoWin.dispose();
		}

		videoWin = new VideoPlayerWin(mrl,vplayer,factory);
		
	}
	
	public static MediaPlayer playAudio(String title,String mrl){
		MediaPlayer ret=playAudio(mrl);
		audioWin.setTitle(title);
		return ret;
	}

	public static MediaPlayer playAudio(final String mrl) {
		if (audioWin != null) {
			player.stop();
			audioWin.dispose();
		}
		
		long time=System.currentTimeMillis();
		Thread t=new Thread(new Runnable(){public void run(){player.playMedia(mrl);}});
		t.start();
		
		while (!player.isPlaying()){
			if(System.currentTimeMillis()-time>4000) throw new RuntimeException("Media Get: Connection Timeout");
			try {Thread.sleep(200);} catch (Exception e) {}
		}
		try {t.join();}
		catch (InterruptedException e) {e.printStackTrace();}
		
		audioWin = new AudioPlayerWin(player);
		
		return player;
		
	}
	
	public static void closeCurrWin(){
		if(audioWin==null) return;
		
		audioWin.close();
	}

	public static MediaPlayer getAudioPlayer() {
		if(player==null)
			return null;
		return player;
	}

}
