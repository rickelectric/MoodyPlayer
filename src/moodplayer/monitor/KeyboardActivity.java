package moodplayer.monitor;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyboardActivity implements NativeKeyListener,Runnable{
	
	private static GlobalScreen gscr;
	private static KeyboardActivity a;
	private static Thread activeThread;
	
	@SuppressWarnings("unused")
	private int keyValue, alphaNum, error, space, ti, ei;
	private long[] type,err;

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
		a = new KeyboardActivity();
		
		gscr.addNativeKeyListener(a);

		activeThread = new Thread(a);
		activeThread.start();
	}
	
	public KeyboardActivity(){
		keyValue=0;
		alphaNum=0;
		error=0;
		space=0;
		ti=0;
		ei=0;
		type=new long[]{0,0,0,0,0,0,0,0,0,0};
		err=new long[]{0,0,0,0,0,0,0,0,0,0};
	}
	
	@SuppressWarnings("unused")
	public void run(){
		while(!Thread.interrupted()){
			try{
				Thread.sleep(1000);
				long typeDelay=average(type);
				long errDelay=average(err);
			}catch(Exception e){e.printStackTrace();}
		}
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {}
	
	public void nativeKeyReleased(NativeKeyEvent e) {
		recordKey(e);
	}
	
	public void nativeKeyTyped(NativeKeyEvent arg0) {}
	
	public long average(long[] intv){
		long tote=0;
		int i=0;
		while(i<intv.length-1){
			if(intv[i]==0) break;
			i++;
			tote+=(intv[i]-intv[i-1]);
		}
		if(i==0) return 0;
		return tote/i;
	}
	
	public void recordKey(NativeKeyEvent e) {
		int key = e.getKeyCode();
		if (key == NativeKeyEvent.VK_SPACE){
			space++;
			type();
		}
		else if (key == NativeKeyEvent.VK_BACK_SPACE || key == NativeKeyEvent.VK_DELETE){
			error++;
			err();
		}
		else if (isAlphaNumeric(e)){
			alphaNum++;
			type();
		}
	}
	
	private void type(){
		type[ti]=System.currentTimeMillis();
		ti++;
		ti%=10;
	}
	
	private void err(){
		err[ei]=System.currentTimeMillis();
		ei++;
		ei%=10;
	}

	private boolean isAlphaNumeric(NativeKeyEvent e){
		int key=e.getKeyCode();
		if(key>=NativeKeyEvent.VK_A && key<=NativeKeyEvent.VK_Z) return true;
		if(key>=NativeKeyEvent.VK_0 && key<=NativeKeyEvent.VK_9) return true;
		return false;
	}

	public int getKeyValue() {
		return keyValue;
	}
}
