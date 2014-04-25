package moodplayer.models.streamer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import moodplayer.api.StreamDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SeekResult extends StreamResult implements Streamable{
	private static final long serialVersionUID = 1L;
	
	private String obstURL;
	
	public SeekResult(String title, String obstURL) {
		super(title, null);
		this.obstURL=obstURL;
	}
	
	public String getPage(){
		return obstURL;
	}
	
	public boolean loadURL() throws IOException{
		if(url!=null) return true;
		try{
			String result=StreamDownloader.getStringStream(obstURL);
			Document doc=Jsoup.parse(result);
			
			String url=doc.getElementsByClass("line0").get(4).getElementsByTag("a").get(0).attr("href");
			new URL(url);
			this.url=url;
			return true;
		}catch(MalformedURLException e){
			e.printStackTrace();
			return false;
		}catch(IOException e){throw e;}
		
	}
	
	@Override
	public String getUrl(){
		try{
			if(loadURL()) return url;
			return null;
		}catch(IOException e){
			return null;
		}
	}

	@Override
	public String toString() {
		return "SeekResult [" + (obstURL != null ? "obstURL=" + obstURL + ", " : "")
				+ (state != null ? "state=" + state + ", " : "")
				+ (title != null ? "title=" + title + ", " : "")
				+ (url != null ? "url=" + url : "") + "]";
	}

}
