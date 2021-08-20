package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.PlayerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import tools.BotConfig;
import tools.Tool;

public class MusicJoin extends ListenerAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(MusicJoin.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("join") && Tool.checkMusicCommandChannel(event)) {
				Member self = event.getGuild().getSelfMember();
				GuildVoiceState voiceState = self.getVoiceState();
				// Check if bot is already in voice channel
				if(voiceState.inVoiceChannel()) {
					event.getChannel().sendMessage("I am already in the voice channel: " + self.getVoiceState().getChannel().getName()).queue();
				} else {
					Member user = event.getMember();
					GuildVoiceState userVoiceState = user.getVoiceState();
					// Check if the user is in a voice channel
					if(!userVoiceState.inVoiceChannel()) {
						event.getChannel().sendMessage("You need to be in a voice channel.").queue();
					} else {
						// Permission check
						boolean canJoin = true;
						// Check if bot can join channel
						if(!self.hasPermission(Permission.VOICE_CONNECT)) {
							canJoin = false;
							event.getChannel().sendMessage("I need Connect permissions to join the channel.").queue();
							LOGGER.warn("{} [{}] - bot attempted to connect without VOICE_CONNECT.", event.getAuthor().getAsTag(), args[1]);
						}
						// Check if bot can speak
						if(!self.hasPermission(Permission.VOICE_SPEAK) || !self.hasPermission(Permission.VOICE_USE_VAD)) {
							canJoin = false;
							event.getChannel().sendMessage("I need Speak and Use Voice Activity permissions to play in the channel.").queue();
							LOGGER.warn("{} [{}] - bot attempted to connect without VOICE_SPEAK.", event.getAuthor().getAsTag(), args[1]);
						}
						// Check if bot can see the channel
						if(!self.hasAccess(userVoiceState.getChannel())) {
							canJoin = false;
							event.getChannel().sendMessage("bruh i can't even join your channel.").queue();
							LOGGER.warn("{} [{}] - bot unable to join user's channel.", event.getAuthor().getAsTag(), args[1]);
						}
						// Join the channel
						if(canJoin) {
							AudioManager audioManager = event.getGuild().getAudioManager();
							VoiceChannel userChannel = userVoiceState.getChannel();
							// Join the channel
							audioManager.openAudioConnection(userChannel);
							PlayerManager.getInstance().getMusicManager(event.getGuild());
							PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.setPaused(false);;
							event.getChannel().sendMessage("Connecting to " + userChannel.getName()).queue();
							LOGGER.info("{} requested bot to connect to channel: {}", user.getUser().getAsTag(), userChannel.getName());
						}
					}
				}
			}
		}
    }
}
