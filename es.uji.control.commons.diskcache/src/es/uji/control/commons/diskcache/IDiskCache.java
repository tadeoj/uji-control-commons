/*******************************************************************************
 * Copyright © Universitat Jaume I de Castelló 2015.
 * Aquest programari es distribueix sota les condicions de llicència EUPL 
 * o de qualsevol altra que la substituisca en el futur.
 * La llicència completa es pot descarregar de 
 * https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11
 *******************************************************************************/
package es.uji.control.commons.diskcache;

import java.util.Date;

public interface IDiskCache {
	public ICacheEntry getEntry(long id) throws DiskCacheException;
	public void removeEntry(long id) throws DiskCacheException;
	public ICacheEntry addEntry(long id, Date timestamp, byte[] bytes) throws DiskCacheException;
}
