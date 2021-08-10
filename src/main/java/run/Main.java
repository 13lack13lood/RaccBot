package run;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commands.ChannelInfo;
import commands.Clear;
import commands.Coin;
import commands.Dice;
import commands.Echo;
import commands.Help;
import commands.Invite;
import commands.RoleInfo;
import commands.ServerChannels;
import commands.ServerInfo;
import commands.ServerRoles;
import commands.UserInfo;
import music.MusicJoin;
import music.MusicLeave;
import music.MusicPlay;
import music.MusicVolume;
import music.SetMusicChannel;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import tools.BotConfig;

public class Main {
	private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws LoginException {
		// Init bot configurations
		BotConfig.init();
		LOGGER.info("Bot Configurations loaded.");
		// Start the bot
		JDABuilder builder = JDABuilder.createDefault(BotConfig.getData("token"));
		LOGGER.info("JDABuilder initialized.");
		// Enable intent
		builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
		builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
		builder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
		LOGGER.info("Intents enabled.");
		// Enable cache
		builder.enableCache(CacheFlag.ONLINE_STATUS);
		builder.enableCache(CacheFlag.ACTIVITY);
		builder.enableCache(CacheFlag.MEMBER_OVERRIDES);
		builder.enableCache(CacheFlag.ROLE_TAGS);
		builder.enableCache(CacheFlag.CLIENT_STATUS);
		builder.enableCache(CacheFlag.VOICE_STATE);
		LOGGER.info("Caches enabled.");
		// Member caching
		builder.setChunkingFilter(ChunkingFilter.ALL);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		LOGGER.info("Member caching enabled.");
		// Status / Activity
		builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
		builder.setActivity(Activity.playing(BotConfig.getData("activity")));
		LOGGER.info("Status and activity set.");
		// Ready listener
		builder.addEventListeners(new Listener());
		// Add commands
		builder.addEventListeners(new Help()); // Help command
		builder.addEventListeners(new Clear()); // Clear command
		builder.addEventListeners(new Echo()); // Echo command
		builder.addEventListeners(new Dice()); // Dice command
		builder.addEventListeners(new Coin()); // Coin command
		builder.addEventListeners(new UserInfo()); // UserInfo command
		builder.addEventListeners(new ServerInfo()); // ServerInfo command
		builder.addEventListeners(new ServerChannels()); // ServerChannels command
		builder.addEventListeners(new ServerRoles()); // ServerRoles command
		builder.addEventListeners(new ChannelInfo()); // ChannelInfo command
		builder.addEventListeners(new RoleInfo()); // RoleInfo command
		builder.addEventListeners(new Invite()); // Invite command
		// Music commands
		builder.addEventListeners(new MusicJoin()); // Join voice channel command
		builder.addEventListeners(new MusicLeave()); // Leave voice channel command
		builder.addEventListeners(new MusicPlay()); // Play music command
		builder.addEventListeners(new MusicVolume()); // Change music volume
		builder.addEventListeners(new SetMusicChannel()); // Set the music commands chanenl
		LOGGER.info("All commands loaded.");
		// Create the new instance and login
		builder.build();
		LOGGER.info("Bot instance created.");
	}
}
