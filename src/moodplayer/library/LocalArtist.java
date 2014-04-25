package moodplayer.library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class LocalArtist implements Serializable,Comparable<Object>{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private ArrayList<LocalSong> songs;
	
	public LocalArtist(String name) {
		this.name = name;
		this.songs = new ArrayList<LocalSong>();
	}
	
	public LocalSong find(String song){
		if(songs.size()==0) return null;
		Collections.sort(songs);
		int loc=Collections.binarySearch(songs, song);
		if(loc<0) return null;
		if(songs.get(loc).getTitle().equals(song)){
			return songs.get(loc);
		}
		return null;
	}
	
	public boolean add(LocalSong s){
		boolean added=songs.add(s);
		if(added){
			Collections.sort(songs);
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<LocalSong> getSongs() {
		return songs;
	}

	public void setSongs(ArrayList<LocalSong> songs) {
		this.songs = songs;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof LocalArtist){
			return name.compareTo(((LocalArtist) o).name);
		}
		if(o instanceof String){
			return name.compareTo(o.toString());
		}
		throw new IllegalArgumentException("Expected a LocalArtist or a String");
	}
	
	public boolean equals(Object o){
		if(o instanceof LocalArtist){
			return name.equals(((LocalArtist) o).name);
		}
		if(o instanceof String){
			return name.equals(o.toString());
		}
		throw new IllegalArgumentException("Invalid Comparison");
	}
	
	@Override
	public String toString() {
		return "LocalArtist [" + (name != null ? "\n\t>>name=" + name + ", " : "")
				+ (songs != null ? "\n\t>>songs=\n" + songs+"\n" : "") + "\n]\n";
	}
	
	
}
