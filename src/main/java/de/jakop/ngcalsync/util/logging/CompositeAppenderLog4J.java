package de.jakop.ngcalsync.util.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * log4j appender outputting to an {@link Composite}.
 * <br>
 * The different log levels are visualized like this:
 * <table border="1">
 * <tr><td>loglevel</td><td>text property</td></tr>
 * <tr><td>DEBUG</td><td>grey</td></tr>
 * <tr><td>INFO</td><td>black</td></tr>
 * <tr><td>WARN</td><td>dark orange</td></tr>
 * <tr><td>ERROR</td><td>red</td></tr>
 * <tr><td>FATAL</td><td>dark red (bold)</td></tr>
 * </table>
 * Configuration may be like this:
 * <pre>
 * log4j.appender.gui={@link de.jakop.ngcalsync.util.logging.CompositeAppenderLog4J}
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
public class CompositeAppenderLog4J extends WriterAppender {

	private static final String LINEBREAK = "\n";

	private final StyledText text;


	/**
	 * Creates a new instance
	 */
	public CompositeAppenderLog4J(final Composite parent, final int style) {
		Validate.notNull(parent);

		text = new StyledText(parent, style);
		text.setEditable(false);

		// always scroll to bottom on modification
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				text.setTopIndex(text.getLineCount() - 1);

			}
		});

	}

	@Override
	public void append(final LoggingEvent event) {

		final String message = layoutMessage(event);

		text.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				final StyleRange styleRange = getStyleForLevel(event, text.getText().length(), message.length());

				text.append(message);
				text.setStyleRange(styleRange);
			}
		});
	}

	private String layoutMessage(final LoggingEvent event) {
		final StringBuilder layoutedMsg = new StringBuilder();
		layoutedMsg.append(getLayout().format(event));
		if (event.getThrowableInformation() != null) {
			final String stacktrace = StringUtils.trimToEmpty(StringUtils.join(event.getThrowableInformation().getThrowableStrRep(), LINEBREAK));
			layoutedMsg.append("Stacktrace:").append(LINEBREAK).append(stacktrace);
		}

		final String message = layoutedMsg.toString();
		return message;
	}

	private StyleRange getStyleForLevel(final LoggingEvent event, final int start, final int length) {

		final StyleRange style = new StyleRange();

		switch (event.getLevel().toInt()) {
			case Priority.DEBUG_INT:
				style.foreground = text.getDisplay().getSystemColor(SWT.COLOR_GRAY);
				break;
			case Priority.INFO_INT:
				style.foreground = text.getDisplay().getSystemColor(SWT.COLOR_BLACK);
				break;
			case Priority.WARN_INT:
				style.foreground = text.getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW);
				break;
			case Priority.ERROR_INT:
				style.foreground = text.getDisplay().getSystemColor(SWT.COLOR_RED);
				break;
			case Priority.FATAL_INT:
				style.foreground = text.getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
				style.fontStyle = SWT.BOLD;
				break;
			default:
				style.foreground = text.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}

		style.start = start;
		style.length = length;

		return style;
	}



	@Override
	public void close() {
		// nop
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

}
