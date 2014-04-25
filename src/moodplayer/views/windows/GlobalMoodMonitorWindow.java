package moodplayer.views.windows;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import moodplayer.models.Emotion;
import moodplayer.monitor.FaceMonitor;
import moodplayer.monitor.GlobalMoodModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GlobalMoodMonitorWindow extends JFrame implements Observer{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private JProgressBar[] moods;
	private JPanel panel_emotions;
	private JButton button_fmstate;
	private JLabel img_facemon;

	private JPanel panel_facemon;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GlobalMoodMonitorWindow frame = new GlobalMoodMonitorWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public GlobalMoodMonitorWindow() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(GlobalMoodMonitorWindow.class.getResource("/moodplayer/img/MoodyPlayer3.png")));
		setTitle("Mood State Monitor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 748, 410);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel_emotions = new JPanel();
		panel_emotions.setBorder(new TitledBorder(null, "Emotions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_emotions.setBounds(6, 6, 348, 359);
		contentPane.add(panel_emotions);
		panel_emotions.setLayout(null);
		
		JLabel lblHappy = new JLabel("Happy");
		lblHappy.setBounds(52, 279, 47, 16);
		panel_emotions.add(lblHappy);
		lblHappy.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblHappy.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblCalm = new JLabel("Calm");
		lblCalm.setBounds(6, 279, 47, 16);
		panel_emotions.add(lblCalm);
		lblCalm.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblCalm.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblSurprised = new JLabel("Surprised");
		lblSurprised.setBounds(100, 279, 47, 16);
		panel_emotions.add(lblSurprised);
		lblSurprised.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblSurprised.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblConfused = new JLabel("Confused");
		lblConfused.setBounds(154, 279, 47, 16);
		panel_emotions.add(lblConfused);
		lblConfused.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblConfused.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblSad = new JLabel("Sad");
		lblSad.setBounds(202, 279, 47, 16);
		panel_emotions.add(lblSad);
		lblSad.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblSad.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblAngry = new JLabel("Angry");
		lblAngry.setBounds(245, 279, 47, 16);
		panel_emotions.add(lblAngry);
		lblAngry.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblAngry.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblDisgust = new JLabel("Disgust");
		lblDisgust.setBounds(291, 279, 47, 16);
		panel_emotions.add(lblDisgust);
		lblDisgust.setFont(new Font("SansSerif", Font.PLAIN, 11));
		lblDisgust.setHorizontalAlignment(SwingConstants.CENTER);
		
		JProgressBar calm=new JProgressBar();
		calm.setBounds(16, 20, 26, 247);
		panel_emotions.add(calm);
		calm.setValue(1);
		calm.setOrientation(SwingConstants.VERTICAL);
		
		JProgressBar happy = new JProgressBar();
		happy.setBounds(60, 20, 26, 247);
		panel_emotions.add(happy);
		happy.setValue(1);
		happy.setOrientation(SwingConstants.VERTICAL);
		
		JProgressBar surprised = new JProgressBar();
		surprised.setBounds(107, 20, 26, 247);
		panel_emotions.add(surprised);
		surprised.setValue(1);
		surprised.setOrientation(SwingConstants.VERTICAL);
		
		JProgressBar confused = new JProgressBar();
		confused.setBounds(159, 20, 26, 247);
		panel_emotions.add(confused);
		confused.setValue(1);
		confused.setOrientation(SwingConstants.VERTICAL);
		
		JProgressBar sad = new JProgressBar();
		sad.setBounds(212, 20, 26, 247);
		panel_emotions.add(sad);
		sad.setValue(1);
		sad.setOrientation(SwingConstants.VERTICAL);
		
		JProgressBar angry = new JProgressBar();
		angry.setBounds(254, 20, 26, 247);
		panel_emotions.add(angry);
		angry.setValue(1);
		angry.setOrientation(SwingConstants.VERTICAL);
		
		JProgressBar disgust = new JProgressBar();
		disgust.setBounds(297, 20, 26, 247);
		panel_emotions.add(disgust);
		disgust.setValue(1);
		disgust.setOrientation(SwingConstants.VERTICAL);
		
		panel_facemon = new JPanel();
		panel_facemon.setBorder(new TitledBorder(null, "Face Monitor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_facemon.setBounds(366, 6, 360, 359);
		contentPane.add(panel_facemon);
		panel_facemon.setLayout(null);
		
		img_facemon = new JLabel("");
		setCamImage(img_facemon);
		
		button_fmstate = new JButton("Pause Face Monitor");
		button_fmstate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(button_fmstate.getText().contains("Pause")){
					FaceMonitor.stopCapture();
					button_fmstate.setText("Resume Face Monitor");
				}else{
					FaceMonitor.startCapture();
					button_fmstate.setText("Pause Face Monitor");
				}
			}
		});
		button_fmstate.setBounds(19, 274, 170, 28);
		panel_facemon.add(button_fmstate);
		
		moods=new JProgressBar[]{
			calm,
			happy,
			surprised,
			sad,
			confused,
			angry,
			disgust
		};
		update(null,null);
	}
	
	public void setCamImage(JLabel img){
		panel_facemon.remove(img_facemon);
		this.img_facemon=img;
		img_facemon.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		img_facemon.setIcon(new ImageIcon(GlobalMoodMonitorWindow.class.getResource("/moodplayer/img/MoodyPlayer.png")));
		img_facemon.setHorizontalAlignment(SwingConstants.CENTER);
		img_facemon.setBounds(19, 22, 320, 240);
		panel_facemon.add(img_facemon);
	}

	@Override
	public void update(Observable o, Object arg) {
		Emotion[] e=GlobalMoodModel.getAllMoods();
		for(int i=0;i<7;i++){
			moods[i].setValue((int) (100*e[i].getValue()));
		}
		repaint();
	}
}
