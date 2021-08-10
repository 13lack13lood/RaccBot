package tools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Tool {
	public static String BOTCONFIG = "./src/main/resources/settings.botconfig";
	public static String MUSICCHANNELS = "./src/main/resources/musicchannels.botconfig";
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
	// Search through the list to find the channel
	public static GuildChannel searchChannel(List<GuildChannel> list, String pattern) {
		if(list.isEmpty())
			return null;
		// Loop through all the channels
		for(GuildChannel c : list) {
			if(c.getName().toLowerCase().contains(pattern.toLowerCase())) {
				return c;
			}
		}
		
		return null;
	}
	// Channel search with channelid
	// Search through the list to find the role
	public static Role searchRole(List<Role> list, String pattern) {
		if(list.isEmpty())
			return null;
		// Loop through all the channels
		for(Role r : list) {
			if(r.getName().toLowerCase().contains(pattern.toLowerCase())) {
				return r;
			}
		}
		
		return null;
	}
	// Search through the list to find the role
	public static Role searchRole(List<Role> list, String pattern, int number) {
		if(list.isEmpty())
			return null;
		// Loop through all the channels
		List<Role> results = new ArrayList<Role>();
		for(Role r : list) {
			if(r.getName().toLowerCase().contains(pattern.toLowerCase())) {
				results.add(r);
			}
		}
		// Find the role with given index
		if(!results.isEmpty()) {
			// Check if index > # of results returned
			if(number > results.size()) {
				return results.get(results.size() - 1);
			} else if(number > 0) {
				return results.get(number - 1);
			}
		}

		return null;
	}
	
	public static boolean checkMusicCommandChannel(GuildMessageReceivedEvent event) {
		long serverid = event.getGuild().getIdLong();
		if(BotConfig.getMusicChannels(serverid) != null && event.getChannel().getIdLong() != BotConfig.getMusicChannels(serverid)) {
			event.getChannel().sendMessage("You can only use music commands in " + event.getGuild().getGuildChannelById(BotConfig.getMusicChannels(serverid)).getName()).queue();
			return false;
		}
		return true;
	}
}
