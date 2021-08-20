package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicNowPlaying extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicNowPlaying.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("nowplaying") && Tool.checkMusicCommandChannel(event)) {
				Member self = event.getGuild().getSelfMember();
				GuildVoiceState voiceState = self.getVoiceState();
				// Check if bot is already in voice channel
				if(!voiceState.inVoiceChannel()) {
					event.getChannel().sendMessage("I am not currently in a voice channel").queue();
				} else if(PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.getPlayingTrack() == null) { 
					event.getChannel().sendMessage("No music is currently playing.").queue();
				} else {
					AudioPlayer audioPlayer = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer;
					if(audioPlayer.getPlayingTrack() == null) {
						event.getChannel().sendMessage("No track is playing currently").queue();
					} else {
						AudioTrackInfo track = audioPlayer.getPlayingTrack().getInfo();
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("Now Playing: " + track.title);
						embed.addField("By:", track.author, false);
						embed.addField("Duration:", Tool.convertTime(audioPlayer.getPlayingTrack().getPosition()), false);
						embed.addField("Length:", Tool.convertTime(track.length), false);
						embed.addField("URL:", track.uri, false);
						embed.setColor(Tool.randomColor());
						event.getChannel().sendMessageEmbeds(embed.build()).queue();
						embed.clear();
						LOGGER.info("{} - [{}]", event.getAuthor().getAsTag(), args[1]);
					}
				}
			}
		}
    }
}
