package commands;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class Coin extends ListenerAdapter {
	private static Random random = new Random(); 
	private static Logger LOGGER = LoggerFactory.getLogger(Coin.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("coin")) {
				if(args.length > 2 && args[2].equalsIgnoreCase("help")) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Flip a Coin Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " coin`");					
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					// Generate a random boolean for the coin
					boolean coin = random.nextBoolean();
					// True is heads
					if(coin) {
						event.getChannel().sendMessage("Heads.").queue();
					} else { // False is tails
						event.getChannel().sendMessage("Tails.").queue();
					}
					
					LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
				}
			}
		}
	}
}
