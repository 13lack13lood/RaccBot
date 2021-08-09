package tools;

import java.awt.Color;
import java.util.Random;

public class Tool {
	public static String BOTCONFIG = "./src/main/resources/settings.botconfig";
	// Method to generate random color
	public static Color randomColor() {
		Random random = new Random();
		// Generate random RGB values
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();
		// Return the color with the random floats
		return new Color(r, g, b);
	}
	// Combine the arguments into 1 string
	public static String combine(String[] args, int start) {
		String out = "";
		// Loop through all the args
		for(int i = start; i < args.length - 1; i++) {
			out += args[i] + " ";
		}
		// Last args don't add space
		out += args[args.length - 1];
		
		return out;
	}
	// Combine the arguments into 1 string
	public static String combine(String[] args, int start, int end) {
		String out = "";
		// Loop through all the args
		for(int i = start; i < end - 1; i++) {
			out += args[i] + " ";
		}
		// Last args don't add space
		out += args[end - 1];
		
		return out;
	}
	// Check if string is a number
	public static boolean isNumber(String string) {
		if(string.replaceAll("[0-9]", "").length() > 0) {
			return false;
		}
		
		return true;
	}
	// Convert long (ms) to mm:ss
	public static String convertTime(long n) {
		long seconds = n / 1000;
		long minutes = seconds / 60;
		seconds -= (minutes * 60);
		return Long.toString(minutes) + ":" + Long.toString(seconds);
	}
}
