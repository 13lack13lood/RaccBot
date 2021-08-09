package music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
	private AudioPlayer audioPlayer;
	private ByteBuffer buffer;
	private MutableAudioFrame frame;
	
	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		buffer = ByteBuffer.allocate(1024);
		frame = new MutableAudioFrame();
		frame.setBuffer(buffer);
	}
	
	@Override
	public boolean canProvide() {
		// Write to audio frame
		return audioPlayer.provide(frame);
	}
	
	// Give the bytebuffer to discord
	@Override
	public ByteBuffer provide20MsAudio() {
		return buffer.flip();
	}
	
	@Override
	// Audio is already encoded in opus
	public boolean isOpus() {
		return true;
	}
	
}
