package music;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
	private AudioPlayer player;
	private BlockingQueue<AudioTrack> queue;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		queue = new LinkedBlockingQueue<>();
	}
	
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Check if the current 
		if(endReason.mayStartNext) {
			nextTrack();
		}
	}
	
	public void queue(AudioTrack track) {
		// Check if a track is already playing
		if(!player.startTrack(track, true)) {
			queue.offer(track);
		}
	}

	public void nextTrack() {
		// Poll the next track from list
		player.startTrack(queue.poll(), false);
	}
	// Look at the next element
	public AudioTrack peek() {
		return queue.peek();
	}
	// Clear all tracks
	public void clear() {
		queue.clear();
	}
}
