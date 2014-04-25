package moodplayer.views.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moodplayer.MoodyPlayer;
import moodplayer.api.MP3_Interface;
import moodplayer.api.RequestCache;
import moodplayer.api.StreamDownloader;
import moodplayer.exception.MoodAccessException;
import moodplayer.library.MediaLibrary;
import moodplayer.models.Emotion;
import moodplayer.monitor.GlobalMoodModel;
import moodplayer.player.MediaStreamer;
import moodplayer.player.playlist.MPlaylist2;
import moodplayer.player.playlist.PlaylistObserverModel;
import moodplayer.player.playlist.PlaylistRunner;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import com.echonest.api.v4.Song;

public class PlayerWindow extends JFrame implements Observer{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	JScrollBar bar;
	private JTable table;
	private JPanel panel_controls;

	private PlaylistRunner pl;
	private MediaPlayer player;
	PlaylistObserverModel pl_model;
	private JScrollPane scrollPane;
	
	private JPanel panel_top;
	private JSlider slider_progress;
	private JSlider slider_volume;
	private JPanel panel_r;
	private JLabel statusBar;
	private JLabel loading_icon;

	private static final ImageIcon icons[] = new ImageIcon[] {
			new ImageIcon(
					MoodyPlayer.class
							.getResource("/moodplayer/player/media_play.png")),
			new ImageIcon(
					MoodyPlayer.class
							.getResource("/moodplayer/player/media_pause.png")),
			new ImageIcon(
					MoodyPlayer.class
							.getResource("/moodplayer/player/media_back.png")),
			new ImageIcon(
					MoodyPlayer.class
							.getResource("/moodplayer/player/media_next.png")),
			new ImageIcon(
					MoodyPlayer.class
							.getResource("/moodplayer/player/media_stop.png")) 
	};

	private JLabel btn_playpause, btn_stop, btn_next, btn_prev;

	public static void main(String[] args) {
		
		try{
			StreamDownloader.ping("http://echonest.com");
		}catch(IOException e){
			
		}
		
		MediaStreamer.init();
		MediaLibrary.init();
		RequestCache.init();
		new PlayerWindow().setVisible(true);
	}

	public PlayerWindow() {
		
		setMinimumSize(new Dimension(560, 330));
		
		setPreferredSize(new Dimension(560, 415));
		
		setSize(new Dimension(570, 520));
		setLocation(0,0);
		setLocationRelativeTo(null);

		setTitle("Moody Player");
		setIconImage(new ImageIcon(MoodyPlayer.class.getResource("img/MoodyPlayer2_32.png")).getImage());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				if(!MoodyPlayer.exit()) setVisible(true);
				else dispose();
			}
			@Override
			public void windowIconified(WindowEvent e) {
				setVisible(false);
			}
		});
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mi_monitor = new JMenuItem("Show Mood Monitor");
		mi_monitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MoodyPlayer.showMeter();
			}
		});
		mnFile.add(mi_monitor);
		
		mi_settings = new JMenuItem("Settings");
		mnFile.add(mi_settings);
		
		mi_exit = new JMenuItem("Exit");
		mnFile.add(mi_exit);
		
		mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mi_topics = new JMenuItem("Topics");
		mnHelp.add(mi_topics);
		
		mi_about = new JMenuItem("About");
		mnHelp.add(mi_about);

		contentPane = new JPanel();
		contentPane
				.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null,
				null));
		table.setFont(new Font("Dialog", Font.BOLD, 12));
		scrollPane.setViewportView(table);
		table.setRowHeight(25);

		panel_top = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			public void setEnabled(boolean b){
				super.setEnabled(b);
				panel_controls.setEnabled(b);
				slider_volume.setEnabled(b);
				slider_progress.setEnabled(b);
			}
		};
		panel_top.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		panel_top.setPreferredSize(new Dimension(10, 89));
		contentPane.add(panel_top, BorderLayout.NORTH);
		panel_top.setLayout(new BorderLayout(0, 0));

		panel_controls = new JPanel(){
			private static final long serialVersionUID = 1L;

			public void setEnabled(boolean b){
				super.setEnabled(b);
				btn_next.setEnabled(b);
				btn_playpause.setEnabled(b);
				btn_prev.setEnabled(b);
				btn_stop.setEnabled(b);
			}
		};
		panel_controls.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_top.add(panel_controls, BorderLayout.CENTER);
		
		FlowLayout fl_panel_controls = (FlowLayout) panel_controls.getLayout();
		fl_panel_controls.setVgap(3);
		fl_panel_controls.setAlignOnBaseline(true);
		fl_panel_controls.setAlignment(FlowLayout.LEADING);
		panel_controls.setPreferredSize(new Dimension(10, 50));
		
		MouseListener blinker=new MouseAdapter() {

			private Thread rThread=null;
			private Color orig;
			private JLabel prev;

			@SuppressWarnings("deprecation")
			public void mouseClicked(final MouseEvent e) {
				if(rThread!=null&&rThread.isAlive()){
					rThread.stop();
					prev.setBackground(orig);
				}
				rThread=new Thread(new Runnable(){

					public void run(){
						prev=((JLabel)(e.getSource()));
						orig = prev.getBackground();
						try{
							for(int i=0;i<5;i++){
								prev.setBackground(Color.RED);
								Thread.sleep(200);
								prev.setBackground(orig);
								Thread.sleep(200);
							}
						}catch(Exception ex){
							prev.setBackground(orig);
						}
					}
				});
				rThread.start();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				((JLabel)(e.getSource())).setHorizontalAlignment(JLabel.LEADING);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				((JLabel)(e.getSource())).setHorizontalAlignment(JLabel.CENTER);
			}
		};

		btn_playpause = new JLabel("");
		btn_playpause.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				pl.playPause();
			}
		});
		btn_playpause.addMouseListener(blinker);
		btn_playpause.setIcon(icons[0]);
		btn_playpause.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		btn_playpause.setPreferredSize(new Dimension(50, 50));
		panel_controls.add(btn_playpause);

		btn_prev = new JLabel("");
		btn_prev.addMouseListener(blinker);
		btn_prev.setIcon(icons[2]);
		btn_prev.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		btn_prev.setPreferredSize(new Dimension(50, 50));
		panel_controls.add(btn_prev);

		btn_next = new JLabel("");
		btn_next.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				pl.next();
			}
		});
		btn_next.addMouseListener(blinker);
		btn_next.setIcon(icons[3]);
		btn_next.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		btn_next.setPreferredSize(new Dimension(50, 50));
		panel_controls.add(btn_next);

		btn_stop = new JLabel("");
		btn_stop.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				pl.stop();
			}
		});
		btn_stop.addMouseListener(blinker);
		btn_stop.setIcon(icons[4]);
		btn_stop.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		btn_stop.setPreferredSize(new Dimension(50, 50));
		panel_controls.add(btn_stop);
		
		panel_controls.setEnabled(false);

		slider_progress = new JSlider();
		slider_progress.setValue(0);
		slider_progress.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		panel_top.add(slider_progress, BorderLayout.SOUTH);

		panel_r = new JPanel();
		panel_r.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_r.setPreferredSize(new Dimension(240, 10));
		panel_top.add(panel_r, BorderLayout.EAST);
		panel_r.setLayout(null);

		slider_volume = new JSlider();
		slider_volume.setBounds(78, 29, 151, 16);
		slider_volume.setMaximum(200);
		slider_volume.setValue(0);
		panel_r.add(slider_volume);

		JLabel lblVolume = new JLabel("Volume: ");
		lblVolume.setBounds(76, 12, 55, 16);
		panel_r.add(lblVolume);

		statusBar = new JLabel("Ready");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		contentPane.add(statusBar, BorderLayout.SOUTH);

		setVisible(true);

		player = MediaStreamer.getAudioPlayer();

		loading_icon = new JLabel("");
		loading_icon.setIcon(new ImageIcon(getClass()
				.getResource("/moodplayer/img/ajax-loader-48.gif")));
		loading_icon.setBounds(12, 2, 52, 52);
		loading_icon.setVisible(false);
		panel_r.add(loading_icon);
	}
	
	private MouseListener volumeSlider;
	private ChangeListener volumeChange=new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			int val = slider_volume.getValue();
			player.setVolume(val);
		}
	};
	private boolean volReady=false;
	
	ProgressSlider progressSlider;
	
	public void addPlayerEvents(){
		player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			Thread track = null;

			private String millisToTime(long mills) {
				String sec = String.format("%02d", (int) (mills / 1000) % 60);
				String min = String.format("%02d",
						(int) ((mills / (1000 * 60)) % 60));
				String hr = String.format("%02d",
						(int) ((mills / (1000 * 60 * 60)) % 24));
				String ps = hr + ":" + min + ":" + sec;
				return ps;
			}

			public void lengthChanged(final MediaPlayer p, final long time) {
				slider_progress.setMaximum((int) time);
				final String timeStr = millisToTime(time);
				
				if (track != null)
					track.interrupt();

				track = new Thread(new Runnable() {

					public void run() {
						while (true) {
							if (p.isPlaying()) {
								float position = player.getPosition();
								slider_progress
										.setValue((int) (position * time));

								int pos = (int) (time * position);
								String ps = millisToTime(pos);

								statusBar.setText("Streaming: " + ps + " / "
										+ timeStr);
							}
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								break;
							}
						}

					}
				});
				if(progressSlider==null){
					progressSlider=new ProgressSlider(slider_progress,player,time);
					slider_progress.addMouseListener(progressSlider);
				}
				else{
					progressSlider.updateTime(time);
				}
				
				if(!volReady){
					slider_volume.addChangeListener(volumeChange);
					slider_volume.addMouseListener(volumeSlider);
					volReady=true;
				}
				
				track.start();
			}

			public void opening(MediaPlayer p) {
				loading_icon.setVisible(true);
				statusBar.setText("Media Changing: --:--:-- / --:--:--");
			}

			public void finished(MediaPlayer p) {
				btn_playpause.setIcon(icons[0]);
				super.finished(p);
			}

			public void playing(MediaPlayer p) {
				loading_icon.setVisible(false);
				btn_playpause.setIcon(icons[1]);
				panel_controls.setEnabled(true);
			}

			public void paused(MediaPlayer p) {
				btn_playpause.setIcon(icons[0]);
			}

			public void stopped(MediaPlayer p) {
				btn_playpause.setIcon(icons[0]);
			}

		});
	}
	
	public void load(Emotion dMood) {
		loading_icon.setVisible(true);
		
		addPlayerEvents();
		List<Song> songs=null;
		try {
			songs = MPlaylist2.list(GlobalMoodModel.getDominantMood().getName());
		} catch (MoodAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pl = MP3_Interface.buildPlaylist(songs, player);
		
		//pl = MP3_Interface.buildPlaylist(songs, player);
		pl_model = new PlaylistObserverModel(pl);
		new Thread(pl).start();
		
		table.setModel(pl_model);
		table.addMouseListener(tableMenu);
	}

	private MouseListener tableMenu = new MouseAdapter() {

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getClickCount() == 1) {
				int r = table.rowAtPoint(e.getPoint());
				if (r >= 0 && r < table.getRowCount()) {
					table.setRowSelectionInterval(r, r);
				} else {
					table.clearSelection();
				}
				int rowindex = table.getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.getButton() == MouseEvent.BUTTON3
						&& e.getClickCount() == 1) {
					JPopupMenu popup = pl_model.popup(rowindex);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}

	};
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mi_exit;
	private JMenuItem mi_monitor;
	private JMenuItem mi_settings;
	private JMenu mnHelp;
	private JMenuItem mi_topics;
	private JMenuItem mi_about;

	@Override
	public void update(Observable o, Object arg) {
		setTitle("Moody Player: "+GlobalMoodModel.getDominantMood().getName());
		table.repaint();
	}
}

class ProgressSlider extends MouseAdapter{
	
	private JSlider slider_progress;
	MediaPlayer player;
	long time;
	
	public ProgressSlider(JSlider slider_progress,MediaPlayer player,long time){
		this.slider_progress=slider_progress;
		this.player=player;
		this.time=time;
	}
	
	public void updateTime(long time){
		slider_progress.setValue(0);
		this.time=time;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point p = e.getPoint();
		float position = 1.0f * p.x
				/ slider_progress.getWidth();
		slider_progress.setValue((int) (position * time));

		int pos = slider_progress.getValue();
		float pm = 1.0f * pos / slider_progress.getMaximum();
		player.setPosition(pm);
	}
	
}

class VolumeSlider extends MouseAdapter{
	
	private JSlider slider_volume;
	MediaPlayer player;
	
	public VolumeSlider(JSlider slider_volume,MediaPlayer player){
		this.slider_volume=slider_volume;
		this.player=player;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		Point p = e.getPoint();
		float position = 1.0f * p.x / slider_volume.getWidth();
		int nval = (int) (position * 200);
		player.setVolume(nval);
		slider_volume.setValue(nval);
	}
	
}
