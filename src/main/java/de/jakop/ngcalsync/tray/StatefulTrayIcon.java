/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.jakop.ngcalsync.tray;

import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import de.jakop.ngcalsync.Constants;
import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;

/**
 * Represent system tray icon with multiple states, which are e.g. represented by blinking the icon.
 */

public class StatefulTrayIcon extends TrayIcon implements Observer {

	/** blinking interval in milliseconds */
	private static final long BLINK_INTERVAL_MILLISECONDS = 100L;

	private final Image iconNormal;
	private final Image iconWorking;

	private Thread stateThread;
	private AbstractFinishableRunnable stateRunnable;

	/**
	* Constructor.
	*
	* @throws java.io.IOException if an I/O error occurs while reading the icon image resources
	*/
	public StatefulTrayIcon() throws IOException {
		super(new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR));
		setToolTip(Constants.APPLICATION_NAME);
		setImageAutoSize(true);

		iconNormal = ImageIO.read(getClass().getResource(Constants.ICON_NORMAL));
		iconWorking = ImageIO.read(getClass().getResource(Constants.ICON_WORKING));

		setState(SynchronizeState.IDLE);
	}

	/**
	 * Sets the tray icon to the given state. Waits for the previous state to terminate.
	 *
	 * @param state
	 */
	public void setState(final SynchronizeState state) {

		if (stateRunnable != null) {
			stateRunnable.finish();
		}

		if (state == SynchronizeState.RUNNING) {
			stateRunnable = new BlinkingRunnable();
		} else if (state == SynchronizeState.IDLE) {
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

	@Override
	public void update(final Observable o, final Object arg) {
		if (arg instanceof SynchronizeState) {
			final SynchronizeState event = (SynchronizeState) arg;
			setState(event);
		}
	}
}
