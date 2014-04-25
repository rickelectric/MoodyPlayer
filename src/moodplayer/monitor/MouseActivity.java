package moodplayer.monitor;

import java.awt.Point;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

public class MouseActivity implements NativeMouseListener,
		NativeMouseInputListener, NativeMouseMotionListener, Runnable {

	public static GlobalScreen gscr;
	public static MouseActivity a;
	public static Thread activeThread;
	public static Thread sendingThread;

	public static void main(String[] args) throws NativeHookException {
		GlobalScreen.registerNativeHook();
		if (GlobalScreen.isNativeHookRegistered()) {
			init(GlobalScreen.getInstance());
		}
		else{
			System.out.print("Error Registering Hook.");
		}
	}

	public static void init(GlobalScreen gInst) {
		gscr = gInst;
		a = new MouseActivity();
		
		gscr.addNativeMouseListener(a);
		gscr.addNativeMouseMotionListener(a);

		activeThread = new Thread(a);
		activeThread.start();
		
		sendingThread=new Thread(new Runnable(){
			public void run(){
				try{
					while(true){
						Thread.sleep(10000);
						GlobalMoodModel.send(a);
					}
				}catch(InterruptedException e){}
			}
		});
		sendingThread.start();
	}

	private int count, mouseVal, clickVal, shakeVal, sPixels, speedVal, holdVal;

	private Point prev;
	private Point[] shakeValue;
	
	private Thread speedThread;

	private long/* prevTime,*/ pressTime;
	private int[] speed;
	private int i, j;

	public MouseActivity() {
		super();
		count = 0;
		mouseVal = 0;
		clickVal = 0;
		shakeVal = 0;
		speedVal = 0;
		holdVal = 0;
		sPixels = 0;
		i = 0;
		j = 0;
		prev = null;

		shakeValue = new Point[500];
		speed = new int[7];

		for (int i = 0; i < 7; i++) {
			speed[i] = 0;
		}

		//prevTime = System.currentTimeMillis();

	}

	public int getMouseVal() {
		return mouseVal;
	}

	// Burst Clicks
	public void nativeMouseClicked(NativeMouseEvent e) {
		System.out.println("Mouse Clicked");
		count = e.getClickCount();
		if (count == 1 || count == 2){
			clickVal = 2;
		} else if (count == 3 || count == 4) {
			System.out.println("Count 3/4");
			clickVal = -1;
		} else if (count == 5) {
			System.out.println("Count 5");
			clickVal = -2;
		} else if (count == 6) {
			System.out.println("Count 6");
			clickVal = -3;
		} else if (count == 7) {
			System.out.println("Count 7");
			clickVal = -4;
		} else if (count == 8) {
			System.out.println("Count 8");
			clickVal = -5;
		} else if (count > 8) {
			System.out.println("Count >8");
			clickVal = -6;
		}
	}

	public void nativeMousePressed(NativeMouseEvent e) {
		pressTime = System.currentTimeMillis();
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
		long holdTime = System.currentTimeMillis() - pressTime;
		// Acceptable Mouse Button Down Time = 4s (4000ms)
		// >4000 => -ve Mood Value.
		if (holdTime <= 4000)
			holdVal = holdVal + 2;
		else if (holdTime > 4000 && holdTime <= 8000)
			holdVal = holdVal - 2;
		else
			holdVal = holdVal - 3;
		if(holdVal > 10) holdVal=10;
		if(holdVal < -10) holdVal=-10; 
	}

	public synchronized void nativeMouseMoved(NativeMouseEvent e) {
		//Mouse Speed => Count Pixels
		if(prev==null) prev = e.getPoint();
		else{
			Point curr=e.getPoint();
			int pixelsX=Math.abs(curr.x-prev.x);
			int pixelsY=Math.abs(curr.y-prev.y);
			
			double dist = Math.sqrt(
				(pixelsX*pixelsX) + 
				(pixelsY*pixelsY)
			);
			sPixels+=dist;
			//System.out.println("Current Distance: "+sPixels);
			prev = curr;
		}
		
		// Mouse Shaking
		shakeValue[j] = e.getPoint();
		j++;

		if (j == 201) {
			if (shakeValue[0].getX() < shakeValue[50].getX()
					&& shakeValue[100].getX() < shakeValue[50].getX()
					&& shakeValue[100].getX() < shakeValue[150].getX()
					&& shakeValue[200].getX() < shakeValue[150].getX()) {
				shakeVal = shakeVal - 1;
			} else if (shakeValue[0].getX() > shakeValue[50].getX()
					&& shakeValue[100].getX() > shakeValue[50].getX()
					&& shakeValue[100].getX() > shakeValue[150].getX()
					&& shakeValue[200].getX() > shakeValue[150].getX()) {
				shakeVal = shakeVal - 1;
			} else if (shakeValue[0].getY() < shakeValue[50].getY()
					&& shakeValue[100].getY() < shakeValue[50].getY()
					&& shakeValue[100].getY() < shakeValue[150].getY()
					&& shakeValue[200].getY() < shakeValue[150].getY()) {
				shakeVal = shakeVal - 1;
			} else if (shakeValue[0].getY() > shakeValue[50].getY()
					&& shakeValue[100].getY() > shakeValue[50].getY()
					&& shakeValue[100].getY() > shakeValue[150].getY()
					&& shakeValue[200].getX() > shakeValue[150].getY()) {
				shakeVal = shakeVal - 1;
			}
			if (shakeVal < -2)
				shakeVal = -2;
			j = 0;
		}
	}

	public void nativeMouseDragged(NativeMouseEvent e) {}

	public void run() {
		speedThread=new Thread(new Runnable(){
			public void run(){
				while(!Thread.interrupted()){
					try{Thread.sleep(1000);}
					catch(InterruptedException e){e.printStackTrace();}
					
					speed[i]=sPixels;
					i++;
					i=i%7;
					sPixels=0;
					
				}
			}
		});
		speedThread.start();
		while (GlobalScreen.isNativeHookRegistered()&&!Thread.interrupted()) {
			try {Thread.sleep(5000);}
			catch (InterruptedException e) {break;}
			double sum = 0;
			for (int i = 0; i < 7; i++) {
				sum += speed[i];
			}
			
			//Mouse Speed As A Percentage Of A Maximum Speed Of 25000 pixels per second
			double speedAvg = (sum/25000)*100;
			if (speedAvg <= 10) speedVal = speedVal + 2;
			else if (speedAvg <= 20) speedVal = speedVal +1;
			else if (speedVal <= 30) speedVal = speedVal + 0;
			else if (speedAvg <= 50) speedVal = speedVal - 1;
			else if (speedAvg <= 70) speedVal = speedVal - 2;
			else speedVal = speedVal - 3;
			
			mouseVal = speedVal + clickVal + shakeVal + holdVal;
			if (mouseVal < -10) mouseVal = -10;
			else if (mouseVal > 10) mouseVal = 10;
			
			clickVal = 0;
			speedVal = 0;
			shakeVal = 0;
		}
		speedThread.interrupt();
	}
}
