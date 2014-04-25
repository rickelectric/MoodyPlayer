package moodplayer.api;

import java.util.Random;

import moodplayer.exception.MoodAccessException;
import moodplayer.models.Emotion;

public class MoodTranslate {

	static void prn(String str){
		System.out.println(str);
	}
	
	public static void main(String[] args) throws MoodAccessException{
		String m="disgust";
		double percent=0.3;
		for(int x=0;x<5;x++)
		prn(translateMood(m,percent));
	}
	
	public static final String[]
			
		happy={
			"happy","cheerful","dreamy","fun","groovy",
			"romantic","gleeful","joyous","playful",
			"bouncy","sweet","intimate","hilarious"
		},
		sad={
			"sad","melancholia","sentimental","romantic",
			"gloomy","cold","sweet","reflective"
		},
		calm={
			"calming","cool","gentle","elegant","quiet",
			"mellow","relax","light","warm","laid-back",
			"intimate","mystical","meditation","reflective",
			"soothing","fun"
		},
		surprised={
			"enthusiastic","party music","cheerful","manic",
			"playful","dramatic","lively","epic","gleeful",
			"joyous","rowdy","trippy","whimsical"
		},
		angry={
			"angry","agressive","harsh",
			"cold","dark","rebellious","intense"
		},
		confused={
			"ominous","eerie","hypnotic",
			"ambient","strange","light","intense",
			"mystical","trippy"
		},
		disgust={
			"disturbing","angst-ridden","dark","light","trippy"
		};
	
	public static String emotionToMood(Emotion e) throws MoodAccessException{
		return translateMood(e.getName(),e.getValue());
	}
	
	public static String translateMood(String mood,double percent) throws MoodAccessException{
		Random r=new Random();
		if(mood.equals("happy")){
			if(percent>0.8) return happy[0];
			return happy[r.nextInt(happy.length)];
		}
		if(mood.equals("sad")){
			if(percent>0.8) return sad[0];
			return sad[r.nextInt(sad.length)];
		}
		if(mood.equals("calm")){
			if(percent>0.8) return calm[0];
			return calm[r.nextInt(calm.length)];
		}
		if(mood.equals("surprised")){
			return surprised[r.nextInt(surprised.length)];
		}
		if(mood.equals("angry")){
			if(percent>0.7) return angry[0];
			return angry[r.nextInt(angry.length)];
		}
		if(mood.equals("confused")){
			if(percent>0.8) return confused[0];
			return confused[r.nextInt(confused.length)];
		}
		if(mood.equals("disgust")){
			return disgust[r.nextInt(disgust.length)];
		}
		throw new MoodAccessException();
	}
}
