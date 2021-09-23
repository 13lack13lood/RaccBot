package music;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import tools.Tool;

public class PlayerManager {
	private static PlayerManager INSTANCE;
	
	private Map<Long, GuildMusicManager> musicManager;
	private AudioPlayerManager audioPlayerManager;
	
	public PlayerManager() {
		musicManager = new HashMap<>();
		audioPlayerManager = new DefaultAudioPlayerManager();
		
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		AudioSourceManagers.registerLocalSource(audioPlayerManager);
	}
	
	public GuildMusicManager getMusicManager(Guild guild) {
		return musicManager.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayerManager);
			// Tell JDA what to use to send the audio
			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
			
			return guildMusicManager;
		});
	}
	
	public List<Long> guilds() {
		List<Long> list = new ArrayList<Long>();
		list.addAll(musicManager.keySet());
		return list;
	}
	
	public void loadAndPlay(TextChannel channel, String track) {
		GuildMusicManager guildMusicManager = getMusicManager(channel.getGuild());
		audioPlayerManager.loadItemOrdered(musicManager, track, new AudioLoadResultHandler() {
			// Check if there is 1 track loaded
			@Override
			public void trackLoaded(AudioTrack track) {
				guildMusicManager.scheduler.queue(track);
				// Message Embed
				EmbedBuilder message = new EmbedBuilder();
				message.setTitle("Adding to queue:");
				message.setDescription(track.getInfo().title + " by " + track.getInfo().author);
				message.setThumbnail("http://img.youtube.com/vi/" + Tool.getVideoID(track.getInfo().uri) + "/0.jpg");
				
				if(track.getInfo().isStream)
					message.addField("Length:", "Stream", false);
				else
					message.addField("Length:", Tool.convertTime(track.getDuration()), false);
				
				message.addField("URL:", track.getInfo().uri, false);
				message.setColor(Tool.randomColor());
				channel.sendMessageEmbeds(message.build()).queue();
				message.clear();
				
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				List<AudioTrack> tracks = playlist.getTracks();
				
				EmbedBuilder message = new EmbedBuilder();
				message.setTitle("Adding to queue:");
				message.setDescription("Tracks from playlist: " + playlist.getName());
				message.addField("Number of Tracks:", Integer.toString(tracks.size()), false);
				message.addField("URL:", track, false);
				message.setColor(Tool.randomColor());
				channel.sendMessageEmbeds(message.build()).queue();
				message.clear();
				
				for(AudioTrack track : tracks) {
					guildMusicManager.scheduler.queue(track);
				}
			}

			@Override
			public void noMatches() {
				channel.sendMessage("Track could not be found.").queue();
				
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				channel.sendMessage("Track failed to load.").queue();
			}
			
		});
	}
	
	public static PlayerManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new PlayerManager();
		}
		
		return INSTANCE;
	}
}
