package moodplayer.models.streamer;

import java.io.IOException;
import java.util.Observable;

public class StreamResult extends Observable implements Streamable {
	private static final long serialVersionUID = 1L;
	
	protected State state;
	protected String title;
	protected String url;
	protected String page;
	
	public StreamResult(String title,String url) {
		this.title=title;
		this.url=url;
		this.page=null;
		state=IDLE;
	}
	
	public void setPage(String page){
		this.page=page;
	}
	
	public String getPage(){
		return page;
	}
	
	
	public void setState(State state){
		if(this.state==state) return;
		this.state=state;
		stateChanged();
	}
	
	public State getState(){
		return state;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public boolean loadURL() throws IOException{
		return true;
	}

	@Override
	public String toString() {
		return "StreamResult ["
				+"\n\t"+ (title != null ? "title=" + title + ", " : "")
				+"\n\t"+ (url != null ? "url=" + url : "") 
				+"\n]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StreamResult other = (StreamResult) obj;
		if(other.url==null&&this.url==null) return true;
		if(other.url==null) return false;
		if(this.url==null) return false;
		if(this.url.equals(other.url)) return true;
		else if(this.title.equals(other.title)) return true;
		return false;
	}

	public void stateChanged(){
		setChanged();
		notifyObservers();
	}

}
