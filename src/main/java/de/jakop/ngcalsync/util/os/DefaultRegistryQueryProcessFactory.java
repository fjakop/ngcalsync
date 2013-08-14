package de.jakop.ngcalsync.util.os;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.i18n.LocalizedTechnicalStrings.TechMessage;

/**
 * Default implementation of {@link IRegistryQueryProcessFactory}.
 * 
 * @author fjakop
 *
 */
public class DefaultRegistryQueryProcessFactory implements IRegistryQueryProcessFactory {

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public final Process createQueryProcess(final String location, final String key) throws IOException {
		final String command = "reg query " + '"' + location + "\" /v " + key; //$NON-NLS-1$ //$NON-NLS-2$
		log.debug(TechMessage.get().MSG_EXECUTING_COMMAND(command));
		return Runtime.getRuntime().exec(command);
	}
}
