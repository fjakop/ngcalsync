package de.jakop.ngcalsync.util.logging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

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
 * Log4j-Appender, der Log-Ausgaben in einer TextArea ausgibt.
 * Die verschiedenen LogLevel werden wie folgt dargestellt:
 * <table border="1">
 * <tr><td>LogLevel</td><td>Text-Eigenschaft</td></tr>
 * <tr><td>DEBUG</td><td>grau</td></tr>
 * <tr><td>INFO</td><td>schwarz</td></tr>
 * <tr><td>WARN</td><td>dunkles orange</td></tr>
 * <tr><td>ERROR</td><td>rot</td></tr>
 * <tr><td>FATAL</td><td>dunkles rot</td></tr>
 * </table>
 * <br>
 * Kann wie folgt konfiguriert werden
 * <pre>
 * log4j.appender.Programmausgaben=de.jakop.ngcalsync.util.logging.Log4JSwingAppender
 * log4j.appender.Programmausgaben.layout=org.apache.log4j.PatternLayout
 * log4j.appender.Programmausgaben.layout.ConversionPattern=%5p - %m%n
 * log4j.rootLogger = DEBUG, Programmausgaben
 * </pre>
 * 
 * Im Anwendungscode kann mittels
 * <pre>((GUIAppender) Logger.getRootLogger().getAppender("Programmausgaben"))</pre>
 * auf benannte Instance zugegriffen werden
 * 
 * @author fjakop
 */
public class Log4JSwingAppender extends AppenderSkeleton {

	private static final Font DEFAULT_FONT = new Font("COURIER", Font.PLAIN, 12);
	private static final String LINEBREAK = "\n";
	private final JPanel panel;
	private final JTextPane textPane = new JTextPane();

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
			layoutedMsg += "Stacktrace:\n" + stacktrace;
		}
		final int length = textPane.getDocument().getLength();
		textPane.setSelectionStart(length);
		textPane.setSelectionEnd(length);
		textPane.setParagraphAttributes(getAttributeSet(event), false);
		textPane.replaceSelection(layoutedMsg);
		textPane.setEditable(false);
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
		// TODO Auto-generated method stub
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public JComponent getLogPanel() {
		return panel;
	}

}
