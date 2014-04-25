package moodplayer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.github.sarxos.webcam.Webcam;

class NoSubFolderList extends ArrayList<String>{
	private static final long serialVersionUID = 1L;
	
	public boolean add(String s){
		if(contains(s)) return false;
		for(int i=0;i<size();i++){
			String str=get(i);
			if(str.contains(s)){
				remove(i);
			}
			if(s.contains(str)){
				return false;
			}
		}
		return super.add(s);
	}
}

public class SettingsManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private static float version=1.0f;
	
	private static File sFile;
	public static SettingsManager sMan;
	
	private Webcam webcamSelection;
	
	private NoSubFolderList libLocations;
	
	private int playerVolume;
	
	private float minFamiliarity;
	private float minHotttness; 
	
	public SettingsManager(){
		webcamSelection=null;
		playerVolume=100;
		minFamiliarity=0.5f;
		minHotttness=0.5f;
		libLocations=new NoSubFolderList();
	}
	
	public static void init(){
		sFile = new File("settings.db");
		if (!sFile.exists()) {
			try {
				sFile.createNewFile();
				sMan = new SettingsManager();
				save();
			} catch (IOException e) {}
		}
		else{
			sMan=load();
		}
	}
	
	private static SettingsManager load(){
		try{
			FileInputStream i=new FileInputStream(sFile);
			ObjectInputStream ois=new ObjectInputStream(i);
			float version=ois.readFloat();
			if(version==SettingsManager.version){
				SettingsManager s=(SettingsManager)ois.readObject();
				ois.close();
				i.close();
				return s;
			}
			else{
				SettingsManager s=new SettingsManager();
				ois.close();
				return s;
			}
		}catch(Exception e){
			SettingsManager s=new SettingsManager();
			return s;
		}
	}

	public static boolean save() {
		try {
			FileOutputStream o = new FileOutputStream(sFile);
			ObjectOutputStream oos=new ObjectOutputStream(o);
			oos.writeFloat(version);
			oos.writeObject(sMan);
			oos.close();
			return true;
		} catch (Exception e) {return false;}
	}

	public static Webcam webcamSelection() {
		return sMan.webcamSelection;
	}
	
	public static void webcamSelection(Webcam w){
		sMan.webcamSelection=w;
	}

	public static ArrayList<String> libLocations() {
		return sMan.libLocations;
	}

	public static int playerVolume() {
		return sMan.playerVolume;
	}

	public static void playerVolume(int playerVolume) {
		sMan.playerVolume = playerVolume;
	}

	public static float minFamiliarity() {
		return sMan.minFamiliarity;
	}

	public static float minHotttness() {
		return sMan.minHotttness;
	}

}












