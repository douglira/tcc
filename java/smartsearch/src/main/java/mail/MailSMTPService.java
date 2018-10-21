package mail;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class MailSMTPService extends Mailer {
	private static MailSMTPService mailServiceInstance = null;

	private static final String HOST = "smtp.mailtrap.io";
	private static final int PORT = 2525;
	private static final String USER = "c0ad778e5d5be7";
	private static final String PASS = "aa88e846a55d0e";
	
	private Properties propsConfig;
	private VelocityEngine ve;

	private MailSMTPService() {
		this.propsConfig = new Properties();
		this.propsConfig.put("mail.smtp.auth", "true");
		this.propsConfig.put("mail.smtp.starttls.enable", "true");
		this.propsConfig.put("mail.smtp.host", HOST);
		this.propsConfig.put("mail.smtp.port", PORT);
		
		this.ve = new VelocityEngine();
		this.ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		this.ve.setProperty("classpath.resource.loader.class",ClasspathResourceLoader.class.getName());
		this.ve.init();
	}

	public static MailSMTPService getInstance() {
		if (mailServiceInstance == null) {
			mailServiceInstance = new MailSMTPService();
		}

		return mailServiceInstance;
	}

	@Override
	protected void sendHTML(MailerService mailer) {
		Session session = Session.getInstance(this.propsConfig, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER, PASS);
			}
		});

		try {
			Message message = new MimeMessage(session);
			
			String dateFormat = "EEE, dd MMM yyyy HH:mm:ss Z";
			String formattedDate = new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(new Date());
			message.addHeader("Date", formattedDate);
			
			try {
				message.setFrom(new InternetAddress(mailer.getFrom(), mailer.getContext().getProperty("shortName")));
			} catch (Exception e) {
				message.setFrom(new InternetAddress(mailer.getFrom()));
			}
			
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailer.getTo()));
			message.setSubject(mailer.getSubject());
			
			VelocityContext ctx = new VelocityContext();
			
			mailer.getContext().forEach((key, value) -> ctx.put((String) key, value));
						
			StringWriter writerHtml = new StringWriter();
			StringWriter writerText = new StringWriter();
			
			Template templateHtml = this.ve.getTemplate("/templates/mail/" + mailer.getTemplate() + ".vm");
			Template templateText = this.ve.getTemplate("/templates/mail/" + mailer.getTemplate() + "-text.vm");

			templateHtml.merge(ctx, writerHtml);
			templateText.merge(ctx, writerText);
			
			
			
			final MimeBodyPart htmlPart = new MimeBodyPart();
			final MimeBodyPart textPart = new MimeBodyPart();
			htmlPart.setContent(writerHtml.toString(), "text/html");
	        textPart.setContent(writerText.toString(), "text/plain"); 
	        
	        final Multipart mp = new MimeMultipart("alternative");
	        mp.addBodyPart(textPart);
	        mp.addBodyPart(htmlPart);
			
			message.setContent(mp);

			Transport.send(message);

//			System.out.println("Email successfully sent...");
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
