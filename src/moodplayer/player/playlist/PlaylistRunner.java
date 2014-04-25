package moodplayer.player.playlist;

import moodplayer.models.streamer.StreamResult;
import moodplayer.models.streamer.Streamable;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class PlaylistRunner extends StreamPlaylist implements Runnable {

	int curr;
	private StreamResult playing;

	private MediaPlayer player;

	public PlaylistRunner(MediaPlayer player) {
		super();
		if(player!=null){
			this.player=player;
			player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

				public void finished(MediaPlayer p) {
					System.out.println("********************Finished***********************");
					playing.setState(Streamable.STOPPED);
					next();
				}

				public void playing(MediaPlayer p) {
					System.out.println("********************Playing***********************");
					playing.setState(Streamable.PLAYING);
				}

				public void paused(MediaPlayer p) {
					System.out.println("********************Paused***********************");
					playing.setState(Streamable.PAUSED);
				}

				public void stopped(MediaPlayer p) {
					System.out.println("********************Stopped***********************");
					playing.setState(Streamable.STOPPED);
				}
				
				public void error(MediaPlayer p) {
					System.out.println("********************Error***********************");
					next();
				}

			});
		}
		curr = -1;
		playing = null;
	}

	public void run() {
		while (playlist.size() < 4)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		curr=0;
		next();
	}

	public void next() {
		if(playing!=null){
			player.stop();
			remove(playing);
		}
		if(curr>size()) return;
		
		playing = playlist.get(curr);
		if (playing != null){
			player.playMedia(playing.getUrl());
		}
	}

	public void playPause() {
		if (curr == -1 || playing == null)
			return;
		if (player.isPlaying()) {
			player.pause();
			playing.setState(StreamResult.PAUSED);
		} else {
			player.play();
			playing.setState(StreamResult.PLAYING);
		}
	}

	public void stop() {
		if (curr == -1 || player == null)
			return;
		player.stop();
		playing.setState(StreamResult.STOPPED);
	}

	public int getCurrent() {
		return curr;
	}

	public void setNext(int i) {
		this.curr = i - 1;
	}
}
