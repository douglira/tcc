package mail;

import java.util.Properties;

public abstract class Mailer {
	protected abstract void sendHTML(String from, String to, String subject, String template, Properties context);
}
