package commands;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class Clear extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(Clear.class);

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("clear")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Clear Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " clear [# of messages]`");
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					try {
						// Get the past N messages
						List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[2])).complete();
						event.getChannel().deleteMessages(messages).queue();
						// Success message
						event.getChannel().sendMessage("Messages successfully deleted.").queue();
						event.getMessage().delete().queue();
						
						messages = event.getChannel().getHistory().retrievePast(1).complete();
						event.getChannel().deleteMessages(messages).queueAfter(1000, TimeUnit.MILLISECONDS);
						
						LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
					} catch(IllegalArgumentException e) {
						// Too many messages
						if(e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval limit is between 1 and 100 messages.")) {
							event.getChannel().sendMessage("Too many messages! Between 2 - 100 can be deleted.").queue();
							LOGGER.warn("{} [{}] - attempted to delete an ammount not between 2 - 100.", event.getAuthor().getAsTag(), args[1]);
						} else if(Integer.parseInt(args[2]) == 1) { // Delete the command message
							event.getChannel().sendMessage("why send a message if you're gonna delete it...").queue();
							LOGGER.warn("{} [{}] - attempted to delete the command message.", event.getAuthor().getAsTag(), args[1]);
						} else { // Messages too old
							event.getChannel().sendMessage("Messages too old! Messages older than 2 weeks can not be deleted.").queue();
							LOGGER.warn("{} [{}] - attempted to delete the command message.", event.getAuthor().getAsTag(), args[1]);
						}
					} catch(InsufficientPermissionException e) {
						// Not enough permissions
						if(e.toString().startsWith("net.dv8tion.jda.api.exceptions.InsufficientPermissionException: Must have MESSAGE_MANAGE in order to bulk delete messages in this channel regardless of author.")) {
							event.getChannel().sendMessage("no permissions sadge. Must have Manage Messages in order to delete messages.").queue();
							LOGGER.warn("{} [{}] - bot does not have manage messages permissions.", event.getAuthor().getAsTag(), args[1]);
						}
					} catch(Exception e) {
						LOGGER.warn("{} [{}] - unknown exception occured", event.getAuthor().getAsTag(), args[1]);
						e.printStackTrace();
					}
				}
			}
		}
	}
}
