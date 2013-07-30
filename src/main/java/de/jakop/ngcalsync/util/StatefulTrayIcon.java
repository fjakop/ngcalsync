package de.jakop.ngcalsync.util;

import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;

/**
 * Represent system tray icon with multiple states, which are e.g. represented by blinking the icon.
 */

public class StatefulTrayIcon extends TrayIcon {

	/** blinking interval in milliseconds */
	private static final long BLINK_INTERVAL_MILLISECONDS = 100L;

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
	public StatefulTrayIcon() throws IOException {
		super(new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR));
		setToolTip(Constants.APPLICATION_NAME);
		setImageAutoSize(true);

		iconNormal = ImageIO.read(getClass().getResource("/images/tray/icon_normal.png"));
		iconWorking = ImageIO.read(getClass().getResource("/images/tray/icon_working.png"));

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
			throw new IllegalStateException(TechMessage.get().MSG_STATE_NOT_SUPPORTED(state.toString()));
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
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						setImage(iconNormal);
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
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						tempImage = getImage();
					}
				});

				while (!isFinished()) {

					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							setImage(iconWorking);
						}
					});

					Thread.sleep(BLINK_INTERVAL_MILLISECONDS);

					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							setImage(tempImage);
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
