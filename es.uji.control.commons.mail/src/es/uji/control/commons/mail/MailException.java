package es.uji.control.commons.mail;

public class MailException extends Exception {

	private static final long serialVersionUID = 1L;

	public MailException(String msg, Throwable tr) {
		super(msg, tr);
	}
	
	public MailException(String msg) {
		super(msg);
	}

}
