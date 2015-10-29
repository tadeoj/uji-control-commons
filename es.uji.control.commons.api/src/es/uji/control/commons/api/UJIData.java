/*******************************************************************************
 * Copyright © Universitat Jaume I de Castelló 2015.
 * Aquest programari es distribueix sota les condicions de llicència EUPL 
 * o de qualsevol altra que la substituisca en el futur.
 * La llicència completa es pot descarregar de 
 * https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11
 *******************************************************************************/
package es.uji.control.commons.api;

import java.util.Date;

public class UJIData {
	
	private long userId;
	private long cardId;
	private long issueDate;
	private long expirationDate;
	private boolean erased;
	
	public UJIData() {
		this.userId = 0;
		this.cardId = 0;
		this.issueDate = 0;
		this.expirationDate = 0;
		this.erased = true;
	}
	
	public UJIData(long userId, long cardId, long issueDate) {
		this.userId = userId;
		this.cardId = cardId;
		this.issueDate = issueDate;
		this.expirationDate = 0;
		this.erased = false;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCardId() {
		return cardId;
	}

	public void setCardId(long cardId) {
		this.cardId = cardId;
	}

	public long getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(long issueDate) {
		this.issueDate = issueDate;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationData) {
		this.expirationDate = expirationData;
	}

	public boolean isErased() {
		return erased;
	}

	public void setErased(boolean locked) {
		this.erased = locked;
	}
	
	public boolean isEnabledExpiration() {
		if (!isErased() && getExpirationDate() != 0)
			return true;
		else
			return false;
	}
	
	public boolean isExpirated(Date date) {
		if (getExpirationDate() < date.getTime())
			return true;
		else
			return false;
	}
	
	public String toString() {
		return String.format("userId=%d, cardId=%d, issueDate=%s, expirationDate=%s, erased=%b, enabledExpiration=%b", 
				getUserId(), getCardId(), 
				new Date(getIssueDate()).toString(), 
				new Date(getExpirationDate()).toString(),
				isErased(), isEnabledExpiration());
	}
	
	
}
