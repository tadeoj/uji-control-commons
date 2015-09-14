package es.uji.control.commons.diskcache.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import es.uji.control.commons.diskcache.DiskCacheException;
import es.uji.control.commons.diskcache.ICacheEntry;

public class EntryManager {
	
	private static final String FILE_NAME_PREFIX = "";
	
	private DirectoryManager directoryManager;
	
	public EntryManager(DirectoryManager directoryManager) {
		this.directoryManager = directoryManager;
	}
	
	public ICacheEntry getEntry(long id) throws DiskCacheException {
		File dir = directoryManager.getDirById(id);
		File timestampFile = getTimestampFile(dir, id);
		File bytesFile = getBytesFile(dir, id);
		
		if (!timestampFile.exists() || !bytesFile.exists()) {
			return null;
		}

		Date timestamp;
		FastByteArrayOutputStream bytes = new FastByteArrayOutputStream();
		
		// Se crear los nuevos ficheros.
		try {
			FileInputStream fileInputStream = new FileInputStream(timestampFile);
			DataInputStream dataInput = new DataInputStream(fileInputStream);
			timestamp = new Date(dataInput.readLong());
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			throw new DiskCacheException(String.format("Imposible crear el fichero fichero %s", timestampFile.getName()));
		} catch (IOException e) {
			throw new DiskCacheException(String.format("Imposible escribir en el fichero %s", timestampFile.getName()));
		}
		
		try {
			FileInputStream fileInputStream = new FileInputStream(bytesFile);
			DataInputStream dataInput = new DataInputStream(fileInputStream);
			
			byte[] readBuffer = new byte[128];
			bytes.resetBuffer();
			while(true) {
				int readed = dataInput.read(readBuffer);
				if (readed == -1) {
					break;
				} else {
					bytes.write(readBuffer, 0, readed);
				}
			}
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			throw new DiskCacheException(String.format("Imposible crear el fichero fichero %s", bytesFile.getName()));
		} catch (IOException e) {
			throw new DiskCacheException(String.format("Imposible escribir en el fichero %s", bytesFile.getName()));
		}
		
		return new CacheEntry(timestamp, bytes.toByteArray());
	}

	public void removeEntry(long id) throws DiskCacheException {
		File dir = directoryManager.getDirById(id);
		File timestampFile = getTimestampFile(dir, id);
		File bytesFile = getBytesFile(dir, id);

		if (!timestampFile.delete()) {
			throw new DiskCacheException(String.format("No es posible borrar el fichero %s", timestampFile.getName()));
		}
		if (!bytesFile.delete()) {
			throw new DiskCacheException(String.format("No es posible borrar el fichero %s", bytesFile.getName()));
		}
	}

	public ICacheEntry addEntry(long id, Date timestamp, byte[] bytes) throws DiskCacheException {
		File dir = directoryManager.getDirById(id);
		File timestampFile = getTimestampFile(dir, id);
		File bytesFile = getBytesFile(dir, id);
		
		// Se crear los nuevos ficheros.
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(timestampFile);
			DataOutputStream dataOutput = new DataOutputStream(fileOutputStream);
			dataOutput.writeLong(timestamp.getTime());
			dataOutput.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			throw new DiskCacheException(String.format("Imposible crear el fichero fichero %s", timestampFile.getName()));
		} catch (IOException e) {
			throw new DiskCacheException(String.format("Imposible escribir en el fichero %s", timestampFile.getName()));
		}
		
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(bytesFile);
			DataOutputStream dataOutput = new DataOutputStream(fileOutputStream);
			dataOutput.write(bytes);
			dataOutput.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			throw new DiskCacheException(String.format("Imposible crear el fichero fichero %s", bytesFile.getName()));
		} catch (IOException e) {
			throw new DiskCacheException(String.format("Imposible escribir en el fichero %s", bytesFile.getName()));
		}
		
		return new CacheEntry(timestamp, bytes);
	}

	private File getTimestampFile(File dir, long id) {
		return new File(dir, String.format("%s%d.ts", FILE_NAME_PREFIX, id));
	}

	private File getBytesFile(File dir, long id) {
		return new File(dir, String.format("%s%d.dat", FILE_NAME_PREFIX, id));
	}


}
