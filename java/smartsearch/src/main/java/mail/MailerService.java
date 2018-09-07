package mail;

import java.util.Properties;

public class MailerService implements IMailerService {

	private String from;
	private String to;
	private String subject;
	
	private Mailer mail;
	
	public MailerService(Mailer mail) {
		this.mail = mail;
	}
	
	public MailerService(Mailer mail, String from, String to, String subject) {
		this.mail = mail;
		this.from = from;
		this.to = to;
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private void sendMailHTML(String template, Properties context) {
		this.mail.sendHTML(this.from, this.to, this.subject, template, context);
	}

	@Override
	public void sendResetPass(String displayName, String urlRedirect) {		
		this.from = "noreply@smartsearch.com.br";
		this.subject = "SmartSearch | Redefinição de senha";
		
		String template = "mailResetPass";
		
		final Properties context = new Properties();
		context.put("shortName", "SmartSearch");
		context.put("urlRedirect", urlRedirect);
		context.put("displayName", displayName);
		
		this.sendMailHTML(template, context);
	}
}
