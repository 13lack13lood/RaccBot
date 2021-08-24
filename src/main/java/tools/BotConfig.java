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
	// String[] to store all the commands
	public static String[] BOTCOMMANDS;
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
		
		BOTCOMMANDS = getBotCommands();
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
	
	/**
	 * get all the commands that the bot has
	 * @return array[0] = normal commands
	 * <p>
	 * array[1] = music commands
	 * <p>
	 * array[2] = misc commands
	 */
	private static String[] getBotCommands() {
		String[] commands = new String[3]; // [0] is commands, [1] is music, [2] is misc
		
		for(int i = 0; i < commands.length; i++) {
			commands[i] = "";
		}
		
		File folder = new File("./src/main/java/commands");
		File[] files = folder.listFiles();
		// Get all the files, add to the corresponding string
		for(File file : files) {
			String name = file.getName().toLowerCase().replaceAll(".java", "");
			if(name.startsWith("hidden")) {
				continue;
			} else if(name.startsWith("music")) {
				name = name.substring(5) + ", ";
				commands[1] += name;
			} else if(name.startsWith("misc")) {
				name = name.substring(4) + ", ";
				commands[2] += name;
			} else {
				commands[0] += name + ", ";
			}
		}
		// remove trailing comma + space
		commands[0] = commands[0].substring(0, commands[0].length() - 2);
		commands[1] = commands[1].substring(0, commands[1].length() - 2);
		commands[2] = commands[2].substring(0, commands[2].length() - 2);
		
		return commands;
	}
	
	public static String getToken() {
		String input = data.get("token");
		String[] keyInput = data.get("key").split(",");
		int[] key = new int[keyInput.length];
		
		for(int i = 0; i < keyInput.length; i++) {
			key[i] = Integer.parseInt(keyInput[i]);
		}
		
		int start = '!', end = '~';
		int letters = end - start;
		String output = "";
		
		for(int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			int val = c - '!';
			val += (key[i] * letters);
			val = (int) Math.sqrt(val);
			char letter = (char) val;
			output += letter;
		}
		
		return output;
	}
}
