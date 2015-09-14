package es.uji.control.commons.diskcache.internal;

import java.io.File;

import es.uji.control.commons.diskcache.DiskCacheException;

public class DirectoryManager {
	
	private final int levels;
	private final int digitsByLevel;
	private final File rootDir;

	public DirectoryManager(int digitsByLevel, int levels, File rootDir) {
		this.digitsByLevel = digitsByLevel;
		this.levels = levels;
		this.rootDir = rootDir;
	}
	
	public File getDirById(long id) throws DiskCacheException {
		return getDirById(rootDir, id, levels);
	}
	
	private File getDirById(File base, long id, int currentLevel) throws DiskCacheException {
		if (currentLevel == 0) {
			// Estamos en el ultimo nivel
			return getValidDir(base, Long.toString(id));
		} else {
			// Estamos en un nivel intermedio.
			long subId = (id / levelToDivisor(currentLevel));
			long rest = (id % levelToDivisor(currentLevel));
			File newBase = getValidDir(base, Long.toString(subId));
			return getDirById(newBase, rest, currentLevel - 1);
		}
	}
	
	static public File getValidDir(File base, String childName) throws DiskCacheException {
		
		// Se comprueba que existe el directorio base
		if (!base.isDirectory()) {
			throw new DiskCacheException(String.format("El directorio %s no existe o existe pero no es un directorio.", base.getName()));
		}
		
		// Ahora se hacen las verificaciones del hijo.
		File child = new File(base, childName);
		if (!child.exists()) {
			child.mkdir();
		} else if (!child.isDirectory()) {
			throw new DiskCacheException(String.format("El directorio %s no se puede crear porque existe un fichero regular con ese nombre.", childName));
		}
		
		// En teoria tenemos un directorio correcto.
		return child;
	}
	
	private int levelToDivisor(int level) {
		int divisor = 1;
		for (int i = 0; i < level; i++) {
			divisor *= digitToDivisor();
		}
		return divisor;
	}
	
	private int digitToDivisor() {
		int divisor = 1;
		for (int i = 0; i < digitsByLevel; i++) {
			divisor *= 10;
		}
		return divisor;
	}
	
}
