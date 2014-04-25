package moodplayer.user;

import java.io.Serializable;
import java.util.ArrayList;

import moodplayer.api.EchoGenres;
import moodplayer.models.Genre;
import moodplayer.util.UtilBox;

public class GenreSelection implements Serializable,Comparable<Object> {
	private static final long serialVersionUID = 1L;
	
	private Genre genre;
	private ArrayList<String> subGenres;
	
	public GenreSelection(Genre genre){
		this.genre=genre;
		subGenres=new ArrayList<String>();
	}
	
	public boolean addSubGenre(String sub){
		String[] terms=EchoGenres.getGenreTerms(genre);
		for(String s:terms){
			if(s.equals(sub)){
				return subGenres.add(sub);
			}
		}
		return false;
	}
	
	public ArrayList<String> getSubGenres(){
		return subGenres;
	}

	public Genre getGenre() {
		return genre;
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof GenreSelection){
			return genre.compareTo(((GenreSelection) o).genre);
		}
		if(o instanceof Genre){
			return genre.compareTo((Genre)o);
		}
		throw new IllegalArgumentException();
	}

	public String getRandomSubGenre() {
		if(subGenres.size()==0) return null;
		if(subGenres.size()==1) return subGenres.get(0);
		int num=UtilBox.getRandomNumber(subGenres.size());
		return subGenres.get(num);
	}
	
}
