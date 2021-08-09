package run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
	// Check if bot is online
	public void onReady(ReadyEvent event) {
		LOGGER.info(event.getJDA().getSelfUser().getAsTag() + " is ready.");
	}
}
