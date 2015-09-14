package es.uji.control.commons.diskcache;

@SuppressWarnings("serial")
public class DiskCacheException extends Exception {

	public DiskCacheException(String msg, Throwable tr) {
		super(msg, tr);
	}

	public DiskCacheException(String msg) {
		super(msg);
	}

}
