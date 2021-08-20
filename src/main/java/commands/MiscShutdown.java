package commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import tools.BotConfig;

public class MiscShutdown extends ListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MiscShutdown.class);
	// Check if bot is online
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(event.getAuthor().getIdLong() == Long.parseLong(BotConfig.getData("owner")) && args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("shutdown")) {
				event.getChannel().sendMessage("Bot Shutting Down").queue();
				LOGGER.info("{} [{}]", event.getAuthor().getAsTag(), args[1]);
				Member self = event.getGuild().getSelfMember();
				List<Long> guilds = PlayerManager.getInstance().guilds();
				for(long guild : guilds) {
					Guild g = self.getJDA().getGuildById(guild);
					AudioManager audioManager = g.getAudioManager();
					PlayerManager.getInstance().getMusicManager(g).audioPlayer.stopTrack();
					PlayerManager.getInstance().getMusicManager(g).audioPlayer.destroy();;
					PlayerManager.getInstance().getMusicManager(g).scheduler.destroy();
					// Leave the channel
					audioManager.closeAudioConnection();
				}
				self.getJDA().shutdown();
				LOGGER.info("Bot Shutting Down");
				System.exit(0);
			}
		}
	}
}