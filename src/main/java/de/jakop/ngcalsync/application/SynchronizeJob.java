package de.jakop.ngcalsync.application;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * TODO document me
 * @author fjakop
 *
 */
public class SynchronizeJob implements Job {

	public static final String APPLICATION = "application";

	private final Log log = LogFactory.getLog(getClass());

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		System.out.println(new Date() + " Job läuft");
		log.debug(String.format("Starting synchronize job %s", context.getJobDetail().getKey())); // TODO i18n

		final Application application = (Application) context.getJobDetail().getJobDataMap().get(APPLICATION);
		if (!application.reloadSettings()) {
			application.synchronize();
		}
		log.debug(String.format("Finished synchronize job %s", context.getJobDetail().getKey())); // TODO i18n
	}

}
