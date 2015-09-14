package es.uji.control.commons.diskcache.internal;

import java.util.Date;

import es.uji.control.commons.diskcache.ICacheEntry;

public class CacheEntry implements ICacheEntry {
	
	private Date timestamp;
	private byte[] bytes;
	
	public CacheEntry(Date timestamp, byte[] bytes) {
		this.timestamp = timestamp;
		this.bytes = bytes;
	}
	
	@Override
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

}
