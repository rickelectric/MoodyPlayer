package moodplayer.models;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import org.json.JSONObject;

public class APIFace {
	
	private Rectangle boundingBox;
	private double confidence;
	private Point 
		eyeLeft,
		eyeRight,
		nose,
		mouthLeft,
		mouthRight;
	
	private Pose pose;
	
	private String[] raceNames;
	private double[] raceValues;
	
	private String[] emotionNames;
	private double[] emotionValues;
	
	private double age,smile,sex;
	private double glasses,eyeClosed,mouthOpen;

	public APIFace(JSONObject facePart){
		JSONObject rect=facePart.getJSONObject("boundingbox");
		boundingBox=new Rectangle(
			rect.getJSONObject("tl").getInt("x"),
			rect.getJSONObject("tl").getInt("y"),
			rect.getJSONObject("size").getInt("width"),
			rect.getJSONObject("size").getInt("height")
		);
		
		JSONObject el=facePart.getJSONObject("eye_left");
		eyeLeft=new Point(el.getInt("x"),el.getInt("y"));
		
		JSONObject er=facePart.getJSONObject("eye_right");
		eyeRight=new Point(er.getInt("x"),er.getInt("y"));
		
		JSONObject n=facePart.getJSONObject("nose");
		nose=new Point(n.getInt("x"),n.getInt("y"));
		
		JSONObject ml=facePart.getJSONObject("mouth_l");
		mouthLeft=new Point(ml.getInt("x"),ml.getInt("y"));
		
		JSONObject mr=facePart.getJSONObject("mouth_r");
		mouthRight=new Point(mr.getInt("x"),mr.getInt("y"));
		
		confidence=facePart.getDouble("confidence");
		
		JSONObject pObj=facePart.getJSONObject("pose");
		pose=new Pose(
			pObj.getDouble("roll"),
			pObj.getDouble("yaw"),
			pObj.getDouble("pitch")
		);
		
		JSONObject r=facePart.getJSONObject("race");
		int rlen=r.length();
		raceNames=new String[rlen];
		raceValues=new double[rlen];
		int i=0;
		@SuppressWarnings("unchecked")
		Iterator<String> rkeys=r.keys();
		while(rkeys.hasNext()){
			String key=rkeys.next();
			double value=r.getDouble(key);
			raceNames[i]=key;
			raceValues[i]=value;
			i++;
		}
		
		JSONObject e=facePart.getJSONObject("emotion");
		int elen=e.length();
		emotionNames=new String[elen];
		emotionValues=new double[elen];
		i=0;
		@SuppressWarnings("unchecked")
		Iterator<String> ekeys=e.keys();
		while(ekeys.hasNext()){
			String key=ekeys.next();
			double value=e.getDouble(key);
			emotionNames[i]=key;
			emotionValues[i]=value;
			i++;
		}
		
		age=facePart.getDouble("age");
		smile=facePart.getDouble("smile");
		glasses=facePart.getDouble("glasses");
		eyeClosed=facePart.getDouble("eye_closed");
		mouthOpen=facePart.getDouble("mouth_open_wide");
		sex=facePart.getDouble("sex");
		
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	public double getConfidence() {
		return confidence;
	}

	public Point getEyeLeft() {
		return eyeLeft;
	}

	public Point getEyeRight() {
		return eyeRight;
	}

	public Point getNose() {
		return nose;
	}

	public Point getMouthLeft() {
		return mouthLeft;
	}

	public Point getMouthRight() {
		return mouthRight;
	}

	public Pose getPose() {
		return pose;
	}

	public String[] getRaceNames() {
		return raceNames;
	}

	public double[] getRaceValues() {
		return raceValues;
	}

	public String[] getEmotionNames() {
		return emotionNames;
	}

	public double[] getEmotionValues() {
		return emotionValues;
	}

	public double getAge() {
		return age;
	}

	public double getSmile() {
		return smile;
	}

	public double getSex() {
		return sex;
	}

	public double getGlasses() {
		return glasses;
	}

	public double getEyeClosed() {
		return eyeClosed;
	}

	public double getMouthOpen() {
		return mouthOpen;
	}

}

class Pose{
	private double roll,yaw,pitch;
	
	public Pose(double roll, double yaw,double pitch){
		this.roll=roll;
		this.yaw=yaw;
		this.pitch=pitch;
	}

	public double getRoll() {
		return roll;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}
}