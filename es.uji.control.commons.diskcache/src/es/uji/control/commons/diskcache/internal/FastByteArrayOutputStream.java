/*******************************************************************************
 * Copyright © Universitat Jaume I de Castelló 2015.
 * Aquest programari es distribueix sota les condicions de llicència EUPL 
 * o de qualsevol altra que la substituisca en el futur.
 * La llicència completa es pot descarregar de 
 * https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11
 *******************************************************************************/
package es.uji.control.commons.diskcache.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Esta clase reemplaza a <tt>ByteArrayOutputStream</tt> evitando 
 * la sincronizacion en cada escritura.
 */
public class FastByteArrayOutputStream extends OutputStream {

	/**
	 * Define la dimension por defecto del buffer de salida.
	 */
	public static final int DEFAULT_INIT_SIZE = 100;

	/**
	 * Define el incremente por defecto del buffer de salida.
	 */
	public static final int DEFAULT_BUMP_SIZE = 100;

	/**
	 * Numero de bytes en el buffer de salida.
	 */
	protected int count;

	/**
	 * Numero de bytes que se incrementara el buffer de salida.
	 */
	protected int bumpLen;

	/**
	 * Buffer de salida.
	 */
	protected byte[] buf;

	/**
	 * Crea una instancia de <tt>FastByteArrayOutputStream</tt> con un buffer de salida con 
	 * las dimensiones por defecto.
	 */
	public FastByteArrayOutputStream() {
		buf = new byte[DEFAULT_INIT_SIZE];
		bumpLen = DEFAULT_BUMP_SIZE;
	}

	/**
	 * Crea una instancia de <tt>FastByteArrayOutputStream</tt> con un crecimiento 
	 * del buffer con su valor por defecto y la dimension inicial del buffer
	 * segun el parametro proporcionado.
	 * 
	 * @param initialSize dimension inicial del buffer.
	 */
	public FastByteArrayOutputStream(int initialSize) {
		buf = new byte[initialSize];
		bumpLen = DEFAULT_BUMP_SIZE;
	}

	/**
	 * Crea un instancia de <tt>FastByteArrayOutputStream</tt> con un crecimiento 
	 * del buffer y una dimension inicial del buffer indicados por los
	 * parametros proporcionados. 
	 * 
	 * @param initialSize dimension inicial del buffer.
	 * @param bumpSize incremento del buffer.
	 */
	public FastByteArrayOutputStream(int initialSize, int bumpSize) {
		buf = new byte[initialSize];
		bumpLen = bumpSize;
	}

	/**
	 * Crea uns instancia de <tt>FastByteArrayOutputStream</tt> sin posibilidad
	 * del crecimiento del buffer. En caso de necesitar mas espacio, se producira
	 * una IOException.
	 * 
	 * @param buffer buffer de salida que se utilizara.
	 */
	public FastByteArrayOutputStream(byte[] buffer) {
		buf = buffer;
		bumpLen = 0;
	}

	/**
	 * Resetea el buffer.
	 */
	public void resetBuffer() {
		count = 0;
	}

	public void write(int b) throws IOException {
		if (count + 1 > buf.length) {
			bump(1);
		}
		buf[count++] = (byte) b;
	}

	public void write(byte[] fromBuf) throws IOException {
		int needed = count + fromBuf.length - buf.length;
		if (needed > 0) {
			bump(needed);
		}
		System.arraycopy(fromBuf, 0, buf, count, fromBuf.length);
		count += fromBuf.length;
		/*
		for (int i = 0; i < fromBuf.length; i++) {
			buf[count++] = fromBuf[i];
		}
		*/
	}

	public void write(byte[] fromBuffer, int offset, int length) throws IOException {
		int fromOffset;
		int fromLength;

		if (offset >= fromBuffer.length) {
			fromOffset = fromBuffer.length;
			fromLength = 0;
		} else {
			fromOffset = offset;
			fromLength = length > (fromBuffer.length - fromOffset) ? fromBuffer.length - fromOffset : length; 
		}
		int needed = count + fromLength - buf.length;
		if (needed > 0) {
			bump(needed);
		}
		int fromLen = offset + fromLength;

		System.arraycopy(fromBuffer, offset, buf, count, fromLen);
		count += fromLen;
		/*
		for (int i = offset; i < fromLen; i++) {
			buf[count++] = fromBuffer[i];
		}
		*/
	}
	
	public int write(InputStream inputStream, int size) throws IOException {
		
		int needed = count + size - buf.length;
		if (needed > 0) {
			bump(needed);
		}
		// Se leen los bytes directamente sobre el buffer.
		int readed = inputStream.read(buf, count, size);
		// Si esta en EOF, no hay nada que hacer
		if (readed == -1)
			return -1;
		// Se actualiza la dimension del buffer.
		count += readed;
		
		return readed;
	}

	/**
	 * Escribe el contenido de este <tt>FastByteArrayOutputStream</tt> sobre
	 * el OutputStream proporcionado como parametro. 
	 * 
	 * @param out OutputStream por el que se escribira.
	 * @throws IOException si ocurre cualquier error de E/S.
	 */
	public synchronized void writeTo(OutputStream out) throws IOException {
		out.write(buf, 0, count);
	}

	public String toString() {
		return new String(buf, 0, count);
	}

	/**
	 * Retorna el buffer de salida.
	 *
	 * @return buffer de salida.
	 */
	public byte[] getBufferBytes() {
		return buf;
	}

	/**
	 * Retorna la longitud utilizada en el buffer, o lo que es lo mismo,
	 * el indice en el buffer de donde se realizara la proxima lectura.
	 *
	 * @return Longutud utilizada en el buffer o indice para la proxima escritura.
	 */
	public int getBufferLength() {
		return count;
	}

	private void bump(int needed) throws IOException {
		if (bumpLen == 0)
			throw new IOException("El buffer esta configurado sin crecimiento.");
		
		byte[] toBuf = new byte[buf.length + needed + bumpLen];

		System.arraycopy(buf, 0, toBuf, 0, count);
		/*
		for (int i = 0; i < count; i++) {
			toBuf[i] = buf[i];
		}
		*/
		buf = toBuf;
	}
	
	public byte[] toByteArray() {
		byte[] retBuf = new byte[count];
		System.arraycopy(buf, 0, retBuf, 0, count);
		return retBuf;
	}

}
