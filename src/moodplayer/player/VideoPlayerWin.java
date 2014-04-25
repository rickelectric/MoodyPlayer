package moodplayer.player;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moodplayer.MoodyPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

public class VideoPlayerWin extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JPanel videoPane;
	private JLabel statusBar;
	private Thread track;

	private Canvas videoCanvas;

	private CanvasVideoSurface videoSurface;

	private long time;

	private JLabel loading;
	
	public VideoPlayerWin(String mrl,final EmbeddedMediaPlayer player,MediaPlayerFactory factory) {
		setMinimumSize(new Dimension(560, 168));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				player.stop();
				dispose();
			}
		});
		setBounds(100, 100, 560, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		videoPane = new JPanel();
		videoPane.setBorder(new CompoundBorder(new LineBorder(Color.black, 2),
				new EmptyBorder(4, 4, 4, 4)));
		videoPane.setLayout(new BorderLayout());
		videoPane.setBackground(Color.white);
		
		videoCanvas = new Canvas();
		videoCanvas.setBackground(Color.BLACK);
		videoCanvas.setSize(720, 350);

		videoPane.add(videoCanvas, BorderLayout.CENTER);

		videoSurface = factory.newVideoSurface(videoCanvas);
		
		player.setVideoSurface(videoSurface);
		
		contentPane.add(videoPane,BorderLayout.CENTER);
		
		final JSlider slider_position = new JSlider();
		slider_position.setPaintTicks(true);
		videoPane.add(slider_position, BorderLayout.SOUTH);
		
		time = player.getLength();
		slider_position.setMaximum((int) time);
		
		track=new Thread(new Runnable(){
			public void run(){
				time = player.getLength();
				slider_position.setMaximum((int) time);
				while (isVisible()){
					if(player.isPlaying()){
						float position = player.getPosition();
						slider_position.setValue((int) (position * time));
						
						int pos=(int)(time*position);
						String sec  = String.format("%02d",(int)(pos/ 1000) % 60 );
						String min  = String.format("%02d",(int)((pos/ (1000*60)) % 60));
						String hr   = String.format("%02d",(int)((pos/ (1000*60*60)) % 24));
						String ps=hr+":"+min+":"+sec;
						statusBar.setText("Streaming: "+ ps);
					}
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						break;
					}
				}
				
			}
		});
		
		slider_position.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e){
				Point p=e.getPoint();
				float position = 1.0f*p.x/slider_position.getWidth();
				slider_position.setValue((int) (position * time));
				
				int pos = slider_position.getValue();
				float pm = 1.0f * pos / slider_position.getMaximum();
				player.setPosition(pm);
			}

		});
		
		statusBar = new JLabel("0:00:00");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(statusBar, BorderLayout.SOUTH);
		
		JPanel panel_controls = new JPanel();
		panel_controls.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		FlowLayout flowLayout = (FlowLayout) panel_controls.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel_controls, BorderLayout.NORTH);
		
		final JLabel btn_playpause = new JLabel("");
		panel_controls.add(btn_playpause);
		btn_playpause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e){
				if(player.isPlaying()){
					player.pause();
					try{Thread.sleep(100);}catch(Exception ex){}
				}
				else{
					player.play();
					try{while(!player.isPlaying()) Thread.sleep(100);}catch(Exception ex){}
				}
				if(player.isPlaying()){
					btn_playpause.setIcon(new ImageIcon(getClass().getResource("media_pause.png")));
				}
				else{
					btn_playpause.setIcon(new ImageIcon(getClass().getResource("media_play.png")));
				}
			}
		});
		btn_playpause.setHorizontalAlignment(SwingConstants.CENTER);
		btn_playpause.setIcon(new ImageIcon(getClass().getResource("media_pause.png")));
		btn_playpause.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		JLabel btn_stop = new JLabel("");
		panel_controls.add(btn_stop);
		btn_stop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				player.stop();
				if(player.isPlaying()){
					btn_playpause.setIcon(new ImageIcon(getClass().getResource("media_pause.png")));
				}
				else{
					btn_playpause.setIcon(new ImageIcon(getClass().getResource("media_play.png")));
				}
			}
		});
		btn_stop.setHorizontalAlignment(SwingConstants.CENTER);
		btn_stop.setIcon(new ImageIcon(getClass().getResource("media_stop.png")));
		btn_stop.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		Component glue = Box.createGlue();
		glue.setPreferredSize(new Dimension(180, 0));
		panel_controls.add(glue);
		
		JLabel lblVolume = new JLabel("Volume: ");
		panel_controls.add(lblVolume);
		
		final JSlider slider_volume = new JSlider();
		slider_volume.setMaximum(200);
		slider_volume.setMinimum(0);
		slider_volume.setValue(100);
		slider_volume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				int val=slider_volume.getValue();
				player.setVolume(val); 
			}
		});
		slider_volume.setPreferredSize(new Dimension(100, 16));
		panel_controls.add(slider_volume);
		
		loading = new JLabel("");
		loading.setIcon(new ImageIcon(MoodyPlayer.class.getResource("img/ajax-loader-48.gif")));
		loading.setPreferredSize(new Dimension(49, 49));
		loading.setVisible(false);
		panel_controls.add(loading);
		
		statusBar = new JLabel("0:00:00");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(statusBar, BorderLayout.SOUTH);
		
		setVisible(true);
		player.playMedia(mrl);
		
		try {
			while (!player.isPlaying())
				loading.setVisible(true);
				Thread.sleep(200);
		} catch (Exception e) {dispose();}
		loading.setVisible(false);
		track.start();
	}

}
