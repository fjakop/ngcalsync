package de.jakop.ngcalsync.util;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TrayItem;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;

/**
 * Represent system tray icon with multiple states, which are e.g. represented by blinking the icon.
 */

public class StatefulTrayIcon {

	/** blinking interval in milliseconds */
	private static final long BLINK_INTERVAL_MILLISECONDS = 100L;

	private final TrayItem trayItem;

	private final Image iconNormal;
	private final Image iconWorking;

	private Thread stateThread;
	private AbstractFinishableRunnable stateRunnable;


	/**
	* States of icon.
	*/
	public enum State {
		/** normal state, i.e. not working */
		NORMAL,
		/** blinking state (working) */
		BLINK;
	}

	/**
	* Constructor.
	* 
	* @throws java.io.IOException if an I/O error occurs while reading the icon image resources
	*/
	public StatefulTrayIcon(final TrayItem trayItem) throws IOException {
		Validate.notNull(trayItem);
		this.trayItem = trayItem;
		trayItem.setToolTipText(Constants.APPLICATION_NAME);

		iconNormal = new Image(trayItem.getDisplay(), getClass().getResourceAsStream("/images/tray/icon_normal.png"));
		iconWorking = new Image(trayItem.getDisplay(), getClass().getResourceAsStream("/images/tray/icon_working.png"));

	}

	/**
	 * Sets the tray icon to the given state. Waits for the previous state to terminate.
	 * 
	 * @param state
	 */
	public void setState(final State state) {

		if (stateRunnable != null) {
			stateRunnable.finish();
		}

		if (state == State.BLINK) {
			stateRunnable = new BlinkingRunnable();
		} else if (state == State.NORMAL) {
			stateRunnable = new NormalRunnable();
		} else {
			throw new IllegalStateException(TechMessage.get().MSG_STATE_NOT_SUPPORTED(state));
		}

		stateThread = new Thread(stateRunnable);
		stateThread.start();

	}

	private abstract class AbstractFinishableRunnable implements Runnable {

		private boolean finish = false;

		protected final boolean isFinished() {
			return finish;
		}

		public final void finish() {
			finish = true;
		}
	}

	private class NormalRunnable extends AbstractFinishableRunnable {

		@Override
		public void run() {
			try {
				trayItem.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						trayItem.setImage(iconNormal);
					}
				});
			} catch (final Exception e) {
				// nop
			}
		}
	}

	private class BlinkingRunnable extends AbstractFinishableRunnable {

		/** temporary image used for blinking effect */
		private Image tempImage;

		@Override
		public void run() {

			try {
				trayItem.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						tempImage = trayItem.getImage();
					}
				});

				while (!isFinished()) {

					trayItem.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							trayItem.setImage(iconWorking);
						}
					});

					Thread.sleep(BLINK_INTERVAL_MILLISECONDS);

					trayItem.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							trayItem.setImage(tempImage);
						}
					});

					Thread.sleep(BLINK_INTERVAL_MILLISECONDS);
				}
			} catch (final Exception e) {
				/* ignore */
			}

		}
	}
}
