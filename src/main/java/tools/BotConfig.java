package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BotConfig {
	// Bot config file
	private static File settings = new File(Tool.BOTCONFIG);
	// Scanner to read file
	private static Scanner scanner;
	// Map to store file data
	private static Map<String, String> data = new HashMap<String, String>();
	
	// Method to read the file
	public static void init() {
		try {
			// Read File
			scanner = new Scanner(settings);
			
			while(scanner.hasNextLine()) {
				// Get the line and split the name and data
				String[] line = scanner.nextLine().split(": ");
				// Add the data into the map
				if(!data.containsKey(line[0]))
					data.put(line[0], line[1]);
			}
		} catch(FileNotFoundException e) { // Error handling
			// File not found
			System.out.println("Bot Config file not found");
			e.printStackTrace();
		} catch(Exception e) {
			// Something else happened
			e.printStackTrace();
		}
	}
	
	// Method to retrieve data
	public static String getData(String name) {
		return data.get(name);
	}
}
