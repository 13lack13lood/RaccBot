package commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class ServerRoles extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(ServerRoles.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("serverroles")) {
				if(args.length > 2 && args[2].equalsIgnoreCase("help")) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Server Roles Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " serverroles`");					
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
					info.setTitle("All Roles on " + server.getName());
					info.setDescription("Roles (Sorted by Position):");
					// Get all the channels
					String[] roles = getRoles(server.getRoles());
					for(int i = 0; i < roles.length; i++) {
						info.addField("", roles[i], false);
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
	private String[] getRoles(List<Role> list) {
		String[] roles = new String[list.size()];
		int total = 0;
		// Check if the server has no roles
		if(list.isEmpty()) {
			return new String[] {"No roles"};
		}
		// Add all the roles to the string
		for(int i = 0; i < list.size(); i++) {
			roles[i]= "`" + list.get(i).getName() + "` ";
			total += 3 + list.get(i).getName().length();
		}

		// Make a new array and add all the strings together, make a new string when length > 1024
		String[] output = new String[(total / 1096) + 1];
		// Create a new string in each element (avoid nullptr)
		for(int i = 0; i < output.length; i++) {
			output[i] = "";
		}
		// Loop through all the strings and keep track of which field value it is on
		int field = 0;
		for(int i = 0; i < roles.length; i++) {
			// Current string
			String current = roles[i];
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
