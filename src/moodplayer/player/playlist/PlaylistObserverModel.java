package moodplayer.player.playlist;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.AbstractTableModel;

import moodplayer.models.streamer.StreamResult;

public class PlaylistObserverModel extends AbstractTableModel implements
		Observer {
	private static final long serialVersionUID = 1L;

	private static final String[] columnNames = { "Title", "Host", "Status" };

	private static final Class<?>[] columnClasses = { String.class,
			String.class, String.class };

	private PlaylistRunner pl;

	public PlaylistObserverModel(PlaylistRunner pl) {
		this.pl = pl;
		pl.addObserver(this);
		new Thread(pl).start();
	}

	public StreamResult getStream(int i) {
		return pl.get(i);
	}

	public int getColumnCount() {
		return columnNames.length;
	}
	
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
	}

	@Override
	public int getRowCount() {
		return pl.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		StreamResult s = pl.get(rowIndex);
		if (columnIndex == 0)
			return s.getTitle();
		else if (columnIndex == 1){
			String url=s.getUrl();
			try{
				URL u=new URL(url);
				return u.getHost();
			}catch(Exception e){
				return url;
			}
		}
		else if (columnIndex == 2)
			return s.getState();
		return null;
	}

	@Override
	public void update(Observable o, Object arg1) {
		if (o.equals(pl)) {
			int loc = (int) arg1;
			if (loc < 0) {
				loc = (-1 * loc) - 1;
				fireTableRowsDeleted(pl.size() - 1, pl.size());
			}
			fireTableRowsInserted(pl.size() - 1, pl.size() - 1);
		}
		if (o instanceof StreamResult) {
			fireTableRowsUpdated(0, pl.size()-1); 
		}

	}

	public void action() {
		
	}

	public JPopupMenu popup(int rowindex) {
		return new ContextMenu(rowindex);
	}

	class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		private JMenuItem download;
		int row;

		public ContextMenu(int i) {
			super();
			row = i;
			
			download = new JMenuItem("Download This Song");
			download.addActionListener(this);

			StreamResult.State state = getStream(i).getState();
			if (state == StreamResult.PLAYING) {
				add(download);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src.equals(download)) {
				try {
					StreamResult sr=pl.get(pl.getCurrent());
					
					Desktop.getDesktop().browse(new URI(sr.getPage()));
				} catch (IOException | URISyntaxException e1) {}
			}
		}

	}

}
