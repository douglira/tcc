package mail;

import java.util.Properties;

public abstract class MailerService {

	protected String from;
	protected String to;
	protected String subject;
	protected String template;
	protected Properties context;
	protected Mailer mail;
	
	public MailerService() {
		this.context = new Properties();
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
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Properties getContext() {
		return context;
	}

	public void setContext(Properties context) {
		this.context = context;
	}

	public Mailer getMail() {
		return mail;
	}

	public void setMail(Mailer mail) {
		this.mail = mail;
	}

	public abstract void send();
}
