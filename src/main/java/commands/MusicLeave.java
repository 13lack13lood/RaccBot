package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import tools.BotConfig;
import tools.Tool;

public class MusicLeave extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicLeave.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("leave") && Tool.checkMusicCommandChannel(event)) {
				Member self = event.getGuild().getSelfMember();
				GuildVoiceState voiceState = self.getVoiceState();
				// Check if bot is already in voice channel
				if(!voiceState.inVoiceChannel()) {
					event.getChannel().sendMessage("I am not currently in a voice channel").queue();
				} else {
					AudioManager audioManager = event.getGuild().getAudioManager();
					PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.stopTrack();
					PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.clear();
					// Leave the channel
					audioManager.closeAudioConnection();
					event.getChannel().sendMessage("Disconnecting from " + voiceState.getChannel().getName()).queue();
					LOGGER.info("{} requested bot to disconnect from channel: {}", event.getMember().getUser().getAsTag(), voiceState.getChannel().getName());
				}
			}
		}
    }
}
