package moodplayer.views.swingmods;

import javax.swing.Timer;


public interface Slideable extends Opacible{
	
	public static final int
	UP=-10,
	DOWN=10,
	LEFT=-20,
	RIGHT=20;
	
	public void setSlidingThread(Thread t);
	public Thread getSlidingThread();
	
	public void setSlidingTimer(Timer t);
	
	public boolean isSliding();
	public void setSliding(boolean b);
	public int getX();
	public int getY();
	public void setLocation(int x,int i);
	public void repaint();
	
}
