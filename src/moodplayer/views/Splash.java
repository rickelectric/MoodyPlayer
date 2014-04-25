package moodplayer.views;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import moodplayer.MoodyPlayer;
import moodplayer.views.swingmods.JFadeLabel;

public class Splash extends JWindow {
	private static final long serialVersionUID = 1L;

	private JLayeredPane contentPane;
	private JLabel splashImage;

	private JLabel dispLine;

	private JProgressBar bLoad;

	private Timer fader;
	private JFadeLabel cameraPanel;
	private JButton button_X;
	private JButton button_info;
	private JButton button_yes;
	private JButton button_no;
	private JLabel dispLine1;

	public static void main(String[] args) {
		new Splash().setVisible(true);
	}

	public Splash() {
		
		try {
	        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
	    } catch (Exception evt) {}

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				Splash.class.getResource("/moodplayer/img/MoodyPlayer_64.png")));
		setAlwaysOnTop(true);

		// setUndecorated(true);
		// getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

		setSize(566, 426);
		if (UIManager.getLookAndFeel().getClass().getName()
				.equals(UIManager.getCrossPlatformLookAndFeelClassName()))
			setSize(570, 466);

		contentPane = new JLayeredPane();
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new CompoundBorder(new BevelBorder(
				BevelBorder.RAISED, null, null, null, null), new LineBorder(
				new Color(0, 0, 0), 5)));
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		splashImage = new JLabel("");
		splashImage.setIcon(new ImageIcon(Splash.class
				.getResource("/moodplayer/img/Splash.png")));
		splashImage.setHorizontalAlignment(SwingConstants.CENTER);
		splashImage.setBounds(7, 7, 550, 340);
		contentPane.add(splashImage);

		dispLine = new JLabel("Loading...");
		contentPane.setLayer(dispLine, 8);
		dispLine.setFont(new Font("Dialog", Font.BOLD, 14));
		dispLine.setBounds(12, 352, 542, 20);
		dispLine.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(dispLine);
		bLoad = new JProgressBar();
		bLoad.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 13));
		contentPane.setLayer(bLoad, 8);
		bLoad.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		bLoad.setBounds(8, 397, 549, 20);
		bLoad.setForeground(Color.BLACK);
		bLoad.setOpaque(false);
		bLoad.setStringPainted(true);
		bLoad.setString("Please Wait...");
		bLoad.setIndeterminate(true);
		contentPane.add(bLoad);

		setCameraPanel(new JFadeLabel());
		getCameraPanel().setAlpha(0.8f);
		contentPane.setLayer(getCameraPanel(), 6);
		getCameraPanel().setBounds(113, 21, 320, 240);
		contentPane.add(getCameraPanel());

		button_X = new JButton("");
		button_X.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						setVisible(false);
						while (isVisible()) {
							dispLine.setText("Exiting...");
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}
						}
						MoodyPlayer.exit();
					}
				}).start();
			}
		});
		button_X.setIcon(new ImageIcon(Splash.class
				.getResource("/moodplayer/img/x_32.png")));
		contentPane.setLayer(button_X, 7);
		button_X.setBounds(528, 6, 32, 32);
		contentPane.add(button_X);

		button_info = new JButton("");
		button_info.setIcon(new ImageIcon(Splash.class
				.getResource("/net/sf/fmj/ui/images/Information24.gif")));
		contentPane.setLayer(button_info, 7);
		button_info.setBounds(495, 6, 32, 32);
		contentPane.add(button_info);

		button_yes = new JButton("");
		contentPane.setLayer(button_yes, 4);
		button_yes.setBounds(174, 319, 90, 28);
		button_yes.setVisible(false);
		contentPane.add(button_yes);

		button_no = new JButton("");
		contentPane.setLayer(button_no, 4);
		button_no.setBounds(276, 319, 90, 28);
		button_no.setVisible(false);
		contentPane.add(button_no);

		dispLine1 = new JLabel("");
		dispLine1.setFont(new Font("Dialog", Font.BOLD, 14));
		dispLine1.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.setLayer(dispLine1, 4);
		dispLine1.setBounds(12, 297, 542, 20);
		contentPane.add(dispLine1);
		
		JLabel en_logo = new JLabel("");
		en_logo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.echonest.com/"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		en_logo.setIcon(new ImageIcon(Splash.class.getResource("/moodplayer/api/en_logo_powered_transparent.png")));
		contentPane.setLayer(en_logo, 10);
		en_logo.setBounds(350, 366, 200, 32);
		contentPane.add(en_logo);
		
		JLabel label = new JLabel("");
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("http://orbe.us/"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		label.setIcon(new ImageIcon(Splash.class.getResource("/moodplayer/api/reKognition_logo.png")));
		contentPane.setLayer(label, 10);
		label.setBounds(17, 366, 154, 32);
		contentPane.add(label);
	}

	public void updateCamPanel(BufferedImage img) {
		getCameraPanel().setIcon(new ImageIcon(img));
	}

	@Override
	public void setVisible(final boolean b) {
		if (!isVisible() && !b)
			return;
		super.setVisible(true);
		setOpacity(b ? 0 : 1);
		fader = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float newOpac = getOpacity() + (b ? 0.05f : -0.05f);
				if (newOpac > 1) {
					setOpacity(1);
					((Timer) e.getSource()).stop();
				} else if (newOpac < 0) {
					setOpacity(0);
					if (!b)
						setVisible(false);
					((Timer) e.getSource()).stop();
				} else {
					setOpacity(newOpac);
				}
			}
		});
		fader.start();
	}

	@Override
	public boolean isVisible() {
		if (getOpacity() == 0)
			return false;
		return super.isVisible();
	}

	public void setText(String s) {
		dispLine.setText(s);
	}

	public JFadeLabel getCameraPanel() {
		return cameraPanel;
	}

	public void setCameraPanel(JFadeLabel cameraPanel) {
		this.cameraPanel = cameraPanel;
	}

	public void showChoice(String line1, String line2, String yesBtn,
			final Runnable yesAction, String noBtn, final Runnable noAction) {

		dispLine1.setText(line1);
		dispLine.setText(line2);

		button_yes.setText(yesBtn);
		button_yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						yesAction.run();
					}
				}).start();

				button_yes.setVisible(false);
				button_yes.setText("");
				button_yes.removeActionListener(this);
			}
		});
		button_yes.setVisible(true);

		button_no.setText(noBtn);
		button_no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						noAction.run();
					}
				}).start();
				button_no.setVisible(false);
				button_no.setText("");
				button_no.removeActionListener(this);
			}
		});
		button_no.setVisible(true);

	}
}
