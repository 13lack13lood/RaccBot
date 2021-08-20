package commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import music.GuildMusicManager;
import music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;
import tools.Tool;

public class MusicQueue extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicQueue.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("queue") && Tool.checkMusicCommandChannel(event)) {
				if(args.length < 3) {
					// Usage embed
					EmbedBuilder usage = new EmbedBuilder();
					usage.setColor(Tool.randomColor());
					usage.setTitle("Music Queue Command");
					usage.setDescription("Usage: `" + BotConfig.getData("prefix") + " queue [page number]`");	
					// Send the embed
					event.getChannel().sendMessageEmbeds(usage.build()).queue();
					// Clear builder to save resources
					usage.clear();
					
					LOGGER.info("{} [{}] - help", event.getAuthor().getAsTag(), args[1]);
				} else {
					Member self = event.getGuild().getSelfMember();
					GuildVoiceState selfVoiceState = self.getVoiceState();
					
					if(!selfVoiceState.inVoiceChannel()) {
						event.getChannel().sendMessage("bruh im not even in a voice channel").queue();
					} else {
						try {
							GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
							int page = Integer.parseInt(args[2]);
							// Throw exception if user gives page < 1
							if(page < 1)
								throw new NumberFormatException();
							// Throw exception if queue is empty
							if(manager.scheduler.getQueue().isEmpty())
								throw new NullPointerException();
							
							List<IndexedAudioTrack> queue = getQueue(manager.scheduler.getQueue(), page);
							
							EmbedBuilder output = new EmbedBuilder();
							output.setColor(Tool.randomColor());
							output.setTitle("Music Queue Page " + page + " (Total: " + manager.scheduler.getQueue().size() + ")");
							
							AudioTrack currentTrack = manager.audioPlayer.getPlayingTrack();
							output.setDescription("**Current Track**:\n" + currentTrack.getInfo().title + "\nBy: *" + currentTrack.getInfo().author + "*\nLength: *" + Tool.convertTime(currentTrack.getInfo().length) + "*\t Duration: *" + Tool.convertTime(currentTrack.getPosition()) + "*");
							
							for(IndexedAudioTrack t : queue) {
								output.addField("(" + t.getIndex() + ") " + t.getTrack().getInfo().title, "By: *" + t.getTrack().getInfo().author + "*\nLength: *" + Tool.convertTime(t.getTrack().getInfo().length) + "*", false);
							}
							
							event.getChannel().sendMessageEmbeds(output.build()).queue();
							output.clear();
							LOGGER.info("{} [{}] - page {}", event.getAuthor().getAsTag(), args[1], args[2], page);
						} catch (NumberFormatException e) { // Catch error if user does not give integer
							event.getChannel().sendMessage("bruh give me an integer > 0.").queue();
							LOGGER.warn("{} [{}] - attempted to change the volume with a non-integer", event.getAuthor().getAsTag(), args[1]);
						} catch (NullPointerException e) {
							event.getChannel().sendMessage("Queue is empty.").queue();
							LOGGER.info("{} [{}] - empty queue", event.getAuthor().getAsTag(), args[1], args[2]);
						}
					}
				}
			}
		}
    }
    
    private static List<IndexedAudioTrack> getQueue(BlockingQueue<AudioTrack> queue, int page) {
    	Iterator<AudioTrack> it = queue.iterator();
    	List<IndexedAudioTrack> list = new ArrayList<IndexedAudioTrack>();
    	int index = 1;
    	while(it.hasNext()) {
    		list.add(new IndexedAudioTrack(index++, it.next()));
    	}
    	
    	int start = (page - 1) * 10;
    	int end = start + 10;
    	int size = list.size();
    	
    	if(start > list.size()) {
    		start = list.size() - (list.size() % 10);
    		end = list.size();
    	}
    	
    	for(int i = 0; i < start; i++) {
    		list.remove(0);
    	}
    	
    	for(int i = end; i < size; i++) {
    		list.remove(list.size() - 1);
    	}
	    	
    	return list;
    }
    
    private static class IndexedAudioTrack {
    	private int index;
    	private AudioTrack track;
    	
		public IndexedAudioTrack(int index, AudioTrack track) {
			this.index = index;
			this.track = track;
		}
		
		public int getIndex() {
			return index;
		}
		
		public AudioTrack getTrack() {
			return track;
		}
    }
}
