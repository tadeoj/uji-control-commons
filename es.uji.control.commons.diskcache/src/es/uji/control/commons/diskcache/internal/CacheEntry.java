/*******************************************************************************
 * Copyright © Universitat Jaume I de Castelló 2015.
 * Aquest programari es distribueix sota les condicions de llicència EUPL 
 * o de qualsevol altra que la substituisca en el futur.
 * La llicència completa es pot descarregar de 
 * https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11
 *******************************************************************************/
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
