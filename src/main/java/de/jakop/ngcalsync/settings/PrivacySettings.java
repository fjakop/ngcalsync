package de.jakop.ngcalsync.settings;

/**
 * Encapsulates settings for protecting privacy of event's data.
 * 
 * @author fjakop
 *
 */
public final class PrivacySettings {

	private final boolean transferTitle;
	private final boolean transferDescription;
	private final boolean transferLocation;

	/**
	 * 
	 * @param transferTitle
	 * @param transferDescription
	 * @param transferLocation
	 */
	public PrivacySettings(final boolean transferTitle, final boolean transferDescription, final boolean transferLocation) {
		this.transferTitle = transferTitle;
		this.transferDescription = transferDescription;
		this.transferLocation = transferLocation;
	}

	/**
	 * 
	 * @return <code>true</code>, if the event's title is to be transferred to Google
	 */
	public boolean isTransferTitle() {
		return transferTitle;
	}

	/**
	 * 
	 * @return <code>true</code>, if the event's description is to be transferred to Google
	 */
	public boolean isTransferDescription() {
		return transferDescription;
	}

	/**
	 * 
	 * @return <code>true</code>, if the event's location is to be transferred to Google
	 */
	public boolean isTransferLocation() {
		return transferLocation;
	}


}
