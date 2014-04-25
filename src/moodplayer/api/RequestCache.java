package moodplayer.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import moodplayer.models.APIRequest;
import moodplayer.models.streamer.Streamable;

public class RequestCache {
	
	public static File sFile;
	
	@SuppressWarnings("unchecked")
	public static void load(){
		try{
			FileInputStream i=new FileInputStream(sFile);
			ObjectInputStream ois=new ObjectInputStream(i);
			APIR.requests=(ArrayList<APIRequest>)ois.readObject();
			ois.close();
			i.close();
		}catch(Exception e){
			APIR.requests = new ArrayList<APIRequest>();
			save();
		}
	}
	
	public static boolean save(){
		try {
			sFile.delete();
			sFile.createNewFile();
			FileOutputStream o = new FileOutputStream(sFile);
			ObjectOutputStream oos=new ObjectOutputStream(o);
			oos.writeObject(APIR.requests);
			oos.close();
			return true;
		} catch (Exception e) {return false;}
	}

	public static void init() {
		sFile = new File("request.cache");
		if (!sFile.exists()) {
			try {
				sFile.createNewFile();
				APIR.requests = new ArrayList<APIRequest>();
				save();
			} catch (IOException e) {}
		}
		else{
			load();
		}
	}

	public static class APIR {

		private static ArrayList<APIRequest> requests;

		public static APIRequest get(String id){
			try{
				if (requests == null)
					return null;
				Collections.sort(requests);
				int pos = Collections.binarySearch(requests, id);
				if (pos < 0)
					return null;
				APIRequest ar = requests.get(pos);
				if (ar.getId().equals(id))
					return ar;
				return null;
			}catch(Exception e){
				flush();
				return null;
			}
		}

		public static boolean add(String id, ArrayList<Streamable> json) {
			if(id==null||id.equals("")||json==null||json.size()==0) return false;
			if (requests == null)
				requests = new ArrayList<APIRequest>();
			APIRequest exist = get(id);
			if (exist != null) {
				exist.setList(json);
				return true;
			}
			if(requests.size()>100){
				for(int i=0;i<5;i++)
					requests.remove(0);
				System.gc();
			}
			boolean added = requests.add(new APIRequest(id, json));
			return added;
		}
		
		public static ArrayList<Streamable> find(String id){
			if(id==null) return null;
			APIRequest r=RequestCache.APIR.get(id);
			if(r!=null){
				return r.getList();
			}
			return null;
		}

		public static void flush() {
			requests.removeAll(requests);
			System.gc();
		}

		public static Iterator<APIRequest> iterate() {
			if (requests == null)
				return null;
			return requests.iterator();
		}

	}

}
