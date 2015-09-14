package es.uji.control.commons.diskcache;

import java.util.Date;

public interface IDiskCache {
	public ICacheEntry getEntry(long id) throws DiskCacheException;
	public void removeEntry(long id) throws DiskCacheException;
	public ICacheEntry addEntry(long id, Date timestamp, byte[] bytes) throws DiskCacheException;
}
