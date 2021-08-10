package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BotConfig {
	// Bot config file
	private static File settings = new File(Tool.BOTCONFIG);
	// Music Channels file
	private static File musicchannels = new File(Tool.MUSICCHANNELS);
	// Scanner to read file
	private static Scanner scanner;
	// Map to store file data
	private static Map<String, String> data = new HashMap<String, String>();
	// Map to store music channels
	private static Map<Long, Long> musicChannels = new HashMap<Long, Long>();
	
	// Method to read the file
	public static void init() {
		try { // Read settings.botconfig
			// Read File
			scanner = new Scanner(settings);
			
			while(scanner.hasNextLine()) {
				// Get the line and split the name and data
				String[] line = scanner.nextLine().split(": ");
				// Add the data into the map
				if(!data.containsKey(line[0]))
					data.put(line[0], line[1]);
			}
			
			scanner.close();
		} catch(FileNotFoundException e) { // Error handling
			// File not found
			System.out.println("Bot Config file not found");
			e.printStackTrace();
		} catch(Exception e) {
			// Something else happened
			e.printStackTrace();
		}
		try {
			scanner = new Scanner(musicchannels);
			
			while(scanner.hasNextLine()) {
				// Get the line and split the name and data
				String[] line = scanner.nextLine().split(":");
				Long server = Long.parseLong(line[0]);
				Long channel = Long.parseLong(line[1]);
				// Add the data into the map
				if(!musicChannels.containsKey(server))
					musicChannels.put(server, channel);
			}
			
			scanner.close();
		} catch(FileNotFoundException e) {
			System.out.println("Music Channels file not found");
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Method to retrieve data
	public static String getData(String name) {
		return data.get(name);
	}

	public static Long getMusicChannels(long guildID) {
		return musicChannels.get(guildID);
	}
	
	public static void setMusicChannels(long guildID, long channelID, boolean remove) {
		if(remove && musicChannels.containsKey(guildID)) {
			musicChannels.remove(guildID);
		} else if(!remove) {
			musicChannels.put(guildID, channelID);
		}
		
		String tempFile = Tool.MUSICCHANNELS.replace("musicchannels", "temp");
		
		File newFile = new File(tempFile);
		String guild = Long.toString(guildID);
		String channel = Long.toString(channelID);
		String oldGuild = "", oldChannel = "";
		
		try {
			FileWriter fw = new FileWriter(tempFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			scanner = new Scanner(musicchannels);
			if(musicChannels.containsKey(guildID)) {
				scanner.useDelimiter("[:\n]");
				while(scanner.hasNext()) {
					oldGuild = scanner.next();
					oldChannel = scanner.next();
					
					if(oldGuild.equals(guild)) {
						if(!remove) {
							pw.println(guild + ":" + channel);
						}
					} else {
						pw.print(oldGuild + ":" + oldChannel);
					}
				}
			} else {
				pw.println(guild + ":" + channel);
			}
			scanner.close();
			pw.flush();
			pw.close();
			musicchannels.delete();
			File dump = new File(Tool.MUSICCHANNELS);
			newFile.renameTo(dump);
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
}
