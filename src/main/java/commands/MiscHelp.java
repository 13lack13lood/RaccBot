package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MiscHelp extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MiscHelp.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("help")) {
				// Typing indicator
				event.getChannel().sendTyping().queue();
				// Help embed
				EmbedBuilder help = new EmbedBuilder();
				help.setTitle("RaccBot Info");
				help.setDescription("Help menu for a dog shit bot");
				help.addField("Prefix:", BotConfig.getData("prefix"), false);
				
				String[] commands = BotConfig.BOTCOMMANDS;
				help.addField("Main Commands:", commands[0], false);
				help.addField("Music Commands:", commands[1], false);
				help.addField("Misc commands:", commands[2], false);
				help.addField("More info about a command: ", "usually `" + BotConfig.getData("prefix") + " [command]` or __**sometimes**__ `" + BotConfig.getData("prefix") + " [command] help`", false);
				help.setFooter("Creator: 13lack13lood");
				help.setColor(Tool.randomColor());
				// Send embed
				event.getChannel().sendMessageEmbeds(help.build()).submit();
				// Save resources by clearing builder
				help.clear();
				
				LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
			}
		}
	}
}
