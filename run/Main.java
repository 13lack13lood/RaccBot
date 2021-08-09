package run;

import javax.security.auth.login.LoginException;

import Tools.BotConfig;
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
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Main {
	
	public static void main(String[] args) throws LoginException {
		// Init bot configurations
		BotConfig.init();
		// Start the bot
		JDABuilder builder = JDABuilder.createDefault(BotConfig.getData("token"));
		// Enable intent
		builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
		builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
		// Enable cache
		builder.enableCache(CacheFlag.ONLINE_STATUS);
		builder.enableCache(CacheFlag.ACTIVITY);
		builder.enableCache(CacheFlag.MEMBER_OVERRIDES);
		builder.enableCache(CacheFlag.ROLE_TAGS);
		builder.enableCache(CacheFlag.CLIENT_STATUS);
		// Member caching
		builder.setChunkingFilter(ChunkingFilter.ALL);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		// Status
		builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
		// Activity
		builder.setActivity(Activity.playing(BotConfig.getData("activity")));
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
		// Create the new instance and login
		builder.build();
	}
}
