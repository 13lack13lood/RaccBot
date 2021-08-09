package commands;

import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class ServerInfo extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(ServerInfo.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("serverinfo")) {
				if(args.length > 2 && args[2].equalsIgnoreCase("help")) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Server Info Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " serverinfo`");					
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
					info.setTitle("Information on " + server.getName());
					info.addField("Server Creation Date: ", server.getTimeCreated().format(DateTimeFormatter.ofPattern("d MMM uuuu")), false);
					info.addField("ID: ", server.getId(), false);
					info.addField("Owner:", server.getOwner().getUser().getAsTag(), false);
					info.addField("Members:", Integer.toString(server.getMemberCount()), false);
					info.addField("Boost Tier:", server.getBoostTier().toString().replaceAll("_", " "), false);
					info.addField("Boosts:", Integer.toString(server.getBoostCount()), false);
					info.addField("Boosters:", Integer.toString(server.getBoosters().size()), false);
					info.addField("Channels:", Integer.toString(server.getChannels().size()) + "\nUse serverchannels to see all channels",false);
					info.addField("Roles:", Integer.toString(server.getRoles().size()) + "\nUse serverroles to see all roles",false);
					info.addField("@everyone Permissions:", getPerms(server.getPublicRole().getPermissions()), false);
					info.addField("Vanity URL:", server.getVanityUrl() == null ? "No vanity URL" : server.getVanityUrl(), false);
					// Send message
					event.getChannel().sendMessageEmbeds(info.build()).queue();
					// Save resources by clearing builder
					info.clear();
					
					LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
				}
			}
		}
	}
	// Get permissions of @everyone
	private String getPerms(EnumSet<Permission> perms) {
		String permissions = "";
		// Check if it has no permissions
		if(perms.isEmpty()) {
			return "No permissions";
		}
		// Add all the permissions to the string
		for(Permission p : perms) {
			permissions += "`" + p.getName() + "` ";
		}
		
		return permissions;
	}
}
