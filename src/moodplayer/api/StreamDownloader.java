package moodplayer.api;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class StreamDownloader {
	
	public static String getStringStream(String url) throws IOException,MalformedURLException{ //Echonest REST
		int batchWriteSize=512;
		OutputStream out = new ByteArrayOutputStream();
		GetMethod get = new GetMethod(url);
		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout((int) (8000));
		params.setParameter("User-Agent", "Mozilla/5.0");
		params.setParameter("Accept-Language", "en-US,en;q=0.5");
		client.setParams(params);

		try {
			client.executeMethod(get);
		} catch (ConnectException e) {
			out.close();
			throw new IOException("ConnectionException trying to GET "
					+ url, e);
		}

		if (get.getStatusCode() != 200) {
			out.close();
			throw new FileNotFoundException("Server returned "
					+ get.getStatusCode());
		}
		
		/*
		Header[] hs=get.getResponseHeaders();
		System.out.println("------------------------");
		for(Header h:hs){
				System.out.print(h.toString());
		}
		System.out.println("------------------------");
		
		/*
		List<String> cookies = con.getHeaderFields().get("Set-Cookie");
		for (String s : cookies) {
			if (s.split("=")[0].equalsIgnoreCase("JSESSIONID"))
				nextCookie = s.split("=")[1];

		}*/
		
		BufferedInputStream bis = new BufferedInputStream(
				get.getResponseBodyAsStream());

		byte[] b = new byte[batchWriteSize];
		int bytesRead = bis.read(b, 0, batchWriteSize);
		while (bytesRead != -1) {
			out.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, batchWriteSize);
		}
		bis.close();
		String s = out.toString();
		out.flush();
		out.close();
		return s;
	}

	public static String postDataPartStream(String fileURL, Part[] parts) {

		HttpClient httpclient = new HttpClient();

		try {
			PostMethod filePost = new PostMethod(fileURL);

			filePost.setRequestEntity(new MultipartRequestEntity(parts,
					filePost.getParams()));

			int response = httpclient.executeMethod(filePost);
			
			if(response<200||response>299) throw new IOException("HTTP Response "+response);
			
			InputStream s = filePost.getResponseBodyAsStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] b = new byte[512];
			int bytesRead = s.read(b, 0, 512);
			while (bytesRead != -1) {
				os.write(b, 0, bytesRead);
				bytesRead = s.read(b, 0, 512);
			}
			s.close();
			String re = os.toString();
			os.close();
			return re;
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Pings the specified web address or ip address.
	 * @param address URL to ping
	 * @return round trip latency time(in ms)
	 */
	public static long ping(String address) throws IOException{
		try{
			if(!address.contains("http://")) address="http://"+address;
			
			URL url = new URL(address);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(10000);
			long startTime = System.currentTimeMillis();
			urlConn.connect();
			long endTime = System.currentTimeMillis();
			int rCode=urlConn.getResponseCode();
			if(rCode>=200&&rCode<=299) {
				long time=(endTime - startTime);
				return time;
			}
			else{
				return -1*rCode;
			}
		}catch (MalformedURLException e1){
			return -1;
		}catch (IOException e){
			System.err.println("Connection Failed");
			throw new IOException("Connection Failed");
		}
	}

}
