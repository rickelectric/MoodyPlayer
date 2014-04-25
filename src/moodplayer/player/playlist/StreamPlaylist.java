package moodplayer.player.playlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moodplayer.api.StreamDownloader;
import moodplayer.models.streamer.LocalResult;
import moodplayer.models.streamer.SeekResult;
import moodplayer.models.streamer.StreamResult;

/**
 * Stores A Playlist of Streamables That Can Be Observed From Within The Main
 * App Player.
 * 
 * @author Ionicle
 */
public class StreamPlaylist extends Observable {

	private int added, checked;
	Observer o=null;

	private ExecutorService exec;
	protected List<StreamResult> playlist;

	public StreamPlaylist() {
		super();
		exec = Executors.newFixedThreadPool(4);
		playlist = new ArrayList<StreamResult>();
		added = checked = 0;
	}

	/**
	 * 
	 * @param streams
	 *            Add These Streams To The Playlist, Checking Their Validity As
	 *            They Are Added. Streams are added asynchronously.
	 */
	public StreamPlaylist(List<StreamResult> streams) {
		this();
		addAsync(streams);
	}

	public List<StreamResult> observe() {
		while (added != checked)
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		;
		return playlist;
	}

	public int size() {
		return playlist.size();
	}

	public void addAsync(final List<StreamResult> streams) {
		exec.execute(new Runnable() {
			public void run() {
				add(streams);
			}
		});
	}

	public void add(List<StreamResult> streams) {
		for (StreamResult s : streams) {
			add(s);
		}
	}

	public boolean add(StreamResult s) {
		if (playlist.contains(s))
			return false;
		System.out.println("Adding " + s.getTitle());
		added++;
		try {
			playlist.add(s);
			if(o!=null) s.addObserver(o);
			stateChanged(playlist.size() - 1);
			if(s instanceof SeekResult){
				((SeekResult) s).loadURL();
			}
			long conn = 1L;
			if(!(s instanceof LocalResult)){
				conn=StreamDownloader.ping(s.getUrl());
			}
			checked++;
			if (conn > 0) {
				return true;
			}
			remove(s);
			return false;

		} catch (IOException e) {
			remove(s);
			checked++;
			return false;
		}
	}

	public void addAsync(final StreamResult s) {
		exec.execute(new Runnable() {
			public void run() {
				add(s);
			}
		});
	}

	public StreamResult get(int i) {
		if (i >= playlist.size())
			return null;
		return playlist.get(i);
	}

	public boolean contains(StreamResult s) {
		return playlist.contains(s);
	}

	public StreamResult remove(int i) {
		if (i >= playlist.size())
			return null;
		StreamResult pop = playlist.remove(i);
		if (pop != null) {
			stateChanged(-1);
		}
		return pop;
	}

	public boolean remove(StreamResult s) {
		if (playlist.remove(s)) {
			stateChanged(-1);
			return true;
		}
		return false;
	}

	private void stateChanged(int arg) {
		setChanged();
		notifyObservers(arg);
	}
	
	public void addObserver(Observer o){
		super.addObserver(o);
		this.o=o;
		for(StreamResult s:playlist)
			s.addObserver(o);
	}
	
}
