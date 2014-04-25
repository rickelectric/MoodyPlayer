package moodplayer.views.swingmods;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.Timer;

public class OpacEffects {

	private static ExecutorService pool = null;
	private static ThreadFactory factory = null;

	public static final int X_AXIS = 100, Y_AXIS = 101;

	public static void init() {
		if (factory == null)
			factory = Executors.defaultThreadFactory();
		if (pool == null)
			pool = Executors.newFixedThreadPool(2);
	}

	public static Thread getThread(Runnable r) {
		if (factory == null)
			init();
		return factory.newThread(r);
	}

	public static void slide(final Slideable o, final int distance,
			final int direction, int speed, final int fadeMode) {
		if (pool == null)
			init();
		if (o.isSliding())
			return;
		o.setSliding(true);
		if (distance == 0) {
			o.setSliding(false);
			return;
		}
		if (direction != Slideable.UP && direction != Slideable.DOWN
				&& direction != Slideable.LEFT && direction != Slideable.RIGHT) {
			o.setSliding(false);
			throw new IllegalArgumentException("Invalid Direction Parameter.");
		}
		if (fadeMode != Opacible.IN && fadeMode != Opacible.OUT
				&& fadeMode != Opacible.NONE) {
			o.setSliding(false);
			throw new IllegalArgumentException("Invalid FadeMode");
		}
		int aResolve = 0;
		if (fadeMode == Opacible.IN) {
			if (o.isVisible()) {
				o.setSliding(false);
				throw new IllegalArgumentException(
						"Cannot fade in a visible object.");
			}
			aResolve = 1;
			o.setAlpha(0);
		}
		if (fadeMode == Opacible.OUT) {
			if (!o.isVisible()) {
				o.setSliding(false);
				throw new IllegalArgumentException(
						"Cannot fade out an invisible object.");
			}
			aResolve = -1;
			o.setAlpha(1);
		}
		final int time = 1000/speed;
		final float alphaInc = (float) aResolve / distance;
		
		Timer t = new Timer(time, new ActionListener() {

			int i = 0;

			public void actionPerformed(ActionEvent e) {
				if (i >=distance){
					((Timer)e.getSource()).stop();
					return;
				}
				if (direction == Slideable.UP || direction == Slideable.DOWN) {
					o.setLocation(o.getX(), o.getY() + (int) (direction / 10));
				} else {
					o.setLocation(o.getX() + (int) (direction / 20), o.getY());
				}
				if (fadeMode == Opacible.IN || fadeMode == Opacible.OUT) {
					o.setAlpha(o.getAlpha() + alphaInc);
				}
				//o.repaint();
				i++;
			}
		});
		o.setSlidingTimer(t);
		t.start();
		o.setSliding(true);
		// return t;
	}

	public static Thread slde(final Slideable o, final int distance,
			final int direction, int speed, final int fadeMode) {
		if (pool == null)
			init();
		if (o.isSliding())
			return null;
		o.setSliding(true);
		if (distance == 0) {
			o.setSliding(false);
			return null;
		}
		if (direction != Slideable.UP && direction != Slideable.DOWN
				&& direction != Slideable.LEFT && direction != Slideable.RIGHT) {
			o.setSliding(false);
			throw new IllegalArgumentException("Invalid Direction Parameter.");
		}
		if (fadeMode != Opacible.IN && fadeMode != Opacible.OUT
				&& fadeMode != Opacible.NONE) {
			o.setSliding(false);
			throw new IllegalArgumentException("Invalid FadeMode");
		}
		int aResolve = 0;
		if (fadeMode == Opacible.IN) {
			if (o.isVisible()) {
				o.setSliding(false);
				throw new IllegalArgumentException(
						"Cannot fade in a visible object.");
			}
			aResolve = 1;
			o.setAlpha(0);
		}
		if (fadeMode == Opacible.OUT) {
			if (!o.isVisible()) {
				o.setSliding(false);
				throw new IllegalArgumentException(
						"Cannot fade out an invisible object.");
			}
			aResolve = -1;
			o.setAlpha(1);
		}
		final int time = 1000 / speed;
		final float alphaInc = (float) aResolve / distance;

		Thread t = getThread(new Runnable() {
			public void run() {
				for (int i = 0; i < distance; i++) {
					if (Thread.interrupted()) {
						o.setSliding(false);
						return;
					}
					if (direction == Slideable.UP
							|| direction == Slideable.DOWN) {
						o.setLocation(o.getX(), o.getY()
								+ (int) (direction / 10));
					} else {
						o.setLocation(o.getX() + (int) (direction / 20),
								o.getY());
					}
					if (fadeMode == Opacible.IN || fadeMode == Opacible.OUT) {
						o.setAlpha(o.getAlpha() + alphaInc);
					}
					o.repaint();
					try{Thread.sleep(time);}catch(Exception e){}
				}
				o.setSliding(false);
			}
		});
		t.start();
		o.setSlidingThread(t);
		return t;
	}

	public static void slideSet(final Slideable[] o, final int[] distances,
			final int[] directions, final int[] speeds, final int[] fadeModes) {
		if (o.length != distances.length)
			return;
		if (distances.length != directions.length)
			return;
		if (directions.length != speeds.length)
			return;
		if (speeds.length != fadeModes.length)
			return;

		for (int i = 0; i < o.length; i++) {
			final int ii = i;
			slide(o[ii], distances[ii], directions[ii], speeds[ii],
					fadeModes[ii]);
		}
	}

	public static void fadeIn(final Opacible o, int speed) {
		if(o.isVisible()||o.getAlpha()!=0) return;
		int delay=100/speed;
		
		new Timer(delay,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(o.getAlpha()>=1){
					((Timer)e.getSource()).stop();
					return;
				}
				o.setAlpha(o.getAlpha()+0.01f);
				
			}
		}).start();
	}

	public static void fadeOut(final Opacible o, int speed) {
		if(o.isVisible()||o.getAlpha()!=0) return;
		int delay=100/speed;
		
		new Timer(delay,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(o.isVisible()==false||o.getAlpha()==0){
					((Timer)e.getSource()).stop();
					return;
				}
				o.setAlpha(o.getAlpha()-0.01f);
				
			}
		}).start();
	}

	public static void fadeInSet(Opacible[] o, int[] speeds) {

	}

	public static void fadeOutSet(Opacible[] o, int[] speeds) {

	}

	public static void oscillate(final Opacible o, final int speed) {
		o.setOscSpeed(1000 / speed);
		if (o.isOscillating()) {
			return;
		}
		pool.execute(new Runnable() {
			public void run() {
				o.setOscillating(true);
				float direction = -0.05f;
				while (o.isOscillating()) {
					float alpha = o.getAlpha();
					alpha += direction;
					if (alpha < 0) {
						alpha = 0;
						direction = 0.05f;
					} else if (alpha > 1) {
						alpha = 1;
						direction = -0.05f;
					}
					o.setAlpha(alpha);
					try{Thread.sleep(o.getOscSpeed());}catch(Exception e){}
				}
				o.setAlpha(1f);
			}
		});
	}

	public static void stopOscillation(Opacible o) {
		o.setOscillating(false);
	}

	public static void stopAll() {
		pool.shutdownNow();
	}

}
