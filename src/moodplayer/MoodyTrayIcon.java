package moodplayer;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MoodyTrayIcon {

	private static TrayIcon trayIcon = null;
	private Image currIcon = null;

	private ActionListener defAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			new Thread(new Runnable() {
				public void run() {
					// If Media Player is already visible, show Mood Monitor
					if(!MoodyPlayer.showPlayer()){
						MoodyPlayer.showMeter();
					}
				}
			});
		}
	};

	public MoodyTrayIcon() {
		if (trayIcon != null)
			return;

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(
					MoodyPlayer.class.getResource("img/MoodyPlayer.png"));
			
			ActionListener exitAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MoodyPlayer.exit();
				}
			};

			final PopupMenu popup = new PopupMenu();
			// create menu item for the default action
			MenuItem showApp = new MenuItem("Show Moody Player");
			showApp.addActionListener(defAction);
			popup.add(showApp);

			MenuItem addDl = new MenuItem("Show Mood Monitor");
			addDl.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					MoodyPlayer.showMeter();
				}
			});
			popup.add(addDl);
			
			MenuItem showSettings = new MenuItem("Settings");
			showSettings.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					MoodyPlayer.showSettings();
				}
			});
			popup.add(showSettings);

			popup.setFont(new Font("Dialog", Font.BOLD, 12));

			MenuItem exitApp = new MenuItem("Exit");
			exitApp.addActionListener(exitAction);
			popup.add(exitApp);
			
			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Moody Player", popup);
			trayIcon.addActionListener(defAction);
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
			
		} else {
			throw new RuntimeException("System Tray Not Supported");
		}
	}

	public void setIcon(Image i) {
		trayIcon.setImage(i);
	}

	public void loading() {
		final Toolkit def = Toolkit.getDefaultToolkit();
		currIcon = trayIcon.getImage();
		trayIcon.setImage(def.getImage(
			MoodyPlayer.class.getResource("img/ajax-loader-48.gif"))
		);
	}

	public void stopLoading() {
		if (currIcon == null)
			return;
		trayIcon.setImage(currIcon);
	}

	public void actionRemove() {
		for (ActionListener a : trayIcon.getActionListeners()) {
			trayIcon.removeActionListener(a);
		}
	}

	public void popupMessage(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.NONE);
				if (clickAction != null)
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				else
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void popupInfo(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
				if (clickAction != null) {
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				} else
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void popupWarning(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text,
						TrayIcon.MessageType.WARNING);
				if (clickAction != null) {
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				} else
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void popupError(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.ERROR);
				if (clickAction != null) {
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				} else
					trayIcon.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}
}
