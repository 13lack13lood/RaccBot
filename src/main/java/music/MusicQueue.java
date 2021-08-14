package music;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

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
							List<AudioTrack> queue = getQueue(manager.scheduler.getQueue(), page);
							EmbedBuilder output = new EmbedBuilder();
							output.setColor(Tool.randomColor());
							output.setTitle("Music Queue Page " + page + " (Total: " + manager.scheduler.getQueue().size() + ")");
							AudioTrack currentTrack = manager.audioPlayer.getPlayingTrack();
							output.setDescription("**Current Track**:\n" + currentTrack.getInfo().title + "\nBy: *" + currentTrack.getInfo().author + "*\nLength: *" + Tool.convertTime(currentTrack.getInfo().length) + "*\t Duration: *" + Tool.convertTime(currentTrack.getPosition()) + "*");
							for(AudioTrack t : queue) {
								output.addField(t.getInfo().title, "By: *" + t.getInfo().author + "*\nLength: *" + Tool.convertTime(t.getInfo().length) + "*", false);
							}
							event.getChannel().sendMessageEmbeds(output.build()).queue();
							LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1], args[2]);
						} catch (NumberFormatException e) { // Catch error if user does not give integer
							event.getChannel().sendMessage("bruh give me an integer.").queue();
							LOGGER.warn("{} [{}] - attempted to change the volume with a non-integer", event.getAuthor().getAsTag(), args[1]);
						}
					}
				}
			}
		}
    }
    
    private static List<AudioTrack> getQueue(BlockingQueue<AudioTrack> queue, int page) {
    	Iterator<AudioTrack> it = queue.iterator();
    	int target = page * 10;
    	List<AudioTrack> list = new ArrayList<AudioTrack>();
    	// TODO completely redo this, just put all the tracks from the blockingqueue into the list, current method is trash
    	if(target <= queue.size()) {
    		while(it.hasNext() && target > 10) {
    			it.next();
    			target--;
    		}
    		while(it.hasNext() && target > 0) {
    			list.add(it.next());
    			target--;
    		}
    	} else {
    		target = (queue.size() / 10) * 10;
    		while(it.hasNext() && target > queue.size() - (queue.size() / 10) * 10) {
    			it.next();
    			target--;
    		}
    		while(it.hasNext() && target > 0) {
    			list.add(it.next());
    			target--;
    		}
    	}
    	
    	return list;
    }
}
