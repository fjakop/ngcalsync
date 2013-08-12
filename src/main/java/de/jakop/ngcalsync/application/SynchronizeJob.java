package de.jakop.ngcalsync.application;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job for actually executing the synchronisation. Triggered by the {@link SchedulerFacade}
 * 
 * @author fjakop
 */
public class SynchronizeJob implements Job {

	static final String APPLICATION = "application"; //$NON-NLS-1$

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {

		final Application application = (Application) context.getJobDetail().getJobDataMap().get(APPLICATION);
		if (!application.reloadSettings()) {
			application.synchronize();
		}
	}

}
