package moodplayer.data;

public class DefaultParams {

	public static final String 
	
		LIBVLC_PATH = ".\\libvlc\\"+((System.getProperty("os.arch").contains("64"))?"64":".\\32"),
		
		ECHONEST_API_KEY = "XFPI6HFNJM7OGMBPV",
		
		REKOGNITION_API_KEY="HKLIfOQk250gDoFh",
		REKOGNITION_API_SECRET="rrvbnfINoPyA8MKs";
	
	public static final int HAAR_FACE_DETECT_MIN = 1;
	public static final int HAAR_FACE_DETECT_MAX = 20;

	public static final String MEDIA_LIB = System.getProperty("user.home")+"\\Music";
	
	
}
