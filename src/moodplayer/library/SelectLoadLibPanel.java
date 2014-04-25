package moodplayer.library;

import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import moodplayer.util.SettingsManager;
import moodplayer.util.UtilBox;

/**
 * Subsection of the Setup Wizard and the Settings Manager Layered Pane.
 * @author Ionicle
 *
 */
public class SelectLoadLibPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JButton button_add;
	private JButton button_remove;
	private ProgressObserver progress_scan;
	private JPanel panel_progress;
	private JScrollPane scroll_locations;
	private List list_locations;
	private JPanel panel_locations;
	private JButton button_background;
	private JButton button_scan;
	
	public SelectLoadLibPanel() {
		setLayout(null);
		
		panel_locations = new JPanel();
		panel_locations.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Library Locations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_locations.setBounds(10, 10, 478, 218);
		add(panel_locations);
		panel_locations.setLayout(null);
		
		scroll_locations = new JScrollPane();
		scroll_locations.setBounds(16, 24, 444, 134);
		panel_locations.add(scroll_locations);
		
		list_locations = new List();
		list_locations.setFont(new Font("Dialog", Font.BOLD, 12));
		updateList();
		scroll_locations.setViewportView(list_locations);
		
		button_add = new JButton("Add Location...");
		button_add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Open File Selection Dialog To Select Folder
				String loc=UtilBox.openFile();
				if(loc==null){
					System.err.println("Location Null");
					return;
				}
				//TODO Add Folder Location To The List In MediaLibrary
				SettingsManager.libLocations().add(loc);
				
				//TODO Update The List
				updateList();
				
				//TODO Silent Update The MediaLibrary On Save
				MediaLibrary.update();
				button_background.setVisible(true);
				button_scan.setText("Stop Scanning");
			}
		});
		button_add.setBounds(16, 170, 134, 28);
		panel_locations.add(button_add);
		
		button_remove = new JButton("Remove Selected");
		button_remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opt=JOptionPane.showConfirmDialog(null,
					"Are you sure you want to remove this location from the index?",
					"Confirm Removal",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
				);
				if(opt==JOptionPane.YES_OPTION){
					//TODO Remove The Selected Folder From The List in MediaLibrary
					int pos=list_locations.getSelectedIndex();
					String rem=SettingsManager.libLocations().remove(pos);
					
					//TODO Update The List
					updateList();
					
					//TODO Silent Remove All The Songs In The Removed Folder From The MediaLibrary On Save.
					MediaLibrary.purge(rem);
				}
			}
		});
		button_remove.setBounds(162, 170, 134, 28);
		panel_locations.add(button_remove);
		
		panel_progress = new JPanel();
		panel_progress.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Scan Progress", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_progress.setBounds(10, 240, 478, 109);
		add(panel_progress);
		//panel_progress.setVisible(false);
		panel_progress.setLayout(null);
		
		progress_scan = new ProgressObserver();
		MediaLibrary.addObserver(progress_scan);
		progress_scan.setFont(new Font("SansSerif", Font.BOLD, 13));
		progress_scan.setStringPainted(true);
		progress_scan.setBounds(10, 20, 460, 26);
		panel_progress.add(progress_scan);
		
		button_background = new JButton("Background");
		button_background.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(button_background.getText().equals("Background")){
					MediaLibrary.background(true);
					button_background.setText("Foreground");
				}
				else{
					MediaLibrary.background(false);
					button_background.setText("Background");
				}
			}
		});
		button_background.setBounds(136, 58, 161, 28);
		button_background.setVisible(false);
		panel_progress.add(button_background);
		
		button_scan = new JButton("Scan");
		button_scan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(button_scan.getText().equals("Scan")){
					MediaLibrary.update();
					button_background.setVisible(true);
					button_scan.setText("Stop Scanning");
				}
				else{
					MediaLibrary.stopUpdate();
					
					try {MediaLibrary.completeSave();}
					catch (IOException x) {}
					
					button_background.setVisible(false);
					button_scan.setText("Scan");
				}
			}
		});
		button_scan.setBounds(309, 58, 161, 28);
		panel_progress.add(button_scan);
		
	}
	
	private void updateList() {
		list_locations.removeAll();
		for(String s:SettingsManager.libLocations()){
			list_locations.add(s);
		}
		list_locations.repaint();
		
	}
	
}

class ProgressObserver extends JProgressBar implements Observer{
	private static final long serialVersionUID = 1L;
	
	public ProgressObserver(){
		super();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof LibScanner){
			LibScanner curr=(LibScanner)o;
			int[] v=(int[])arg;
			this.setString(v[0]+"/"+v[1]+" ["+curr.currFolderName()+"]");
			this.setMaximum(v[1]);
			this.setValue(v[0]);
		}
		
	}
	
}
