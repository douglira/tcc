package mail;

import java.util.Properties;

public interface IMailerService {

	public void sendHTML(String from, String to, String subject, String templateName, Properties context);
}
