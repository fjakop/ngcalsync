package de.jakop.ngcalsync.application;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


/**
 * Facade for a quartz-scheduler and trigger which accepts a cron string and triggers the
 * synchronisation at given times.
 * 
 * @author fjakop
 */
public class SchedulerFacade {

	private static final String SYNC_TRIGGER_NAME = "ngcalsync.trigger"; //$NON-NLS-1$
	private static final String SYNC_JOB_NAME = "ngcalsync.syncJob"; //$NON-NLS-1$
	private static final String SYNC_GROUP_NAME = "ngcalsync.syncGroup"; //$NON-NLS-1$

	/** Default is every 15 minutes */
	public static final String DEFAULT_CRON_EXPRESSION = "0 */15 * * * ?"; //$NON-NLS-1$

	private final Application application;

	private boolean started = false;
	private final Scheduler scheduler;
	private JobKey jobKey;
	private CronExpression cronTimerExpression;

	/**
	 * 
	 * @param application
	 * @throws ParseException
	 * @throws SchedulerException 
	 */
	public SchedulerFacade(final Application application) throws ParseException, SchedulerException {
		Validate.notNull(application);
		this.application = application;
		cronTimerExpression = new CronExpression(DEFAULT_CRON_EXPRESSION);
		scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
	}

	/**
	 * (Re-)starts the scheduling
	 *  
	 * @throws SchedulerException
	 */
	public void start() throws SchedulerException {
		started = true;
		scheduler.resumeJob(jobKey);
	}

	/**
	 * Pauses the scheduling
	 * 
	 * @throws SchedulerException
	 */
	public void pause() throws SchedulerException {
		started = false;
		scheduler.pauseJob(jobKey);
	}

	/**
	 * Triggers synchronisation right now, if not already running.
	 * 
	 * @throws SchedulerException
	 */
	public void triggerNow() throws SchedulerException {

		if (isJobRunning()) {
			return;
		}

		if (started) {
			scheduler.pauseJob(jobKey);
		}

		scheduler.triggerJob(jobKey);

		if (started) {
			scheduler.resumeJob(jobKey);
		}
	}

	/**
	 * Reschedules the synchronizin job with the given cron expression.
	 * <p>Some examples:
	 * <table>
	 * <tr><td>Every 15 min. (default)</td><td>0 *&frasl;15 * * * ?</td><tr>
	 * <tr><td>Every hour at 20 min.</td><td>0 20 * * * ?</td><tr>
	 * <tr><td>Every day from 9-17 o'clock every 30 min.</td><td>0 *&frasl;30 9-17 * * ?</td><tr>
	 * <tr><td>From MON to FRI 8-18 o'clock every hour </td><td>0 * 8-18 * * MON-FRI</td><tr>
	 * </table>
	 * 
	 * @param cronExpression
	 * @throws ParseException 
	 * @throws SchedulerException 
	 */
	public void schedule(final String cronExpression) throws ParseException, SchedulerException {
		Validate.notNull(cronExpression);
		cronTimerExpression = new CronExpression(cronExpression);
		scheduler.deleteJob(jobKey);

		final JobDataMap syncDataMap = new JobDataMap();
		syncDataMap.put(SynchronizeJob.APPLICATION, application);

		final JobDetail job = JobBuilder.newJob(SynchronizeJob.class) //
				.withIdentity(SYNC_JOB_NAME, SYNC_GROUP_NAME) //
				.setJobData(syncDataMap)//
				.build();

		jobKey = job.getKey();

		final Trigger trigger = TriggerBuilder.newTrigger() //
				.withIdentity(SYNC_TRIGGER_NAME, SYNC_GROUP_NAME) //
				.withSchedule(CronScheduleBuilder.cronSchedule(cronTimerExpression)) //
				.forJob(job) //
				.build();

		scheduler.scheduleJob(job, trigger);

		if (!started) {
			scheduler.pauseJob(jobKey);
		}
	}

	private boolean isJobRunning() throws SchedulerException {
		final List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
		for (final JobExecutionContext job : jobs) {
			if (job.getJobDetail().getKey() == jobKey) {
				return true;
			}
		}
		return false;
	}
}
