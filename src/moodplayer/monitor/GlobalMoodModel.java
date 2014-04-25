package moodplayer.monitor;

import java.util.Observable;
import java.util.Observer;
import java.util.PriorityQueue;

import moodplayer.exception.MoodAccessException;
import moodplayer.models.APIFace;
import moodplayer.models.Emotion;

public class GlobalMoodModel {

	private static Emotion[] moods;
	private static PriorityQueue<Emotion> pqMoods;
	
	private static double gender;
	
	private static Observable obs;
	
	public static void stateChanged(){
		obs.notifyObservers();
	}
	
	public static void addObserver(Observer o){
		obs.addObserver(o);
	}

	public static void init() {
		obs=new AutoObservable();
		
		moods=new Emotion[]{new Emotion("calm",0),new Emotion("happy",0),
			  new Emotion("surprised",0),new Emotion("sad",0),
			  new Emotion("confused",0),new Emotion("angry",0),
			  new Emotion("disgust",0)
		};
		
		pqMoods=new PriorityQueue<Emotion>();
		pqReload();
		stateChanged();
		
		gender=0.5f;
	}
	
	private static void pqReload(){
		pqMoods.clear();
		for(Emotion e:moods){
			pqMoods.add(e);
		}
		stateChanged();
	}
	
	public static void send(APIFace[] faces){
		if(faces==null) return;
		for(int i=0;i<faces.length;i++){
			APIFace f=faces[i];
			send(f);
		}
	}

	public static void send(APIFace f) {
		if(f==null) return;
		String[] names=f.getEmotionNames();
		double[] vals=f.getEmotionValues();
		
		double tmp=(f.getSex()*5);
		gender=tmp/6;
		
		if(f.getSmile()>0.6){
			//Smiling => +Happy, +Surprised
			moods[1].setValue(moods[1].getValue()+0.05);
			moods[2].setValue(moods[2].getValue()+0.05);
		}
		if(f.getEyeClosed()>0.6){
			//Eyes Closed / Barely Open => +Calm, +Sad, -Surprised
			moods[0].setValue(moods[0].getValue()+0.05);
			moods[3].setValue(moods[3].getValue()+0.05);
			moods[1].setValue(moods[1].getValue()-0.05);
			
		}
		
		for(int x=0;x<names.length;x++){
			boolean isHere=false;
			for(int y=0;y<moods.length;y++){
				if(names[x].equals(moods[y].getName())){
					isHere=true;
					if(moods[y].getValue()==0){
						moods[y].setValue(vals[x]);
					}
					else{
						double temp=moods[y].getValue()+(vals[x]*5);
						moods[y].setValue(temp/6);
					}
					break;
				}
			}
			if(!isHere){
				moods[x].setValue(moods[x].getValue()/6);
			}
			pqReload();
		}
		stateChanged();
	}

	public static void send(KeyboardActivity k) {
		int kVal=k.getKeyValue();
		if(kVal==0) return;
		//Not Yet Implemented
		stateChanged();
	}

	public static void send(MouseActivity m) {
		int mVal=m.getMouseVal();
		double dv=(mVal/200f);
		if(mVal>0){
			moods[0].setValue(moods[0].getValue()+dv);
			moods[1].setValue(moods[1].getValue()+dv);
			moods[2].setValue(moods[2].getValue()+dv);
		}
		else if(mVal==0) moods[4].setValue(moods[4].getValue()+0.05);
		else{
			moods[3].setValue(moods[3].getValue()+dv);
			moods[5].setValue(moods[5].getValue()+dv);
			moods[6].setValue(moods[6].getValue()+dv);
		}
		pqReload();
	}
	
	public static Emotion[] getMoodsInOrder(){
		try{
			Emotion[] e=new Emotion[7];
			int i=0;
			while(i<7&&!pqMoods.isEmpty()){
				e[i]=pqMoods.remove();
				i++;
			}
			return e;
		}catch(Exception e){
			return moods;
		}
	}

	public static Emotion[] getAllMoods() {
		return moods;
	}
	
	public static Emotion getMood(String mood) throws MoodAccessException {
		for (int i = 0; i < moods.length; i++) {
			if (moods[i].getName().equals(mood)) {
				return moods[i];
			}
		}
		throw new MoodAccessException();
	}

	public static Emotion getDominantMood() {
		return pqMoods.peek();
	}
	
	public static double getGender(){
		return gender;
	}

}

class AutoObservable extends Observable{
	public void notifyObservers(){
		setChanged();
		super.notifyObservers();
	}
	public void notifyObservers(Object o){
		setChanged();
		super.notifyObservers(o);
	}
};
