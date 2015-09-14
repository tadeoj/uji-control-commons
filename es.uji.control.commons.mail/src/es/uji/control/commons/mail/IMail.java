package es.uji.control.commons.mail;

public interface IMail {
	public void sendMessage(String from, String to, String subject, String text) throws MailException;
}
