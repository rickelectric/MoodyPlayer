package moodplayer.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class UtilBox {
	
	private static Random rand;
	
	static{
		rand=new Random(System.currentTimeMillis());
	}

	public static int getRandomNumber(int x){
		return rand.nextInt(x);
	}

	public static void addKeyListenerToAll(Component parent,
			KeyListener listener) {
		if (parent instanceof AbstractButton) {
			AbstractButton a = (AbstractButton) parent;
			// Check If The Listener is Already There (Avoid Double Reactions)
			boolean is = false;
			KeyListener[] kl = a.getKeyListeners();
			for (KeyListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addKeyListener(listener);
		}

		else if (parent instanceof JComponent) {
			JComponent a = (JComponent) parent;
			// Check If The Listener is Already There (Avoid Double Reactions)
			boolean is = false;
			KeyListener[] kl = a.getKeyListeners();
			for (KeyListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addKeyListener(listener);
		}

		if (parent instanceof Container) {
			Component[] comps = ((Container) parent).getComponents();
			for (Component c : comps) {
				addKeyListenerToAll(c, listener);
			}
		}
	}

	public static void addMouseListenerToAll(Component parent,
			MouseListener listener) {
		if (parent instanceof AbstractButton) {
			AbstractButton a = (AbstractButton) parent;
			boolean is = false;
			MouseListener[] kl = a.getMouseListeners();
			for (MouseListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addMouseListener(listener);
		}

		else if (parent instanceof JComponent) {
			JComponent a = (JComponent) parent;
			boolean is = false;
			MouseListener[] kl = a.getMouseListeners();
			for (MouseListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addMouseListener(listener);
		}

		if (parent instanceof Container) {
			Component[] comps = ((Container) parent).getComponents();
			for (Component c : comps) {
				addMouseListenerToAll(c, listener);
			}
		}
	}

	public static String openFile() {
		JFileChooser fc = null;

		fc = new JFileChooser(System.getProperty("user.home"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int returnVal = fc.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (file.getPath().equals(""))
				return null;
			return file.getAbsolutePath();
		}
		return null;
	}
}
