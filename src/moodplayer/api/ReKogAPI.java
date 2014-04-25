package moodplayer.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import moodplayer.data.DefaultParams;
import moodplayer.models.APIFace;

import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.xerces.impl.dv.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReKogAPI {

	public static APIFace[] reKogFaceDetect(BufferedImage img){
		try{
			String key=DefaultParams.REKOGNITION_API_KEY;
			String secret=DefaultParams.REKOGNITION_API_SECRET;
			String jobs="face_part_gender_emotion_race_age_mouth_open_wide_eye_closed";
			String json=ReKogAPI.postImage(key,secret,jobs,img);
			JSONObject o=new JSONObject(json);
			//System.out.println(o.toString(4));
			JSONArray faces=o.getJSONArray("face_detection");
			if(faces.length()==0) return null;
			APIFace[] f=new APIFace[faces.length()];
			for(int i=0;i<faces.length();i++){
				f[i]=new APIFace(faces.getJSONObject(i));
			}
			return f;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static String postImage(String key,String secret,String jobs,BufferedImage img) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		ImageIO.write(img, "jpg", baos);
		baos.flush();

		String base64String = Base64.encode(baos.toByteArray());
		baos.close();
		
		Part[] parts = {
				new StringPart("api_key", key),
				new StringPart("api_secret", secret),
				new StringPart("jobs", jobs),
				new StringPart("base64", base64String) };

		String stream = StreamDownloader.postDataPartStream(
				"http://rekognition.com/func/api/",
				parts);

		return stream;
	}
	
}
