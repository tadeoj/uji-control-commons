/*******************************************************************************
 * Copyright © Universitat Jaume I de Castelló 2015.
 * Aquest programari es distribueix sota les condicions de llicència EUPL 
 * o de qualsevol altra que la substituisca en el futur.
 * La llicència completa es pot descarregar de 
 * https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11
 *******************************************************************************/
package es.uji.control.commons.diskcache.internal;

import java.io.File;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uji.control.commons.diskcache.DiskCacheException;
import es.uji.control.commons.diskcache.ICacheEntry;
import es.uji.control.commons.diskcache.IDiskCache;

public class Component implements IDiskCache {
	
	static final Logger logger = LoggerFactory.getLogger(Component.class);

	static private final int LEVELS = 3;
	static private final int DIGITS = 2;
	
	private EntryManager entryManager;
	
	public void startup() throws DiskCacheException {
		try {
			this.entryManager = new EntryManager(new DirectoryManager(DIGITS, LEVELS, getRootDir()));
		} catch (DiskCacheException dcEx) {
			logger.error("Imposible utilizar el directorio raiz indicado para la cache de imagenes.", dcEx);
			throw dcEx;
		}
	}
	
	public void shutdown() {
	}
	
	private File getRootDir() throws DiskCacheException {
		File file = new File(System.getProperty("user.home") + "\\Universitat Jaume I\\");
		if (!file.exists()) {
			file.mkdirs();
		}
		return DirectoryManager.getValidDir(file, "COMMONS FOTOS");
	}

	@Override
	public ICacheEntry getEntry(long id) throws DiskCacheException {
		return entryManager.getEntry(id);
	}

	@Override
	public void removeEntry(long id) throws DiskCacheException {
		entryManager.removeEntry(id);
	}

	@Override
	public ICacheEntry addEntry(long id, Date timestamp, byte[] bytes) throws DiskCacheException {
		return entryManager.addEntry(id, timestamp, bytes);
	}
	
}
