package moodplayer.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moodplayer.data.DefaultParams;
import moodplayer.util.SettingsManager;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;

public class EchoAPI {

	private static String key=null;
	private static EchoNestAPI en = null;

	public static void main(String[] args) throws Exception {
		SettingsManager.init();
		init(DefaultParams.ECHONEST_API_KEY);
		List<Song> ss=echoMoodGenreGet("happy","pop",20,0);
		System.out.println(ss.size()+" Songs\n");
		for(Song s:ss)
			System.out.println(s);
	}

	public static void init(String apiKey) {
		key=apiKey;
		en = new EchoNestAPI(key);
	}
	
	public static List<Song> songSearch(String mood,String genre) throws EchoNestException {
		SongParams p=new SongParams();
		p.add("style", genre);
		p.add("song_min_hotttnesss", "0.8");
		List<Song> songs=en.searchSongs(p);
		return songs;
	}

	/*
	public static String getMoodOf(String fileloc) throws Exception {
		Mp3File song = new Mp3File(fileloc);
		String artist = null;
		if (song.hasId3v1Tag()) {
			ID3v1 id3 = song.getId3v1Tag();
			artist = id3.getArtist();
		}
		if (song.hasId3v2Tag()){
			ID3v2 id3 = song.getId3v2Tag();
			artist = id3.getArtist();
		}
		if (artist!=null) {
			Artist ar = en.newArtistByName(artist);
			List<Term> tm = ar.getTerms();
			for(Term t:tm){
				if(t.getWeight()>0.3f)
					System.out.println(t.getName()+": "+t.getWeight());
			}
		}
		//return null;
	}
	*/
	
	public static List<Song> echoMoodGenreGet(String mood,String genre,int limit,int offset) {
		if(mood==null&&genre==null) return null;
		try{
			ArtistParams aParams=new ArtistParams();
			aParams.setResults(limit);
			aParams.setStart(offset);
			aParams.setMinFamiliarity(SettingsManager.minFamiliarity());
			aParams.setMinHotttnesss(SettingsManager.minHotttness());
			aParams.add("mood", mood);
			if(genre!=null) aParams.add("genre", genre);
			aParams.add("bucket", "songs");
			
			List<Artist> artists=en.searchArtists(aParams);
			List<Song> songs=new ArrayList<Song>();
			
			for(Artist a:artists){
				List<Song> ls=a.getSongs();
				Song prevSong=null;
				for(Song s:ls){
					//Avoid Duplicates
					if(prevSong!=null && !prevSong.getTitle().equals(s.getTitle())){
						songs.add(s);
					}
					prevSong=s;
				}
			}
			Collections.shuffle(songs);
			return songs;
		}catch(Exception e){
			return null;
		}
	}

}











