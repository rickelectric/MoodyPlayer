package moodplayer.library;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import moodplayer.util.SettingsManager;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

@SuppressWarnings("deprecation")
public class MediaLibrary {

	private static File sFile;

	protected static ArrayList<LocalArtist> artists;

	//private static File libFolder;
	//private static JProgressBar bar;
	private static ObjectOutputStream oos;
	private static LocalArtist localTemp;
	private static LibScanner scanner;

	private static boolean isScanning;

	private static Observer observer;

	private static Thread sThread;

	public static void main(String[] args) {
		init();
		System.out.println(artists);
		/*
		JFrame fr = new JFrame();

		bar = new JProgressBar();
		fr.setContentPane(bar);
		bar.setStringPainted(true);
		bar.setMinimumSize(new Dimension(400, 100));
		fr.setVisible(true);
		fr.setLocationRelativeTo(null);

		int cnt = libFolder.list().length;
		bar.setMaximum(cnt);
		 */
		run();
	}

	public static boolean openSaveFile() {
		if (sFile == null)
			throw new IllegalStateException("Not Yet Initialized.");
		try {
			FileOutputStream fos = new FileOutputStream(sFile);
			oos = new ObjectOutputStream(fos);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void completeSave() throws IOException {
		if (!openSaveFile())
			throw new IOException("Error Opening File");
		for (LocalArtist a : artists) {
			save(a);
		}
		closeSaveFile();
	}

	private static boolean save(LocalArtist a) {
		try {
			oos.writeObject(a);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void closeSaveFile() {
		if (oos == null)
			return;
		try {
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void load() {
		if (sFile == null)
			throw new IllegalStateException("Not Initialized");
		try {
			FileInputStream i = new FileInputStream(sFile);
			ObjectInputStream ois = new ObjectInputStream(i);
			LocalArtist a = (LocalArtist) ois.readObject();
			do {
				artists.add(a);
				try {
					a = (LocalArtist) ois.readObject();
				} catch (EOFException e) {
					a = null;
				}
			} while (a != null);
			ois.close();
		} catch (Exception e) {

		}
	}

	public static void init() {
		sFile = new File("library.db");

		artists = new ArrayList<LocalArtist>();

		if(!sFile.exists()){
			try{sFile.createNewFile();}
			catch(IOException e){}
		}
		else {load();}
	}

	public static LocalArtist find(String artist) {
		if (artists.size() == 0)
			return null;
		int loc = Collections.binarySearch(artists, artist);
		if (loc < 0)
			return null;
		if (artists.get(loc).getName().equals(artist)) {
			return artists.get(loc);
		}
		return null;
	}

	public static boolean add(LocalArtist s) {
		boolean added = artists.add(s);
		if (added) {
			Collections.sort(artists);
			return true;
		}
		return false;
	}

	public static void run() {
		
	}

	public static void add(String title, String artist, String genre,
			String path) {

		localTemp = find(artist);
		if (localTemp == null) {
			localTemp = new LocalArtist(artist);
			add(localTemp);
		}
		if (localTemp.find(title) == null)
			localTemp.add(new LocalSong(title, artist, path, genre));
	}

	/*
	 * public static boolean isRootChild(File f) { File[] files =
	 * libFolder.listFiles(); for (File l : files) { if (l.equals(f)) return
	 * true; } return false; }
	 * 
	 * public static int countFiles(File f) { if (!f.exists()) return 0; int
	 * fileCount = 0; if (f.isDirectory()) { File[] files = f.listFiles(); for
	 * (File df : files) { fileCount += countFiles(df); } return fileCount; }
	 * else { String[] namePart = f.getName().split("\\."); String ext =
	 * namePart[namePart.length - 1]; if (ext.equals("mp3")) return 1; else
	 * return 0; } }
	 */

	public static void addObserver(Observer o) {
		observer=o;
		observe();
	}
	
	public static void observe(){
		if(observer==null || scanner==null) return;
		scanner.addObserver(observer);
	}

	public static void update() {
		if (isScanning) return;
		isScanning = true;
		sThread=new Thread(new Runnable(){
			public void run(){
				for (String folder : SettingsManager.libLocations()) {
					if(Thread.interrupted()||isScanning==false) break;
					File f=new File(folder);
					scanner = new LibScanner(f);
					observe();
					scanner.run();
				}
				isScanning=false;
			}
		});
		sThread.start();
	}

	public static void stopUpdate() {
		isScanning=false;
		sThread.stop();
	}

	public static void background(boolean b) {
		scanner.silent(b);
	}

	public static void purge(String rem) {
		Iterator<LocalArtist> i=artists.iterator();
		while(i.hasNext()){
			LocalArtist a=i.next();
			Iterator<LocalSong> si=a.getSongs().iterator();
			while(si.hasNext()){
				LocalSong s=si.next();
				String path=s.getFilePath();
				if(path.contains(rem)||path.equals(rem)){
					si.remove();
				}
			}
			if(a.getSongs().size()==0) i.remove();
		}
		try {completeSave();}
		catch (IOException e){e.printStackTrace();}
	}

}

class LibScanner extends Observable implements Runnable {

	public static boolean scanning = false;

	private File libFolder;
	private File currFolder;
	
	private int numFiles;
	private int pos;

	private boolean silent;

	public LibScanner(File folder) {
		super();
		this.libFolder = folder;
		this.silent = false;
	}
	
	public String folderName(){
		return libFolder.getName();
	}
	public String currFolderName(){
		return currFolder.getName();
	}

	public void silent(boolean b) {
		this.silent = b;
	}

	public boolean isSilent() {
		return silent;
	}

	public void run() {
		if (scanning)
			return;
		try {
			scanning = true;
			numFiles = countFiles(libFolder);
			pos = 0;
			scan();
		} catch (Exception e) {}
		scanning = false;
	}

	private void stateChanged() {
		setChanged();
		notifyObservers(new int[] { pos, numFiles });
	}

	public void scan() {
		scan(libFolder);
	}

	private boolean scan(File f) {
		if (!f.exists())
			return false;
		if(Thread.interrupted()) return false;
		if (f.isDirectory()){
			//System.out.println("Folder: " + f.getName());
			File[] files = f.listFiles();
			for (File df : files) {
				if(scan(df)==false) return false;
			}
			if (pos % 10 == 0){
				if(isRootChild(f)){
					try {
						MediaLibrary.completeSave();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(silent){
					try {
						Thread.sleep(2000);
					} catch (Exception e) {}
				}else{
					try {
						Thread.sleep(200);
					} catch (Exception e) {}
				}
			}
		} else {
			try {
				currFolder=f.getParentFile();
				// System.out.println("File Attempt: \n\t-\""+f.getAbsolutePath()+"\"");
				String[] namePart = f.getName().split("\\.");
				String ext = namePart[namePart.length - 1];
				if (!ext.equals("mp3"))
					throw new RuntimeException("Not An MP3");
				Mp3File mp3track = new Mp3File(f.getAbsolutePath());
				String title = null, artist = null, genre = null;
				if (mp3track.hasId3v2Tag()) {
					ID3v2 id3 = mp3track.getId3v2Tag();
					artist = id3.getArtist();
					title = id3.getTitle();
					genre = id3.getGenreDescription();
				} else if (mp3track.hasId3v1Tag()) {
					ID3v1 id3 = mp3track.getId3v1Tag();
					artist = id3.getArtist();
					title = id3.getTitle();
					genre = id3.getGenreDescription();
				}
				if (title == null && artist == null)
					throw new RuntimeException("No Details. ID3 Error");
				else if (title == null)
					title = "";
				else if (artist == null)
					artist = "";

				MediaLibrary.add(title, artist, genre, f.getAbsolutePath());
				pos++;
				if(silent)
					try {
						Thread.sleep(200);
					} catch (Exception e) {}
			} catch (Exception e) {
				pos++;
			}
			stateChanged();
		}
		return true;
	}

	public boolean isRootChild(File f) {
		File[] files = libFolder.listFiles();
		for (File l : files) {
			if (l.equals(f))
				return true;
		}
		return false;
	}

	public static int countFiles(File f) {
		if (!f.exists())
			return 0;
		int fileCount = 0;
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File df : files) {
				fileCount += countFiles(df);
			}
			return fileCount;
		} else {
			String[] namePart = f.getName().split("\\.");
			String ext = namePart[namePart.length - 1];
			if (ext.equals("mp3"))
				return 1;
			else
				return 0;
		}
	}

}
