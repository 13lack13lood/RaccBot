package music;

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class SetMusicChannel extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(SetMusicChannel.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("setmusicchannel")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Set Music Commands Channel Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " setmusicchannel [#channel]`");					
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					GuildChannel musicchannel = event.getMessage().getMentionedChannels().get(0);
					if(musicchannel != null) {
						Member member = event.getMember();
						if(hasPerms(member.getPermissions()) || member.getUser().getIdLong() == Long.parseLong(BotConfig.getData("owner"))) {
							if(musicchannel.getType() == ChannelType.TEXT) {
								long id = musicchannel.getIdLong();
								long guildID = event.getGuild().getIdLong();
								BotConfig.setMusicChannels(guildID, id, false);
								event.getChannel().sendMessage("Successfully set music commands channel.").queue();
								LOGGER.info("{} - [{}] successfully changed channel to: {}", event.getAuthor().getAsTag(), args[1], musicchannel.getName());
							} else {
								event.getChannel().sendMessage("bruh give a text channel").queue();
							}
						} else {
							event.getChannel().sendMessage("bruh u need " + Permission.MANAGE_CHANNEL.getName() + " in order to do this").queue();
							LOGGER.warn("{} [{}] - user does not have manage channels permissions.", event.getAuthor().getAsTag(), args[1]);
						}
					} else {
						event.getChannel().sendMessage("bruh u didnt mention a channel").queue();
					}		
				}
			} else if(args[1].equalsIgnoreCase("removemusicchannel")) {
				Member member = event.getMember();
				if(hasPerms(member.getPermissions()) || member.getUser().getIdLong() == Long.parseLong(BotConfig.getData("owner"))) {
					BotConfig.setMusicChannels(event.getGuild().getIdLong(), 0, true);
					event.getChannel().sendMessage("Successfully removed music commands channel.").queue();
					LOGGER.info("{} - [{}] successfully removed music commands channel", event.getAuthor().getAsTag(), args[1]);
				} else {
					event.getChannel().sendMessage("bruh u need " + Permission.MANAGE_CHANNEL.getName() + " in order to do this").queue();
					LOGGER.warn("{} [{}] - user does not have manage channels permissions.", event.getAuthor().getAsTag(), args[1]);
				}		
			} else if(args[1].equalsIgnoreCase("musicchannel")) {
				if(BotConfig.getMusicChannels(event.getGuild().getIdLong()) != null) {
					event.getChannel().sendMessage("The channel to use music commands on " + event.getGuild().getName() + " is `" + event.getGuild().getGuildChannelById(BotConfig.getMusicChannels(event.getGuild().getIdLong())).getName() + "`").queue();
				} else {
					event.getChannel().sendMessage("This server has no music commands channel set. Use `setmusicchannel` to set one.").queue();
				}
				LOGGER.info("{} - [{}] requested music channel on [{}]", event.getAuthor().getAsTag(), args[1], event.getGuild().getName());
			}
		}
    }
    
    private boolean hasPerms(EnumSet<Permission> perms) {
    	return perms.contains(Permission.MANAGE_CHANNEL);
    }
}
