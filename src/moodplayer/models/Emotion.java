package moodplayer.models;

public class Emotion implements Comparable<Object>{
	
	private String name;
	private double value;
	
	public Emotion(String name,double value){
		this.name=name;
		this.value=value;
	}
	
	public String getName(){
		return name;
	}
	
	public double getValue(){
		return value;
	}
	
	public void setValue(double value){
		if(value>1) value=1;
		else if(value<0) value=0;
		this.value=value;
	}
	
	public String toString(){
		return "E [name: "+name+", value: "+value+"]";
	}

	@Override
	public int compareTo(Object o) {
		if(o instanceof Emotion){
			return -1*new Double(value).compareTo(new Double(((Emotion) o).value));
		}
		if(o instanceof Double){
			return -1*new Double(value).compareTo((Double) o);
		}
		throw new IllegalArgumentException();
	}
}
