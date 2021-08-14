package commands;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class Echo extends ListenerAdapter {
	private Scanner scanner;
	private static Logger LOGGER = LoggerFactory.getLogger(Echo.class);
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("echo")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Echo Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " echo [message] [# of times]`");
					usage.setFooter("~~there are many ways to use this command~~");
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else if(args[2].contentEquals("console") && event.getMessage().getAuthor().getIdLong() == Long.parseLong(BotConfig.getData("owner"))) {
					LOGGER.info("ECHO command from console now controlling bot, bot will no longer listen for commands.");
					// Only owner can type from console
					scanner = new Scanner(System.in);
					String in = scanner.nextLine();
					while(!in.startsWith("end")) {
						event.getChannel().sendMessage(in).queue();
						in = scanner.nextLine();
					}
					LOGGER.info("ECHO command from console no longer controlling bot, bot will resume listening for commands.");
					scanner.close();
				} else {
					// Check if the user gives a number
					try {
						// Make sure they are not spamming
						if(Integer.parseInt(args[args.length - 1]) > 50) {
							event.getChannel().sendMessage("y u spam").queue();
							LOGGER.warn("{} [{}] - attempted to spam with ECHO command", event.getAuthor().getAsTag(), args[1]);
						} else {
							String message = "";
							String output = "";
							// Loop through the message to get what to echo
							for(int i = 2; i < args.length - 1; i++) {
								message += args[i] + " ";
							}
							// Loop through how many times requested
							for(int i = 0; i < Integer.parseInt(args[args.length - 1]); i++) {
								if((output + message + "\n").length() > 2000) break;
								output += message + "\n";
							}
							if(output.length() == 0)
								event.getChannel().sendMessage("bruh too many characters").queue();
							else// Output the final message
								event.getChannel().sendMessage(output).queue();
							// Delete the original message
							try { // Catch no permission exception
								event.getMessage().delete().queue();
							} catch(InsufficientPermissionException e) {
								LOGGER.warn("{} [{}] - bot does not have MANAGE_MESSAGES.", event.getAuthor().getAsTag(), args[1]);
							}
							LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
						}
					} catch(Exception e) {
						event.getChannel().sendMessage("how many times?").queue();
						LOGGER.warn("{} [{}] - attempted to use the command without entering a number.", event.getAuthor().getAsTag(), args[1]);
					}
				}
			}
		}
	}
}
