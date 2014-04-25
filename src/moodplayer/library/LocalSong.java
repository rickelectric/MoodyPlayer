package moodplayer.library;

import java.io.Serializable;

public class LocalSong implements Serializable,Comparable<Object> {
	private static final long serialVersionUID = 1L;
	
	private String
		title,
		artist,
		filePath,
		genre;

	public LocalSong(String title, String artist, String filePath, String genre) {
		this.title = title;
		this.artist = artist;
		this.filePath = filePath;
		this.genre = genre;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof LocalSong){
			return title.toLowerCase().compareTo(((LocalSong) o).title.toLowerCase());
		}
		else if(o instanceof String){
			return title.toLowerCase().compareTo(o.toString().toLowerCase());
		}
		throw new IllegalArgumentException("Expected A LocalSong or a String, not a "+o.getClass().getName());
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof LocalSong){
			return title.toLowerCase().equals(((LocalSong) o).title.toLowerCase());
		}
		else if(o instanceof String){
			return title.toLowerCase().equals(o.toString().toLowerCase());
		}
		throw new IllegalArgumentException("Invalid Comparison");
	}

	@Override
	public String toString() {
		return "LocalSong [" + (title != null ? "\n\t>>title=" + title + ", " : "")
				+ (artist != null ? "\n\t>>artist=" + artist + ", " : "")
				+ (filePath != null ? "\n\t>>filePath=" + filePath + ", " : "")
				+ (genre != null ? "\n\t>>genre=" + genre : "") + "\n]\n";
	}
	
	
	
}
