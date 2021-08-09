package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class Help extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(Help.class);
	
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
				help.addField("Prefix", BotConfig.getData("prefix"), false);
				help.addField("Main Commands:", "help, userinfo, serverinfo, serverchannels, serverroles, clear, channelinfo, roleinfo", false);
				help.addField("Random Commands:", "echo, dice, coin", false);
				help.addField("Admin Commands:", "test", false);
				help.addField("More info about a command: ", "usually `" + BotConfig.getData("prefix") + " [command]` or __**sometimes**__ `" + BotConfig.getData("prefix") + " [command] help`", false);
				help.setFooter("Creator: 13lack13lood");
				help.setColor(Tool.randomColor());
				// Send embed
				event.getChannel().sendMessageEmbeds(help.build()).queue();
				// Save resources by clearing builder
				help.clear();
				
				LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
			}
		}
	}
}
