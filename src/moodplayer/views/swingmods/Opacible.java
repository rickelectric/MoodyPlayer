package moodplayer.views.swingmods;

public interface Opacible{
	
	public static final int 
		NONE=0,
		IN=1,
		OUT=2;
	
	public int getOscSpeed();
	public boolean isOscillating();
	public void setOscSpeed(int speed);

	public void setAlpha(float value);

    public float getAlpha();
    
    public boolean isVisible();
    
	public void setOscillating(boolean osc);
    
}
