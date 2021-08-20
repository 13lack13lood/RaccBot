package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicSkip extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicSkip.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("skip") && Tool.checkMusicCommandChannel(event)) {
				Member self = event.getGuild().getSelfMember();
				GuildVoiceState voiceState = self.getVoiceState();
				// Check if bot is already in voice channel
				if(!voiceState.inVoiceChannel()) {
					event.getChannel().sendMessage("I am not currently in a voice channel").queue();
				} else if(PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.getPlayingTrack() == null) {
					event.getChannel().sendMessage("I am not currently playing anything").queue();
				} else {
					GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
					AudioPlayer audioPlayer = manager.audioPlayer;
					if(audioPlayer.getPlayingTrack() == null) {
						event.getChannel().sendMessage("No track is playing currently").queue();
					} else {
						manager.scheduler.nextTrack();
						if(audioPlayer.getPlayingTrack() != null)
							event.getChannel().sendMessage("**Next Track:** " + audioPlayer.getPlayingTrack().getInfo().title).queue();
						else
							event.getChannel().sendMessage("Track Skiped.").queue();
						LOGGER.info("{} - [{}]", event.getAuthor().getAsTag(), args[1]);
					}
				}
			}
		}
    }
}
