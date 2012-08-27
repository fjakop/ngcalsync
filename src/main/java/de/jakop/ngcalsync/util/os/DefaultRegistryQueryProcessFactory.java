package de.jakop.ngcalsync.util.os;

import java.io.IOException;

/**
 * Default implementation of {@link IRegistryQueryProcessFactory}.
 * 
 * @author fjakop
 *
 */
public class DefaultRegistryQueryProcessFactory implements IRegistryQueryProcessFactory {

	@Override
	public final Process createQueryProcess(final String location, final String key) throws IOException {
		return Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);
	}
}
