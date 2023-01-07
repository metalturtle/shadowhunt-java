package util;

import network.NetworkInfo;

public class Logger {

	public static boolean enabled=false;
	public static void log(String message) {
		if(enabled)
		System.out.println(message);
	}
	
	public static void log() {
		if(enabled)
		System.out.println();
		}
}
