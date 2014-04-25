package moodplayer.models;

import java.io.Serializable;
import java.util.ArrayList;

import moodplayer.models.streamer.Streamable;

public class APIRequest implements Serializable,Comparable<Object>{
	private static final long serialVersionUID = 1L;
	
	private String identifier;
	private ArrayList<Streamable> rs;

	public APIRequest(String id, ArrayList<Streamable> list) {
		this.identifier=id;
		this.rs=list;
	}

	public ArrayList<Streamable> getList() {
		return rs;
	}

	public void setList(ArrayList<Streamable> list) {
		this.rs=list;
	}

	public String getId() {
		return identifier;
	}
	
	@Override
	public int compareTo(Object o) {
		if(o==null) return -1;
		if(o instanceof APIRequest){
			if(identifier==null) throw new RuntimeException("Null URL Identifier in APIRequest");
			return identifier.compareTo(((APIRequest) o).getId());
		}
		else if(o instanceof String){
			return identifier.compareTo((String)o);
		}
		throw new IllegalArgumentException("Expected Either a String or an APIRequest object, not a "+o.getClass().getName());
	}
	
	@Override
	public boolean equals(Object o){
		if(identifier==null) return false;
		if(o instanceof APIRequest){
			return identifier.equals(((APIRequest) o).getId());
		}
		else if(o instanceof String){
			return identifier.equals((String)o);
		}
		throw new IllegalArgumentException("Expected Either a String or an APIRequest object, not a "+o.getClass().getName());
	}
	
}
