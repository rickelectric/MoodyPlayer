package moodplayer.views.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import moodplayer.library.MediaLibrary;
import moodplayer.library.SelectLoadLibPanel;
import moodplayer.util.ImageManager;
import moodplayer.util.SettingsManager;

import com.github.sarxos.webcam.Webcam;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JButton button_save;
	private JButton button_cancel;
	private JButton button_defaults;
	private JPanel panel_facemon;
	private JPanel panel_webcam;
	private JComboBox<String> choice_webcam;
	private List<Webcam> opened;
	private JLabel img_webcam;
	private JButton button_Test;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
					SettingsManager.init();
					MediaLibrary.init();
					SettingsWindow frame = new SettingsWindow();
					frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public SettingsWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsWindow.class.getResource("/moodplayer/img/MoodyPlayer2.png")));
		setResizable(false);
		setTitle("Settings");
		
		setBounds(100, 100, 525, 518);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(2, 5, 2, 2));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel libPanel = new SelectLoadLibPanel();
		tabbedPane.addTab("Media Library", null, libPanel, null);
		
		panel_facemon = new JPanel();
		tabbedPane.addTab("Face Monitor", null, panel_facemon, null);
		panel_facemon.setLayout(null);
		
		panel_webcam = new JPanel();
		panel_webcam.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Webcam", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_webcam.setBounds(6, 6, 492, 197);
		panel_facemon.add(panel_webcam);
		panel_webcam.setLayout(null);
		
		final List<Webcam> webcams=Webcam.getWebcams();
		choice_webcam = new JComboBox<String>();
		choice_webcam.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				SettingsManager.webcamSelection(webcams.get(choice_webcam.getSelectedIndex()));
			}
		});
		for(Webcam w:webcams){
			choice_webcam.addItem(w.getDevice().getName()); 
		}
		choice_webcam.setBounds(20, 42, 217, 26);
		panel_webcam.add(choice_webcam);
		
		JLabel lblSelectWebcam = new JLabel("Select Default Webcam:");
		lblSelectWebcam.setBounds(20, 25, 150, 16);
		panel_webcam.add(lblSelectWebcam);
		
		img_webcam = new JLabel("");
		img_webcam.setOpaque(true);
		img_webcam.setBorder(new LineBorder(new Color(0, 0, 0)));
		img_webcam.setHorizontalAlignment(SwingConstants.CENTER);
		img_webcam.setIcon(new ImageIcon(SettingsWindow.class.getResource("/com/github/sarxos/webcam/icons/camera-icon.png")));
		img_webcam.setBounds(280, 25, 198, 153);
		panel_webcam.add(img_webcam);
		
		opened=new ArrayList<Webcam>();
		
		button_Test = new JButton("Test");
		button_Test.addActionListener(new ActionListener() {
			
			boolean isActive=false;
			
			public void actionPerformed(ActionEvent e) {
				snapWebcam();
			}

			private void snapWebcam() {
				if(isActive) return;
				isActive=true;
				new Thread(new Runnable(){
					public void run(){
						try{
							img_webcam.setBackground(new Color(214,217,233));
							img_webcam.setIcon(new ImageIcon(SettingsWindow.class.getResource("/moodplayer/img/ajax-loader-48.gif")));
							Webcam w=SettingsManager.webcamSelection();
							if(!w.isOpen()){
								w.open();
								opened.add(w);
							}
							w.getImage();
							BufferedImage img=w.getImage();
							ImageManager.resizeImage(img, 217, 153);
							img_webcam.setBackground(Color.black);
							img_webcam.setIcon(new ImageIcon(img));
						}catch(Exception e){e.printStackTrace();}
						isActive=false;
					}
				}).start();
			}
		});
		button_Test.setBounds(147, 80, 90, 28);
		panel_webcam.add(button_Test);
		
		JPanel panel_bottom = new JPanel();
		panel_bottom.setPreferredSize(new Dimension(10, 60));
		contentPane.add(panel_bottom, BorderLayout.SOUTH);
		panel_bottom.setLayout(null);
		
		button_save = new JButton("Save");
		button_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SettingsManager.save();
				close();
			}
		});
		button_save.setBounds(264, 7, 111, 28);
		panel_bottom.add(button_save);
		
		button_cancel = new JButton("Cancel");
		button_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		button_cancel.setBounds(387, 7, 111, 28);
		panel_bottom.add(button_cancel);
		
		button_defaults = new JButton("Select Defaults");
		button_defaults.setBounds(6, 7, 111, 28);
		panel_bottom.add(button_defaults);
	}
	
	public void close(){
		closeAllOpenedCams();
		dispose();
	}
	
	private void closeAllOpenedCams(){
		for(Webcam w:opened){
			w.close();
		}
	}
}
