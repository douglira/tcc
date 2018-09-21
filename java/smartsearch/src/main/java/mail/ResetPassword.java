package mail;

public class ResetPassword extends MailerService {

	public ResetPassword(String displayName, String urlRedirect) {
		super();

		this.context.put("urlRedirect", urlRedirect);
		this.context.put("displayName", displayName);
	}

	@Override
	public void send() {

		this.from = "noreply@smartsearch.com.br";
		this.subject = "SmartSearch | Redefinição de senha";
		
		this.template = "mailResetPass";
		this.context.put("shortName", "SmartSearch");
		
		this.mail.sendHTML(this.from, this.to, this.subject, this.template, this.context);
	}

}
