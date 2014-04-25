package moodplayer.exception;

public class MoodAccessException extends Exception{
	private static final long serialVersionUID = 1L;

	public MoodAccessException(){
		super("The Mood You Are Trying To Access Does Not Exist");
	}
	
}
