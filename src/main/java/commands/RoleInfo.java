package commands;

import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class RoleInfo extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(RoleInfo.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("roleinfo")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Role Info Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " roleinfo + [role] + [number (optional)]`");	
					usage.addField("Tips:", "Input doesn't have to be exact. As long as the role name contains [role], it will search the first role which has the [role].\n\n"
							+ "You can put a number after [role] in case the search returns multiple results. It will return the role based on the position (use serverroles to get all roles) of them."
							+ "If the number > number of search results, it will return the last role. The number should be > 1.", false);
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					try {
						// Get role from message
						Role role;
						// Check if the user requests which role to return
						if(args.length > 3 && Tool.isNumber(args[args.length - 1])) {
							role = Tool.searchRole(event.getGuild().getRoles(), Tool.combine(args, 2, args.length - 1), Integer.parseInt(args[args.length - 1]));
						} else {
							role = Tool.searchRole(event.getGuild().getRoles(), Tool.combine(args, 2));
						}
						// Check if the query returned null
						if(role != null) {
							// Message Embed
							EmbedBuilder info = new EmbedBuilder();
							info.setColor(Tool.randomColor());
							info.setThumbnail(event.getMessage().getGuild().getIconUrl());
							info.setTitle("Information on the Role " + role.getName());
							info.addField("ID:", role.getId(), false);
							info.addField("Color:", getColor(role), false);
							info.addField("Mentionable:", Boolean.toString(role.isMentionable()), false);
							info.addField("Permissions:", permissions(role.getPermissions()), false);
							info.addField("Member Count:", Integer.toString(event.getGuild().getMembersWithRoles(role).size()), false);
							info.addField("Members:", members(event.getGuild().getMembersWithRoles(role)), false);
							// Send message
							event.getChannel().sendMessageEmbeds(info.build()).queue();
							// Save resources by clearing builder
							info.clear();
							
							LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
						} else {
							event.getChannel().sendMessage("bruh that role doesn't exist.").queue();
							LOGGER.warn("{} [{}] - attempted to search a non-existent role.", event.getAuthor().getAsTag(), args[1]);
						}
					} catch (NumberFormatException e) {
						event.getChannel().sendMessage("bruh give me an integer").queue();
						LOGGER.warn("{} [{}] - attempted to enter a number > Integer.MAX_VALUE.", event.getAuthor().getAsTag(), args[1]);
					}

				}
			}
		}
	}
	// Get the colour of the role
	public String getColor(Role r) {
		if(r.getColor() != null)
			return "RGBA: (" + r.getColor().getRed() + ", " + r.getColor().getBlue() + ", " + r.getColor().getGreen() + ", " + r.getColor().getAlpha() + ")";
		return "Default color.";
	}
	// Get the permissions of a role
	public String permissions(EnumSet<Permission> perms) {
		String output = "";
		// Loop through all the permissions
		for(Permission p : perms) {
			output += "`" + p.getName() + "` ";
		}
		// Check if there are no perms
		if(output.equalsIgnoreCase("")) {
			output += "No permissions.";
		}
		
		return output;
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
}
