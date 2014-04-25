package moodplayer.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import moodplayer.api.EchoGenres;
import moodplayer.models.Genre;
import moodplayer.monitor.GlobalMoodModel;
import moodplayer.util.UtilBox;

public class UserData {
	
	private static ArrayList<GenreSelection> prefGenres;
	private static File sFile;
	
	public static void loadDefaults(){
		
	}
	
	public static void init(){
		sFile=new File("user.db");
		prefGenres=new ArrayList<GenreSelection>();
	}
	
	public static void addPreferredGenre(Genre genre,ArrayList<String> prefs){
		GenreSelection cg=null;
		if(prefGenres.size()==0){
			cg=new GenreSelection(genre);
			prefGenres.add(cg);
			Collections.sort(prefGenres);
		}
		else if(prefGenres.size()==1){
			if(prefGenres.get(0).equals(genre)){
				cg=prefGenres.get(0);
			}
			else{
				cg=new GenreSelection(genre);
				prefGenres.add(cg);
				Collections.sort(prefGenres);
			}
		}
		else{
			int loc=Collections.binarySearch(prefGenres, genre);
			if(loc<0){
				cg=new GenreSelection(genre);
				prefGenres.add(cg);
				Collections.sort(prefGenres);
			}
			else cg=prefGenres.get(loc);
		}
		for(String p:prefs)
			cg.addSubGenre(p);
	}
	
	public static void load(){
		
	}
	
	public static void save() throws IOException{
		sFile.createNewFile();
	}
	
	public static String getRandomPreferredGenre(){
		if(prefGenres.size()==0){
			String[] gen=EchoGenres.genderSpecific(GlobalMoodModel.getGender());
			if(gen==null) return EchoGenres.getRandom();
			int n=UtilBox.getRandomNumber(gen.length);
			return gen[n];
		}
		if(prefGenres.size()==1) return prefGenres.get(0).getRandomSubGenre();
		int num=UtilBox.getRandomNumber(prefGenres.size());
		return prefGenres.get(num).getRandomSubGenre();
	}

}
