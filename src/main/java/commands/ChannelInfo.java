package commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class ChannelInfo extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(ChannelInfo.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("channelinfo")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Channel Info Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " channelinfo + [channel]`");	
					usage.addField("Tips:", "Input doesn't have to be exact. As long as the channel name contains [input], it will find the first channel which has the [input]", false);
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					// Get channel from message
					GuildChannel channel = Tool.searchChannel(event.getMessage().getGuild().getChannels(), Tool.combine(args, 2));
					if(channel != null) {
						// Message Embed
						EmbedBuilder info = new EmbedBuilder();
						info.setColor(Tool.randomColor());
						info.setThumbnail(event.getMessage().getGuild().getIconUrl());
						info.setTitle("Information on the Channel " + channel.getName());
						info.addField("Category:", channel.getType() == ChannelType.CATEGORY ? channel.getName() : channel.getParent() == null ? "None" : channel.getParent().getName(), false);
						info.addField("Type:", channel.getType().toString(), false);
						info.addField("Permissions Denied:", permissionsDenied(channel.getPermissionOverrides()), false);
						info.addField("Permissions Allowed:", permissionsAllowed(channel.getPermissionOverrides()), false);
						info.addField("Member Count:", Integer.toString(channel.getMembers().size()), false);
						info.addField("Members:", members(channel.getMembers()), false);
						// Send message
						event.getChannel().sendMessageEmbeds(info.build()).queue();
						// Save resources by clearing builder
						info.clear();
						
						LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
					} else {
						event.getChannel().sendMessage("bruh that channel doesn't exist.").queue();
						// Warning
						LOGGER.warn("{} [{}] - attempted to search a non-existent channel.", event.getAuthor().getAsTag(), args[1]);
					}
				}
			}
		}
	}
	// Get all members from a channel
	public String members(List<Member> list) {
		String members = "";
		if(list.size() > 100) {
			return "Too many members ( > 100)";
		}
		// Add all members to list
		for(Member m : list) {
			members += "`" + m.getUser().getAsTag() + "` ";
		}
		// Check if there are no members
		if(members.equalsIgnoreCase("")) {
			members += "No members have this role.";
		}
		
		return members;
	}

	// Get all permission overrides for denied
	public String permissionsDenied(List<PermissionOverride> list) {
		String permissions = "";
		// Check if the user has no permissions
		if(list.isEmpty()) {
			return "No permissions";
		}
		// Add all the permissions to the string
		for(PermissionOverride p : list) {
			// Check if permissionoverride list is empty
			if(p.getDenied().isEmpty())
				continue;
			// Check if it is role override
			if(p.isRoleOverride()) {
				permissions += "`Role Denied: " + p.getRole().getName() + "` ";
				continue;
			}
			// Check if it is member override
			if(p.isMemberOverride()) {
				permissions += "`Member Denied: " + p.getMember().getUser().getAsTag() + "` ";
				continue;
			}
			// Loop through all the permissions
			for(Permission perm : p.getDenied()) {
				permissions += "`" + perm.getName() + "` ";
			}
		}
		
		return permissions;
	}
	// Get all permissions overrides for allowed
	public String permissionsAllowed(List<PermissionOverride> list) {
		String permissions = "";
		// Check if the user has no permissions
		if(list.isEmpty()) {
			return "No permissions";
		}
		// Add all the permissions to the string
		for(PermissionOverride p : list) {
			// Check if permissionoverride list is empty
			if(p.getAllowed().isEmpty())
				continue;
			// Check if it is role override
			if(p.isRoleOverride()) {
				permissions += "`Role Allowed: " + p.getRole().getName() + "` ";
				continue;
			}
			// Check if it is member override
			boolean memberOverride = p.isMemberOverride();
			if(memberOverride) {
				permissions += "`Member Allowed: " + p.getMember().getUser().getAsTag() + "` ";
				continue;
			}
			// Loop through all the permissions
			for(Permission perm : p.getAllowed()) {
				permissions += "`" + perm.getName() + "` ";
			}
		}
		
		return permissions;
	}
}
