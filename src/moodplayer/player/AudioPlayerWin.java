package moodplayer.player;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.player.MediaPlayer;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class AudioPlayerWin extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private Thread track;
	private JLabel statusBar;

	private MediaPlayer player;
	
	/**
	 * @wbp.parser.constructor
	 */
	public AudioPlayerWin(final MediaPlayer player){
		this.player = player;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				close();
			}
		});
		setResizable(false);
		setBounds(100, 100, 490, 160);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(3, 3, 3, 3));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		final JSlider slider_position = new JSlider();
		slider_position.setPaintTicks(true);
		contentPane.add(slider_position, BorderLayout.CENTER);
		
		final long time=player.getLength();
		slider_position.setMaximum((int) time);
		
		track=new Thread(new Runnable(){
			public void run(){
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
		
		setVisible(true);
		track.start();
	}
	
	public void close(){
		player.setPosition(1.0f);
		player.stop();
		dispose();
	}
	
}
