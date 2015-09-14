package es.uji.control.commons.diskcache.internal;

import java.io.File;

import es.uji.control.commons.diskcache.DiskCacheException;

public class DirectoryManagerTest {

	static private final int LEVELS = 3;
	static private final int DIGITS = 2;
	
	public static void main(String[] args) throws DiskCacheException {
		File rootDir = DirectoryManager.getValidDir(new File("\\temp"), "debugdir");
		DirectoryManager directoryBuilder = new DirectoryManager(DIGITS, LEVELS, rootDir);
		
		for (int i = 0; i < 20000; i++) {
			directoryBuilder.getDirById(i);
		}
	}

}
