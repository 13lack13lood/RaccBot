package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicLoop extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicLoop.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("loop") && Tool.checkMusicCommandChannel(event)) {
				Member self = event.getGuild().getSelfMember();
				GuildVoiceState voiceState = self.getVoiceState();
				// Check if bot is already in voice channel
				if(!voiceState.inVoiceChannel()) {
					event.getChannel().sendMessage("I am not currently in a voice channel").queue();
				} else if(PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.getPlayingTrack() == null) {
					event.getChannel().sendMessage("I am not currently playing anything").queue();
				} else if(PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.isRepeat()) {
					event.getChannel().sendMessage("A track is already looping").queue();
				} else {
					GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
					boolean newRepeating = !manager.scheduler.isRepeat();
					manager.scheduler.setRepeat(newRepeating);
					event.getChannel().sendMessage(newRepeating ? "Looped." : "Loop stopped.").queue();
					LOGGER.info("{} - [{}]", event.getAuthor().getAsTag(), args[1]);
				}
			}
		}
    }
}
