package commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class ServerChannels extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(ServerChannels.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("serverchannels")) {
				if(args.length > 2 && args[2].equalsIgnoreCase("help")) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Server Channels Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " serverchannels`");					
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					// Get server from message
					Guild server = event.getMessage().getGuild();
					// Message Embed
					EmbedBuilder info = new EmbedBuilder();
					info.setColor(Tool.randomColor());
					info.setThumbnail(server.getIconUrl());
					info.setTitle("All Channels on " + server.getName());
					info.setDescription("Channels (Sorted by Position):");
					// Get all the channels
					String[] channels = getChannels(server.getChannels());
					for(int i = 0; i < channels.length; i++) {
						info.addField("", channels[i], false);
					}
					// Send message
					event.getChannel().sendMessageEmbeds(info.build()).queue();
					// Save resources by clearing builder
					info.clear();
					
					LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
				}
			}
		}
	}
	// Get all the channels in the server
	private String[] getChannels(List<GuildChannel> list) {
		String[] channels = new String[list.size()];
		int total = 0;
		// Check if the server has no channels
		if(list.isEmpty()) {
			return new String[] {"No Channels."};
		}
		// Add all the channels to the string
		for(int i = 0; i < list.size(); i++) {
			GuildChannel channel = list.get(i);
			if(channel.getType() == ChannelType.CATEGORY) {
				channels[i] = "\n**" + channel.getName() + "**\n";
			} else {
				channels[i] = "`" + channel.getName() + " (" + channel.getType().toString().toLowerCase() + ")` ";
			}
			// Add length to total
			total += 4 + channel.getName().length() + channel.getType().toString().length();
		}
		// Make a new array and add all the strings together, make a new string when length > 1024
		String[] output = new String[(total / 1096) + 1];
		// Create a new string in each element (avoid nullptr)
		for(int i = 0; i < output.length; i++) {
			output[i] = "";
		}
		// Loop through all the strings and keep track of which field value it is on
		int field = 0;
		for(int i = 0; i < channels.length; i++) {
			// Current string
			String current = channels[i];
			// Check if the current output string is too large
			if(output[field].length() + current.length() > 1024) {
				field++;
			}
			// Add string to output
			output[field] += current;
		}
		
		return output;
	}
}
