package mail;

import java.util.Properties;

public interface MailerService {

	public void sendHTML(String from, String to, String subject, String templateName, Properties context);
}
