package de.jakop.ngcalsync.util.os;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jakop.ngcalsync.i18n.LocalizedUserStrings.UserMessage;

/**
 * Read keys from Windows registry
 * 
 * @author fjakop
 *
 */
public class WindowsRegistry {

	private final static Log log = LogFactory.getLog(WindowsRegistry.class);

	/**
	 * 
	 * @param location path in the registry
	 * @param key registry key
	 * @return registry value or null if not found
	 */
	public static final String readRegistry(final String location, final String key) {
		try {
			// Run reg query, then read output with StreamReader (internal class)
			final Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);

			final WindowsRegistry.StreamReader reader = new StreamReader(process.getInputStream());
			final ExecutorService pool = Executors.newSingleThreadExecutor();
			final Future<String> future = pool.submit(reader);
			final String output = future.get();

			// the output has the following format:
			// \n<Version information>\n\n<key>\t<registry type>\t<value>
			if (!output.contains("\t")) {
				return null;
			}

			// Parse out the value
			final String[] parsed = output.split("\t");
			return parsed[parsed.length - 1];
		} catch (final Exception e) {
			log.error(UserMessage.get().MSG_FAILED_TO_READ_REGISTRY(key), e);
			// TODO handle this correctly
			return null;
		}

	}


	static class StreamReader implements Callable<String> {
		private final InputStream input;

		public StreamReader(final InputStream is) {
			input = is;
		}

		@Override
		public String call() throws Exception {
			final StringWriter output = new StringWriter();

			int character;
			while ((character = input.read()) != -1) {
				output.write(character);
			}
			return output.toString();
		}

	}
}