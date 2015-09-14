package es.uji.control.commons.diskcache;

import java.util.Date;

public interface ICacheEntry {
	public byte[] getBytes() throws DiskCacheException;
	public Date getTimestamp() throws DiskCacheException;
}
