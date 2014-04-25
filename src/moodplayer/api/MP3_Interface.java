package moodplayer.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moodplayer.exception.MoodAccessException;
import moodplayer.library.LocalArtist;
import moodplayer.library.LocalSong;
import moodplayer.library.MediaLibrary;
import moodplayer.models.streamer.LocalResult;
import moodplayer.models.streamer.SeekResult;
import moodplayer.models.streamer.StreamResult;
import moodplayer.models.streamer.Streamable;
import moodplayer.monitor.GlobalMoodModel;
import moodplayer.player.MediaStreamer;
import moodplayer.player.playlist.MPlaylist2;
import moodplayer.player.playlist.PlaylistRunner;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uk.co.caprica.vlcj.player.MediaPlayer;

import com.echonest.api.v4.Song;

public class MP3_Interface {

	public static final int SKULL = 0, EWORLD = 1, LEMON = 2, SEEK = 3;
	private static boolean skull = false, eworld = false, lemon = false,
			seek = false, checked = false;

	public static boolean[] getActiveSites() {
		if (checked == false)
			checkSites();
		return new boolean[] { skull, eworld, lemon, seek };
	}

	public static void main(String[] args) throws MalformedURLException,
			IOException {
		MediaLibrary.init();
		MediaStreamer.init();
		ArrayList<Streamable> results = localSearch("Faint", "Linkin Park");
		if (results == null)
			System.out.println("No Results Found");
		/*
		 * else for (Streamable r : results) { try { r.loadURL(); MediaPlayer m
		 * = MediaStreamer.playAudio(r.getUrl()); while (m.isPlaying())
		 * Thread.sleep(500); } catch (Exception e) { } } /
		 */
	}

	public void setEnabled(int site, boolean b) {
		switch (site) {
		case SKULL:
			skull = b;
			break;
		case EWORLD:
			eworld = b;
			break;
		case LEMON:
			lemon = b;
			break;
		case SEEK:
			seek = b;
			break;
		}
	}

	public static boolean checkSites() {
		checked = true;
		try {
			StreamDownloader.ping("http://www.mp3skull.com");
			skull = true;
		} catch (Exception e) {
		}
		try {
			StreamDownloader.ping("http://www.seekasong.com");
			seek = true;
		} catch (Exception e) {
		}
		/*
		 * try { StreamDownloader.ping("http://www.mp3lemon.com"); lemon = true;
		 * } catch (Exception e) {}
		 */
		try {
			StreamDownloader.ping("http://www.emp3world.com");
			eworld = true;
		} catch (Exception e) {
		}

		if (skull || seek || lemon || eworld)
			return true;
		return false;
	}

	protected static Song currSong = null;
	private static String searchStr;

	public static PlaylistRunner buildPlaylist(final List<Song> songs,
			MediaPlayer player) {

		final PlaylistRunner pl = new PlaylistRunner(player);

		new Thread(new Runnable() {
			public void run() {
				while (!Thread.interrupted()) {
					int numFails = 0;
					String searchStr = "";
					while (numFails <= 3) {

						try {
							currSong = MPlaylist2
									.list(GlobalMoodModel.getDominantMood()
											.getName()).remove(0);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Song s = currSong;
						ArrayList<Streamable> rs = find(s, searchStr);
						searchStr = s.getArtistName() + " " + s.getTitle();
						if (rs != null) {
							try {
								RequestCache.APIR.add(searchStr, rs);
								if (rs.size() > 1)
									Collections.shuffle(rs);
								for (Streamable r : rs) {
									if (pl.add((StreamResult) r))
										break;
								}
								RequestCache.save();
							} catch (Exception e) {
							}
						} else
							numFails++;
						while (pl.size() >= 10) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								break;
							}
						}
					}
					RequestCache.save();
					try {
						MPlaylist2.echoLoad();
					} catch (MoodAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		return pl;
	}

	public static ArrayList<Streamable> find(Song s, String previousSearch) {
		int engine = SKULL;
		String tmps = s.getArtistName() + " " + s.getTitle();
		if (previousSearch.equals(tmps))
			return null;
		searchStr = tmps;
		ArrayList<Streamable> rs = localSearch(s.getTitle(), s.getArtistName());
		if (rs != null)
			System.out.println("Local Library Hit!");
		else {
			rs = RequestCache.APIR.find(searchStr);
			if (rs != null)
				System.out.println("Cache Hit!");
			else {
				System.out.println("Cache Miss: Searching...");
				if (engine == SKULL) {
					System.out.println("Skull: " + searchStr);
					rs = skullSearch(searchStr);
					if (rs == null)
						engine++;
				}
				if (engine == EWORLD) {
					System.out.println("Eworld: " + searchStr);
					rs = eworldSearch(searchStr);
					if (rs == null)
						engine++;
				}
				if (engine == LEMON) {
					/*
					 * System.out.println("Lemon: " + searchStr); rs =
					 * lemonSearch(searchStr); if (rs == null) engine++;
					 */
					engine++;
				}
				if (engine == SEEK) {
					System.out.println("Seek: " + searchStr);
					rs = seekSearch(s.getArtistName(), s.getTitle());
					if (rs == null)
						engine++;
				}
			}
		}
		return rs;
	}

	public static ArrayList<Streamable> localSearch(String title, String artist) {

		LocalArtist a = MediaLibrary.find(artist);
		if (a == null)
			return null;
		else {
			System.out.println("Found " + artist + "\n: " + a);
		}

		LocalSong s = a.find(title);
		if (s == null)
			return null;
		else
			System.out.println("Found " + title);

		ArrayList<Streamable> l = new ArrayList<Streamable>();
		l.add(new LocalResult(s.getTitle() + " - " + s.getArtist(), s
				.getFilePath()));
		return l;
	}

	/**
	 * Searches mp3skull.com for mp3 music to stream.
	 * 
	 * @param query
	 *            The Artist - Song To Search For.
	 * @return A Collection of <u>unchecked</u> <b>StreamResult</b> objects.
	 *         (null if nothing is found)
	 */
	public static ArrayList<Streamable> skullSearch(String query) {
		if (!skull)
			return null;
		try {
			String url = "http://www.mp3skull.com/search.php?q="
					+ URLEncoder.encode(query, "utf-8");
			String result = StreamDownloader.getStringStream(url);
			Document doc = Jsoup.parse(result);
			ArrayList<Streamable> res = new ArrayList<Streamable>();
			Elements base = doc.getElementById("content")
					.getElementsByTag("div").get(0)
					.getElementsByAttributeValue("id", "song_html");

			if (base.size() == 0)
				return null;

			for (Element b : base) {
				Element rs = b.getElementById("right_song");
				String title = rs.getElementsByTag("b").get(0).text();
				title = title.split(" mp3")[0];

				if (currSong != null)
					title = currSong.getTitle() + " - "
							+ currSong.getArtistName();

				Element link = rs.getElementsByTag("a").get(0);
				String l = link.attr("href");
				StreamResult curr = new StreamResult(title, l);
				curr.setPage(url);
				res.add(curr);
			}
			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("UTF-8 Not Supported\n" + e.getMessage());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Searches www.seekasong.com for mp3 music to stream.
	 * 
	 * @param artist
	 *            The Artist To Search For.
	 * @param title
	 *            The Title Of The Song To Search For.
	 * 
	 * @return A Collection of <u>unchecked</u> <b>SeekResult</b> objects. (null
	 *         if nothing is found)
	 */
	public static ArrayList<Streamable> seekSearch(String artist, String title) {
		if (!seek)
			return null;
		if (artist == null)
			return null;
		try {
			Part[] parts = new Part[] {
					new StringPart("artist", URLEncoder.encode(artist, "utf-8")),
					new StringPart("title", title == null ? ""
							: URLEncoder.encode(title, "utf-8")),
					new StringPart("what", "mp3") };
			String url = "http://www.seekasong.com/searchseekasong.php";

			String result = StreamDownloader.postDataPartStream(url, parts);
			Document doc = Jsoup.parse(result);

			ArrayList<Streamable> res = new ArrayList<Streamable>();
			Elements base = doc.getElementsByAttributeValue("height", "60");

			if (base.size() == 0)
				return null;

			for (Element b : base) {
				Element a = b.getElementsByTag("a").get(0);
				String ttle = a.text();
				ttle = ttle.split(" mp3")[0];

				if (currSong != null)
					ttle = currSong.getTitle() + " - "
							+ currSong.getArtistName();

				String obstURL = "http://www.seekasong.com" + a.attr("href");
				res.add(new SeekResult(ttle, obstURL));
			}

			Collections.shuffle(res);
			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("UTF-8 Not Supported\n" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Searches mp3lemon.org for mp3 music to stream.
	 * 
	 * @param query
	 *            The Artist / Song To Search For.
	 * @return A Collection of <u>unchecked</u> <b>StreamResult</b> objects.
	 *         (null if nothing is found)
	 */
	public static ArrayList<Streamable> lemonSearch(String query) {
		if (!lemon)
			return null;
		try {
			String url = "http://mp3lemon.org/search.php?query_search="
					+ URLEncoder.encode(query, "utf-8");
			String result = StreamDownloader.getStringStream(url);

			Document doc = Jsoup.parse(result);
			ArrayList<Streamable> res = new ArrayList<Streamable>();
			Elements base = doc.getElementsByClass("list_tracks");

			if (base.size() == 0)
				return null;

			for (int i = 0; i < base.size(); i += 8) {
				String link = base.get(i).getElementsByTag("a").get(0)
						.attr("href");
				link = link.replaceAll(" ", "%20");
				link = link.replaceAll("\'", "%27");
				link = "http://mp3lemon.org" + link;
				String title = base.get(i + 3).text() + " - "
						+ base.get(i + 2).text();

				if (currSong != null)
					title = currSong.getTitle() + " - "
							+ currSong.getArtistName();

				StreamResult curr = new StreamResult(title, link);
				res.add(curr);
			}
			return res;
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("UTF-8 Not Supported\n" + e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Searches emp3world.com for mp3 music to stream.
	 * 
	 * @param query
	 *            The Artist - Song To Search For.
	 * @return A Collection of <u>unchecked</u> <b>StreamResult</b> objects.
	 *         (null if nothing is found)
	 */
	public static ArrayList<Streamable> eworldSearch(String query) {
		if (!eworld)
			return null;
		try {
			String url = "http://emp3world.com/r.php?phrase="
					+ URLEncoder.encode(query, "utf-8");
			String result = StreamDownloader.getStringStream(url);
			Document doc = Jsoup.parse(result);
			ArrayList<Streamable> res = new ArrayList<Streamable>();
			Elements base = doc.getElementsByClass("song_item");

			if (base.size() == 0)
				return null;

			for (Element b : base) {
				String title = b.getElementById("song_title").text();
				title = title.split(" mp3")[0];

				if (currSong != null)
					title = currSong.getTitle() + " - "
							+ currSong.getArtistName();

				String link = b.getElementsByTag("a").get(1).attr("href");
				StreamResult curr = new StreamResult(title, link);
				curr.setPage(url);
				res.add(curr);
			}
			return res;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("UTF-8 Not Supported\n" + e.getMessage());
		} catch (Exception e) {
			return null;
		}
	}

}
