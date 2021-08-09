package commands;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class Dice extends ListenerAdapter {
	private static Random random = new Random(); 
	private static Logger LOGGER = LoggerFactory.getLogger(Dice.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("dice")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Roll a Dice Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " dice [# of sides]`");					
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					int sides = 0;
					// Check if number is bigger than an integer
					try {
						sides = Integer.parseInt(args[2]);
						// Generate random number based on how many sides there are
						int number = random.nextInt(sides) + 1;
						// Output message
						event.getChannel().sendMessage("You rolled a " + number + "!").queue();
						
						LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
					} catch(NumberFormatException e) {
						if(args[2].contains(".")) { // Check if it is fraction
							event.getChannel().sendMessage("bruh how can you have a fraction of a dice").queue();
							LOGGER.warn("{} [{}] - attempted to enter a decimal.", event.getAuthor().getAsTag(), args[1]);
						} else if(Tool.isNumber(args[2])) { // Greater than integer limit
							event.getChannel().sendMessage("bruh what kind of dice has that many sides.").queue();
							LOGGER.warn("{} [{}] - attempted to enter a number > Integer.MAX_VALUE.", event.getAuthor().getAsTag(), args[1]);
						} else {
							event.getChannel().sendMessage("bruh that isn't even a number").queue();
							LOGGER.warn("{} [{}] - attempted to enter NaN.", event.getAuthor().getAsTag(), args[1]);
						}
					} catch(IllegalArgumentException e) {
						if(sides == 0) { // User inputed 0
							event.getChannel().sendMessage("bruh theres no dice").queue();
							LOGGER.warn("{} [{}] - attempted to enter 0.", event.getAuthor().getAsTag(), args[1]);
						} else { // User inputed a negative number
							event.getChannel().sendMessage("bruh negative sided dice btw").queue();
							LOGGER.warn("{} [{}] - attempted to enter a negative number.", event.getAuthor().getAsTag(), args[1]);
						}
					}
				}
			}
		}
	}
}
