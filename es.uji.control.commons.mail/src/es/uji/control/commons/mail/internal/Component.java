package es.uji.control.commons.mail.internal;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uji.control.commons.mail.IMail;
import es.uji.control.commons.mail.MailException;

public class Component implements IMail {

	private Session session;
	
	static final Logger logger = LoggerFactory.getLogger(Component.class);
	
	
	public void startup() throws MailException {
		
		try {
			// Propiedades de la conexión
	        Properties props = new Properties();
	        props.setProperty("mail.smtp.host", "smtp.gmail.com");
	        props.setProperty("mail.smtp.starttls.enable", "true");
	        props.setProperty("mail.smtp.port", "587");
	        props.setProperty("mail.smtp.user", "scu@uji.es");
	        props.setProperty("mail.smtp.auth", "true");
	
	        // Preparamos la sesion
	        session = Session.getDefaultInstance(props);
	    } catch (Exception e) {
	    	throw new MailException("No se han podido construir las propiedades de conexión");
	    }
	}
	
	public void shutdown() {
			
	}
	
	public void sendMessage(String from, String to, String subject, String text) throws MailException {
		try
	    {  
	        // Construimos el mensaje
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(
	            Message.RecipientType.TO,
	            new InternetAddress(to));
	        message.setSubject(subject);
	        message.setText(text);

	        // Lo enviamos.
	        Transport t = session.getTransport("smtp");
	        t.connect(from, "**************");
	        t.sendMessage(message, message.getAllRecipients());

	        // Cierre.
	        t.close();
	    }
	    catch (Exception e)
	    {
	    	throw new MailException("Error en la composición y envio del mensaje");	
	    }

	}
	
}
