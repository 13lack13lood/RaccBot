package music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicPlay extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicPlay.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("play") && Tool.checkMusicCommandChannel(event)) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Play Music Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " play [youtube link]`");					
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					TextChannel channel = event.getChannel();
					Member self = event.getGuild().getSelfMember();
					GuildVoiceState selfVoiceState = self.getVoiceState();
					
					if(!selfVoiceState.inVoiceChannel()) {
						event.getChannel().sendMessage("I need to be in a voice channel.").queue();
					} else {
						Member member = event.getMember();
						GuildVoiceState memberVoiceState = member.getVoiceState();
						
						if(!memberVoiceState.inVoiceChannel()) {
							event.getChannel().sendMessage("You need to be in a voice channel.").queue();
						} else if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
							event.getChannel().sendMessage("You need to be in the same voice channel as me.").queue();
						} else {
							try {
								PlayerManager.getInstance().loadAndPlay(channel, args[2]);
								LOGGER.info("{} [{}] song:", event.getAuthor().getAsTag(), args[1], Tool.combine(args, 2));
							} catch(Exception e) {
								event.getChannel().sendMessage("bruh that aint a link").queue();
							}
						}
					}

				}
			}
		}
    }
}
