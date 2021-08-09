package music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicVolume extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicVolume.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("volume")) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Music Volume Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " volume [number]`");	
					usage.addField("Tip:", "Default volume is 100.\nMax volume is 200.", false);
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					Member self = event.getGuild().getSelfMember();
					GuildVoiceState selfVoiceState = self.getVoiceState();
					
					if(!selfVoiceState.inVoiceChannel()) {
						event.getChannel().sendMessage("I need to be in a voice channel.").queue();
					} else {
						Member member = event.getMember();
						GuildVoiceState memberVoiceState = member.getVoiceState();
						// Check if user is in voice chanel
						if(!memberVoiceState.inVoiceChannel()) {
							event.getChannel().sendMessage("You need to be in a voice channel.").queue();
						} else if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) { // Check if user is in the same voice channel as the bot
							event.getChannel().sendMessage("You need to be in the same voice channel as me.").queue();
						} else {
							try {
								PlayerManager.getInstance().getMusicManager(member.getGuild()).audioPlayer.setVolume(((Integer.parseInt(args[2]) > 200) ? 200 : Integer.parseInt(args[2])));
								event.getChannel().sendMessage("Changing volume to " + ((Integer.parseInt(args[2]) > 200) ? 200 : Integer.parseInt(args[2]))).queue();
								LOGGER.info("{} [{}] changed to [{}]", event.getAuthor().getAsTag(), args[1], args[2]);
							} catch (NumberFormatException e) { // Catch error if user does not give integer
								event.getChannel().sendMessage("bruh give me an integer.").queue();
								LOGGER.warn("{} [{}] - attempted to change the volume with a non-integer", event.getAuthor().getAsTag(), args[1]);
							}
						}
					}
				}
			}
		}
    }
}
