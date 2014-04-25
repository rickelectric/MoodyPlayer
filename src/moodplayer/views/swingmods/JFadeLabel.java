package moodplayer.views.swingmods;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * @author Ionicle
 * <b>JFadeLabel, by Ionicle</b>
 * <p> Enhanced version of the Swing JLabel (javax.swing.JLabel) that makes opacity editing
 * (fading in and out, transparancy, etc) easily accessible.
 * </p>
 */
public class JFadeLabel extends JLabel implements Opacible{
		private static final long serialVersionUID = 1L;
		private float alpha;
		private BufferedImage bgimage;
		
		public JFadeLabel(){init(null,null);}
		public JFadeLabel(String s){init(s,null);}
		public JFadeLabel(BufferedImage icon){init(null,icon);}
		public JFadeLabel(String s,BufferedImage icon){init(s,icon);}
		
		public JFadeLabel(ImageIcon i){super(i);}
		
		public void init(String label,BufferedImage icon){
			try{bgimage=icon;}catch(Exception e){}
			if(label!=null) setText(label);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			setAlpha(1f);
			setDoubleBuffered(true);
		}
		
		private boolean oscillation;
		private int oscSpeed=20;
		public boolean isOscillating(){return oscillation;}
		public void setOscillating(boolean oscillation){this.oscillation=oscillation;}
		public int getOscSpeed(){return oscSpeed;}
		public void setOscSpeed(int oscSpeed){this.oscSpeed=oscSpeed;}
		
		public void stopOscillation(){oscillation=false;}
        
		/**
		 * <p>
		 * Set Alpha Composite opacity of the component.
		 * </p>
		 * @param value Alpha Value between 0 and 1 (decimal). 
		 */
		public void setAlpha(float value){
			if(value>1||value<0) return;
			if(value<0.02) setVisible(false);
			else setVisible(true);
			if(alpha!=value) {
				float old=alpha;
				alpha=value;
				firePropertyChange("alpha", old, alpha);
				repaint();
			}
		}
		
		public float getAlpha(){return alpha;}
		
		@Override
		public Dimension getPreferredSize(){
			return (bgimage==null)
				?super.getPreferredSize()
				:new Dimension(bgimage.getWidth(), bgimage.getHeight());
		}
		
		@Override
		public void paint(Graphics g){
			Graphics2D g2 =(Graphics2D)g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,getAlpha()));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); 
			//Set anti-alias for text
		    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
			
			super.paint(g2);
			g2.dispose();
		}
		
		@Override
		protected void paintComponent(Graphics g){
			if(bgimage!=null){
				int x=(getWidth()-bgimage.getWidth())/2;
				int y=(getHeight()-bgimage.getHeight())/2;
				g.drawImage(bgimage,x,y,this);
			}
			super.paintComponent(g);
		}
		
		private boolean fading=false;
		
		public boolean isFading(){
			return fading;
		}
		
		public void fadeIn(final int msec){
			fading=true;
			new Thread(new Runnable(){
				public void run(){
					while(getAlpha()<0.9f){
						setAlpha(alpha+0.02f);
						try{Thread.sleep(1000/msec);}catch(Exception e){}
					}
					setAlpha(1f);
					fading=false;
				}
			}).start();
		}
    }