/*******************************************************************************
 * Copyright © Universitat Jaume I de Castelló 2015.
 * Aquest programari es distribueix sota les condicions de llicència EUPL 
 * o de qualsevol altra que la substituisca en el futur.
 * La llicència completa es pot descarregar de 
 * https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11
 *******************************************************************************/
package es.uji.control.commons.api;

import java.util.Date;

import es.uji.control.controller.core.service.ControllerException;
import es.uji.control.controller.core.service.UnavailableControllerException;
import es.uji.control.controller.mifare.CommandMifare;
import es.uji.control.controller.mifare.CommandMifareAdmin;
import es.uji.control.controller.mifare.MifareControllerException;
import es.uji.control.controller.mifare.MifareControllerExceptionEnum;
import es.uji.control.controller.mifare.MifareKey;
import es.uji.control.controller.mifare.MifareKeyType;
import es.uji.control.controller.mifare.MifareTagId;
import es.uji.control.controller.mifare.MifareUtils;

public class UJIApi {
	
	final static private MifareKey KEY_A = MifareUtils.instantiateKey(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 });
	final static private MifareKey KEY_B = MifareUtils.instantiateKey(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 });
	
	final static private byte[] accessConditions = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	
	final static private int APLICATION_SECTOR = 3;
	
	private CommandMifare commandMifare;
	private MifareTagId mifareTagId;
	
	public UJIApi(CommandMifare commandMifare, MifareTagId mifareTagId) {
		this.commandMifare = commandMifare;
		this.mifareTagId = mifareTagId;
	}
	
	public UJIData get() throws UnavailableControllerException, MifareControllerException, UJIDataException {
		// Seleccionamos el tag
		commandMifare.select(mifareTagId);
		// Logging en el sector
		commandMifare.loginKey(mifareTagId, KEY_A, MifareKeyType.KEY_A, APLICATION_SECTOR);
		// Leer los tres bloques del sector y se convierten en datos.
		return read();
	}
	
	public void set(UJIData data) throws UnavailableControllerException, MifareControllerException {
		// Seleccionamos el tag
		commandMifare.select(mifareTagId);
		// Logging en el sector
		commandMifare.loginKey(mifareTagId, KEY_B, MifareKeyType.KEY_B, APLICATION_SECTOR);
		// Se escriben los datos
		write(data);
	}
	
	public void issue(CommandMifareAdmin commandMifareAdmin, UJIData data) throws UnavailableControllerException, MifareControllerException, UJIIssueException {
		// Seleccionamos el tag
		commandMifare.select(mifareTagId);
		// Ahora los diversos intentos.
		try {
			// Logging en el sector
			commandMifare.loginKey(mifareTagId, KEY_B, MifareKeyType.KEY_B, APLICATION_SECTOR);
			try {
				// Leemos los datos
				UJIData oldData = read();
				// La tarjeta no debe estar en uso
				if (!oldData.isErased()) {
					throw new UJIIssueException("La tarjeta esta en uso");
				}
				// Escribimos los nuevos datos.
				write(data);
			} catch (UJIDataException dataEx) {
				// La tarjeta tiene los datos corrompidos, asi que escribimos
				write(data);
			}
		} catch (MifareControllerException mfEx) {
			// El lector nos dispara una excepcion, comprobamos de que tipo es y la tratamos.
			if (mfEx.getCode() == MifareControllerExceptionEnum.AUTH_OPERATION_ERROR) {
				// Posiblemente la llave no este formateada (tiene las claves de transporte).
				try {
					commandMifareAdmin.retryLoginKeyA(mifareTagId, MifareUtils.PHILIPS_TRANSPORT_KEY, APLICATION_SECTOR);					
					// Se formatea (ya no se usan las llaves de transporte).
					format();
					// Reseteamos de nuevo.
					commandMifareAdmin.retryLoginKeyB(mifareTagId, KEY_B, APLICATION_SECTOR);					
					// Se escriben los datos de la llave.
					write(data);
				} catch (ControllerException e) {
					throw new MifareControllerException(e.getMessage());
				}
			} else {
				// Cualquier otro error
				throw mfEx;
			}
		}
	}
	
	public void erase() throws UnavailableControllerException, MifareControllerException, UJIDataException {
		// Seleccionamos el tag
		commandMifare.select(mifareTagId);
		// Logging en el sector SCAAD
		commandMifare.loginKey(mifareTagId, KEY_B, MifareKeyType.KEY_B, APLICATION_SECTOR);
		// Leer los tres bloques del sector y se convierten en datos.
		int firstBlock = mifareTagId.getTagType().getFirstBlockOfSector(APLICATION_SECTOR);
		UJIRaw raw = UJIRaw.getInstance(
			commandMifare.read(mifareTagId, firstBlock), 
			commandMifare.read(mifareTagId, firstBlock + 1), 
			commandMifare.read(mifareTagId, firstBlock + 2)
			);
		UJIData testData = raw.getData();
		// Se borra la tarjeta
		testData.setExpirationDate(new Date().getTime());
		testData.setErased(true);
		// Se escriben los nuevos datos
		raw.setData(testData);
		commandMifare.write(mifareTagId, firstBlock, raw.block0);
		commandMifare.write(mifareTagId, firstBlock + 1, raw.block1);
		commandMifare.write(mifareTagId, firstBlock + 2, raw.block2);
	}
	
	private void format() throws UnavailableControllerException, MifareControllerException {
		// Se compone el bloque 3
		byte[] block3 = new byte[16];
		System.arraycopy(KEY_A.getBytes(), 0, block3, 0, 6);
		System.arraycopy(accessConditions, 0, block3, 6, 4);
		System.arraycopy(KEY_B.getBytes(), 0, block3, 10, 6);
		// Se personaliza el trailer sector 
		int firstBlock = mifareTagId.getTagType().getFirstBlockOfSector(APLICATION_SECTOR);
		commandMifare.write(mifareTagId, firstBlock + 3, block3);
	}
	
	private UJIData read() throws UnavailableControllerException, MifareControllerException, UJIDataException {
		int firstBlock = mifareTagId.getTagType().getFirstBlockOfSector(APLICATION_SECTOR);
		UJIRaw raw = UJIRaw.getInstance(
				commandMifare.read(mifareTagId, firstBlock), 
				commandMifare.read(mifareTagId, firstBlock + 1), 
				commandMifare.read(mifareTagId, firstBlock + 2)
				);
		return raw.getData();
	}
	
	private void write(UJIData data) throws UnavailableControllerException, MifareControllerException {
		int firstBlock = mifareTagId.getTagType().getFirstBlockOfSector(APLICATION_SECTOR);
		UJIRaw raw = UJIRaw.getInstance(data);
		commandMifare.write(mifareTagId, firstBlock, raw.block0);
		commandMifare.write(mifareTagId, firstBlock + 1, raw.block1);
		commandMifare.write(mifareTagId, firstBlock + 2, raw.block2);
	}
}
