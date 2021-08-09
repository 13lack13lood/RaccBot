package run;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {
	// Check if bot is online
	public void onReady(ReadyEvent event) {
		System.out.println(event.getJDA().getSelfUser().getAsTag() + " is ready.");
	}
}
