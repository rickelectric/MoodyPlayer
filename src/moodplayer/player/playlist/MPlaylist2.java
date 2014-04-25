package moodplayer.player.playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import moodplayer.api.EchoAPI;
import moodplayer.api.MP3_Interface;
import moodplayer.api.MoodTranslate;
import moodplayer.exception.MoodAccessException;
import moodplayer.models.Emotion;
import moodplayer.models.streamer.StreamResult;
import moodplayer.models.streamer.Streamable;
import moodplayer.monitor.GlobalMoodModel;
import moodplayer.user.UserData;
import moodplayer.util.UtilBox;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import com.echonest.api.v4.Song;

public class MPlaylist2 extends StreamPlaylist implements Runnable{
	
	private static List<Song> lcalm,lhappy,lsurprised,
		lsad,lconfused,langry,ldisgust;
	
	private static int[] offsets = new int[]{0,0,0,0,0,0,0};;
	
	private StreamPlaylist 
		calm,happy,surprised,
		sad,confused,angry,disgust;
	
	private boolean isRunning;
	
	private StreamPlaylist curr;
	private StreamResult playing;

	private MediaPlayer player;

	public MPlaylist2(MediaPlayer player) {
		this();
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
		playing = null;
	}

	public MPlaylist2() {
		super();
		
		calm=new StreamPlaylist();
		happy=new StreamPlaylist();
		surprised=new StreamPlaylist();
		sad=new StreamPlaylist();
		angry=new StreamPlaylist();
		confused=new StreamPlaylist();
		disgust=new StreamPlaylist();
		
		try {
			curr=getPlaylist(GlobalMoodModel.getDominantMood().getName());
		} catch (MoodAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void echoLoad() throws MoodAccessException{
		lcalm=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("calm", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("calm")]);
		lhappy=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("happy", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("happy")]);
		lsurprised=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("surprised", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("surprised")]);
		lsad=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("sad", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("sad")]);
		lconfused=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("confused", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("confused")]);
		langry=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("angry", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("angry")]);
		ldisgust=EchoAPI.echoMoodGenreGet(MoodTranslate.translateMood("disgust", 0.8), UserData.getRandomPreferredGenre(), 10, offsets[offset("disgust")]);
		for(int i=0;i<offsets.length;i++){
			offsets[i]+=10;
		}
	}
	
	private static int offset(String mood) throws MoodAccessException{
		if(mood.equals("calm")) return 0;
		if(mood.equals("happy")) return 1;
		if(mood.equals("surprised")) return 2;
		if(mood.equals("sad")) return 3;
		if(mood.equals("confused")) return 4;
		if(mood.equals("angry")) return 5;
		if(mood.equals("disgust")) return 6;
		throw new MoodAccessException();
	}
	
	public void run(){
		while(!Thread.interrupted()){
			invoke();
			try{Thread.sleep(5000);}
			catch(Exception e){}
		}
	}
	
	public void invoke(){
		if(isRunning) return;
		isRunning=true;
		scan();
		isRunning=false;
	}
	
	public void next(){
		next(GlobalMoodModel.getDominantMood().getName());
	}
	
	public void next(String mood){
		if(playing!=null){
			player.stop();
			remove(playing);
		}
		try {
			curr=getPlaylist(mood);
		} catch (MoodAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(curr.size()==0) return;
		
		StreamResult rs=curr.remove(0);
		if(rs==null) return;
		add(rs);
		playing=get(0);
		if (playing != null){
			player.playMedia(playing.getUrl());
		}
	}
	
	private StreamPlaylist getPlaylist(String mood) throws MoodAccessException {
		if(mood==null) return this;
		if(mood.equals("calm")) return calm;
		if(mood.equals("happy")) return happy;
		if(mood.equals("surprised")) return surprised;
		if(mood.equals("sad")) return sad;
		if(mood.equals("angry")) return angry;
		if(mood.equals("confused")) return confused;
		if(mood.equals("disgust")) return disgust;
		throw new MoodAccessException();
	}
	
	public static List<Song> list(String mood) throws MoodAccessException{
		if(mood==null) return null;
		if(mood.equals("calm")) return lcalm;
		if(mood.equals("happy")) return lhappy;
		if(mood.equals("surprised")) return lsurprised;
		if(mood.equals("sad")) return lsad;
		if(mood.equals("confused")) return lconfused;
		if(mood.equals("angry")) return langry;
		if(mood.equals("disgust")) return ldisgust;
		throw new MoodAccessException();
	}
	
	@SuppressWarnings("unused")
	private void partialLoad(Emotion e){
		try {
			if(list(e.getName()).size()>7) return;
			String em=MoodTranslate.emotionToMood(e);
			String eg=UserData.getRandomPreferredGenre();
			int offset=UtilBox.getRandomNumber(30);
			List<Song> L=EchoAPI.echoMoodGenreGet(em, eg, 10, offset);
			
			list(e.getName()).addAll(L);
		} catch (MoodAccessException ex) {
			ex.printStackTrace();
		}
	}
	
	private void scan(){
		try{
			Emotion[] e=GlobalMoodModel.getMoodsInOrder();
			
			for(int i=0;i<e.length;i++){
				int nLoad=1;
				switch(i){
				case 1:
					nLoad=3;
					break;
				case 2:
					nLoad=2;
					break;
				}
				
				//partialLoad(e[i]);
				push(e[i].getName(),nLoad);
			}
		}catch(MoodAccessException e){
			e.printStackTrace();
		}
	}
	
	private void push(String mood,int nLoad) throws MoodAccessException{
		List<Song> curr=list(mood);
		
		StreamPlaylist spl=getPlaylist(mood);
		String prev="";
		int j=0;
		while(curr.size()>0&&j<nLoad){
			if(spl.size()>7) break;
			Song s=curr.remove(0);
			ArrayList<Streamable> res=MP3_Interface.find(s, prev);
			prev=s.getTitle()+" "+s.getArtistName();
			if(res!=null){
				for(Streamable r:res){
					if(spl.add((StreamResult)r)){
						j++;
						break;
					}
				}
			}
		}
	}

	public int getCurrent() {
		return playlist.indexOf(playing);
	}
	
	public void addObserver(Observer o){
		super.addObserver(o);
		calm.addObserver(o);
		happy.addObserver(o);
		surprised.addObserver(o);
		sad.addObserver(o);
		angry.addObserver(o);
		confused.addObserver(o);
		disgust.addObserver(o);
	}
	

}
