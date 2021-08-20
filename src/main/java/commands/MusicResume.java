package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicResume extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicResume.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("resume") && Tool.checkMusicCommandChannel(event)) {
				Member self = event.getGuild().getSelfMember();
				GuildVoiceState voiceState = self.getVoiceState();
				// Check if bot is already in voice channel
				if(!voiceState.inVoiceChannel()) {
					event.getChannel().sendMessage("I am not currently in a voice channel").queue();
				} else if(PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.getPlayingTrack() == null) { 
					event.getChannel().sendMessage("No music is currently playing.").queue();
				} else if(!PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.isPaused()){
					event.getChannel().sendMessage("Music is still playing.\n Use `-r pause` to resume playing.").queue();
				} else {
					PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.setPaused(false);;
					event.getChannel().sendMessage("Music resumed.").queue();
					LOGGER.info("{} - [{}]", event.getMember().getUser().getAsTag(), args[1]);
				}
			}
		}
    }
}
