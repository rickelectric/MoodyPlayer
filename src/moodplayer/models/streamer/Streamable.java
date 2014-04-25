package moodplayer.models.streamer;

import java.io.IOException;
import java.io.Serializable;

public interface Streamable extends Serializable{
	
	public enum State{
		PLAYING,PAUSED,STOPPED,IDLE
	}
	
	public static final State
	PLAYING=State.PLAYING,
	PAUSED=State.PAUSED,
	STOPPED=State.STOPPED,
	IDLE=State.IDLE;
	
	public String getTitle();
	public String getUrl();
	public boolean loadURL() throws IOException;
	public State getState();
	public void setState(State s);
	
}
