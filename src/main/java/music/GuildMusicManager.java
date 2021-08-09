package music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
	public AudioPlayer audioPlayer;
	public TrackScheduler scheduler;
	
	private AudioPlayerSendHandler sendHandler;
	
	public GuildMusicManager(AudioPlayerManager manager) {
		audioPlayer = manager.createPlayer();
		scheduler = new TrackScheduler(audioPlayer);
		audioPlayer.addListener(scheduler);
		sendHandler = new AudioPlayerSendHandler(audioPlayer);
	}
	
	public AudioPlayerSendHandler getSendHandler() {
		return sendHandler;
	}
}
