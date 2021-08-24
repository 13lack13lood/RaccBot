package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tools.BotConfig;

public class HiddenHomework extends ListenerAdapter{
	private static Logger LOGGER = LoggerFactory.getLogger(HiddenHomework.class);
	
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Split the message by space
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		// Check for prefix
		if(args[0].equalsIgnoreCase(BotConfig.getData("prefix"))) {
			if(args[1].equalsIgnoreCase("homework")) {
				event.getChannel().sendMessage("Your request has been sent.").queue();
				LOGGER.info("{} - [{}] requested for homework", event.getAuthor().getAsTag(), args[1]);
				Thread thread = new Thread() {
					public BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					public StringTokenizer st;
					
					public void run() {
						System.out.println("[" + event.getAuthor().getAsTag() + "] from: [" + event.getGuild().getName() + "] - requests for class kick homework (1 = allow, 2 = deny)");
						try {
							int n = nextInt();
							if(n == 1) {
								event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage("**_For Olympiads Homework:_**\nGo to https://classkick.com/\nLog in as student\nClick log in\n**Username**: Harry Zhu\n**Password**: harryzhu\nAll the homework answers for my Gr11 and up classes are there.")).queue();
								event.getChannel().sendMessage("Your request has been accepted.\nCheck DMs.").queue();
								LOGGER.info("{} - homework request has been accepted", event.getAuthor().getAsTag());
							} else {
								event.getChannel().sendMessage("Your request has been declined. kekw").queue();
								LOGGER.info("{} - homework request has been declined", event.getAuthor().getAsTag());
							}
						} catch(Exception e) {
							event.getChannel().sendMessage("Something broke, try again.").queue();
						}	
					}
					
					public String next() throws IOException {
						while (st == null || !st.hasMoreTokens())
							st = new StringTokenizer(br.readLine().trim());
						return st.nextToken();
					}

					public int nextInt() throws IOException {
						return Integer.parseInt(next());
					}
				};
				thread.run();
			}
		}
    }
}
