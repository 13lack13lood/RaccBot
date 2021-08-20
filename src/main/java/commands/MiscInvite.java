package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;

public class MiscInvite extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MiscInvite.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("invite")) {
				event.getChannel().sendMessage("Link to invite the bot:\n" + BotConfig.getData("invite")).queue();
				LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
			}
		}
	}
}