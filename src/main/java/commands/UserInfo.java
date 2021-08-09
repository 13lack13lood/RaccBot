package commands;

import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class UserInfo extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(UserInfo.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("userinfo")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("UserInfo Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " userinfo [@mention]`");					
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					try {
						// Get mentioned user
						Member name = event.getMessage().getMentionedMembers().get(0);
						// Message Embed
						EmbedBuilder info = new EmbedBuilder();
						info.setColor(Tool.randomColor());
						info.setThumbnail( name.getUser().getAvatarUrl());
						info.setTitle("Information on " + name.getUser().getName());
						info.setDescription(name.getUser().getAsTag());
						info.addField("Account Creation Date:", name.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("d MMM uuuu")), false);
						info.addField("Server Join Date:", name.getTimeJoined().format(DateTimeFormatter.ofPattern("d MMM uuuu")), true);
						info.addField("ID:", name.getUser().getId(), false);
						info.addField("Nickname:", name.getNickname() == null ? "No nickname." : name.getNickname(), true);
						info.addField("Status:", name.getOnlineStatus().toString().replaceAll("_", " "), false); 
						info.addField("Game/Activity: ", displayGameInfo(name), false);
						info.addField("Roles:", getRoles(name.getRoles()), false);
						info.addField("Time Boosted on Server:", name.getTimeBoosted() == null ? "Not boosting currently." : name.getTimeBoosted().format(DateTimeFormatter.ofPattern("d MMM uuuu")), false);
						info.addField("Permissions on " + name.getGuild().getName(), getPerms(name.getPermissions()), false);
						// Send the embed
						event.getChannel().sendMessageEmbeds(info.build()).queue();
						// Clear builder to save resources
						info.clear();
						
						LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
					} catch(IndexOutOfBoundsException e) {
						event.getChannel().sendMessage("who?").queue();
						LOGGER.warn("{} [{}] - attempted to search without mentioning anyone.", event.getAuthor().getAsTag(), args[1]);
					}
				}
			}
		}
	}
	// Get game info about the user
	private String displayGameInfo(Member name) {
		// Check if the user is playing a game
		try {
			// Get the activities
			String game = "";
			for(Activity a : name.getActivities()) {
				game += a.getName() + "\n";
			}
			// Check if no game is played
			if(game.equalsIgnoreCase("")) {
				return "No game is being played";
			}
			
			return game;
		} catch(IndexOutOfBoundsException e) {
			return "No game is being played.";
		}
	}
	// Get the roles of the user
	private String getRoles(List<Role> list) {
		String roles = "";
		// Check if the user has no roles
		if(list.isEmpty()) {
			return "No roles";
		}
		// Add all the roles to the string
		for(int i = 0; i < list.size(); i++) {
			roles += "`" + list.get(i).getName() + "` ";
		}
		
		return roles;
	}
	// Get the permissions of the user
	private String getPerms(EnumSet<Permission> perms) {
		String permissions = "";
		// Check if the user has no permissions
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