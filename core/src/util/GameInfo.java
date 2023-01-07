package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GameInfo {

	private static short last_fps;
	private static long time_elapsed;
//	private static long frame_time;
	private static float delta_time;
	
	public static void set_last_fps(short fps) {last_fps = fps;}
	public static short get_last_fps() {return last_fps;}
	
//	public static void set_frame_time(long ft) {
//		frame_time = ft;
//	}
	
//	public static long get_frame_time() {
//		return frame_time;
//	}
	
	public static long get_time_millis() {
		return time_elapsed;
	}

	public static float get_delta_time() {
		return delta_time;
	}
	
	public static void set_time(short time) {
		last_fps = time;
		delta_time = ((float)time)/1000f;
		time_elapsed += time;
		
	}
	
	public static byte ang2b(float angle) {
		angle = (int)Math.floor(angle);
		return (byte)((angle+180)/2 -128);
	}
	
	public static float b2ang(byte angle) {
		return (angle+128)*2-180;
	}
	
	public static int f2ideg2(float val) {
		return (int)(val*100);
	}
	
	public static float i2fdeg2(int val) {
		return (((float)val)/100f);
	}
	
	public static float f2deg(float val) {
		return ((float)
				((int)(val*100))
				)/100f;
	}
	
	 public static byte[] readAllBytes(InputStream inputStream) throws IOException {
		    final int bufLen = 1024;
		    byte[] buf = new byte[bufLen];
		    int readLen;
		    IOException exception = null;

		    try {
		        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		        while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
		            outputStream.write(buf, 0, readLen);

		        return outputStream.toByteArray();
		    } catch (IOException e) {
		        exception = e;
		        throw e;
		    } finally {
		        if (exception == null) inputStream.close();
		        else try {
		            inputStream.close();
		        } catch (IOException e) {
		            exception.addSuppressed(e);
		        }
		    }
		}
}
