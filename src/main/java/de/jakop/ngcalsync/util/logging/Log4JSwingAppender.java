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
package de.jakop.ngcalsync.util.logging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * log4j appender outputting to a TextArea.
 * The different log levels are visualized like this:
 * <table border="1">
 * <tr><td>loglevel</td><td>text property</td></tr>
 * <tr><td>DEBUG</td><td>grey</td></tr>
 * <tr><td>INFO</td><td>black</td></tr>
 * <tr><td>WARN</td><td>dark orange</td></tr>
 * <tr><td>ERROR</td><td>red</td></tr>
 * <tr><td>FATAL</td><td>dark red</td></tr>
 * </table>
 * <br>
 * Configuration is like this:
 * <pre>
 * log4j.appender.gui=de.jakop.ngcalsync.util.logging.Log4JSwingAppender
 * log4j.appender.gui.layout=org.apache.log4j.PatternLayout
 * log4j.appender.gui.layout.ConversionPattern=%5p - %m%n
 * log4j.rootLogger = DEBUG, gui
 * </pre>
 *
 * Access to a configured named instance by code:
 * <pre>((GUIAppender) Logger.getRootLogger().getAppender("gui"))</pre>
 *  *
 * @author fjakop
 */
public class Log4JSwingAppender extends AppenderSkeleton {

	private static final Font DEFAULT_FONT = new Font("COURIER", Font.PLAIN, 12); //$NON-NLS-1$
	private static final String LINEBREAK = "\n"; //$NON-NLS-1$
	private final JPanel panel;
	private final JTextPane textPane = new JTextPane();
	private ObservableBridge observable;

	/**
	 * Creates a new instance
	 */
	public Log4JSwingAppender() {
		textPane.setEditable(false);
		textPane.setFont(DEFAULT_FONT);
		textPane.setSelectionColor(textPane.getBackground());
		textPane.setSelectedTextColor(null);
		final JScrollPane sp = new JScrollPane(textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(sp, BorderLayout.CENTER);
	}

	@Override
	protected void append(final LoggingEvent event) {
		textPane.setEditable(true);
		String layoutedMsg = getLayout().format(event);
		if (event.getThrowableInformation() != null) {
			final String stacktrace = StringUtils.trimToEmpty(StringUtils.join(event.getThrowableInformation().getThrowableStrRep(), LINEBREAK));
			layoutedMsg += "Stacktrace:\n" + stacktrace; //$NON-NLS-1$
		}
		final int length = textPane.getDocument().getLength();
		textPane.setSelectionStart(length);
		textPane.setSelectionEnd(length);
		textPane.setParagraphAttributes(getAttributeSet(event), false);
		textPane.replaceSelection(layoutedMsg);
		textPane.setEditable(false);

		observable.setChanged();
		observable.notifyObservers(event.getLevel());
	}

	/**
	 * @param observer
	 * @see Observable#addObserver(Observer)
	 */
	public void addObserver(final Observer observer) {
		getObservable().addObserver(observer);
	}

	private ObservableBridge getObservable() {
		if (observable == null) {
			observable = new ObservableBridge();
		}
		return observable;
	}

	private AttributeSet getAttributeSet(final LoggingEvent event) {
		final MutableAttributeSet attr = new SimpleAttributeSet();
		switch (event.getLevel().toInt()) {
			case Priority.DEBUG_INT:
				StyleConstants.setForeground(attr, Color.gray);
				break;
			case Priority.INFO_INT:
				StyleConstants.setForeground(attr, Color.black);
				break;
			case Priority.WARN_INT:
				StyleConstants.setForeground(attr, Color.orange.darker());
				break;
			case Priority.ERROR_INT:
				StyleConstants.setForeground(attr, Color.red);
				break;
			case Priority.FATAL_INT:
				StyleConstants.setForeground(attr, Color.red.darker());
				StyleConstants.setBold(attr, true);
				break;
			default:
				StyleConstants.setForeground(attr, Color.black);
		}
		return attr;
	}

	@Override
	public void close() {
		// nop
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 *
	 * @return the panel which shows the log entries
	 */
	public JComponent getLogPanel() {
		return panel;
	}

	private class ObservableBridge extends Observable {

		@Override
		protected synchronized void setChanged() {
			super.setChanged();
		}
	}
}
