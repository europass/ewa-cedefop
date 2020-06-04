/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.services.editor.jobs;

import com.google.inject.name.Named;
import javax.inject.Inject;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JK
 */
public class CleanUpUploadsJobActivator {

    private static final Logger LOG = LoggerFactory.getLogger(CleanUpUploadsJobActivator.class);

    @Inject
    public CleanUpUploadsJobActivator(final Quartz q,
            @Named("europass-ewa-services.files.quartz.cron") String cron,
            @Named("europass-ewa-services.files.repository") String uploadsDir,
            @Named("europass-ewa-services.files.fileMaxLifeTime") String maxLifeTime) throws SchedulerException {

        JobDetail jobDetail = JobBuilder.newJob(CleanUpUploadsJob.class)
                .withIdentity("myCleanUpJob")
                .build();

        jobDetail.getJobDataMap().put("uploads", uploadsDir);
        jobDetail.getJobDataMap().put("maxLifeTime", maxLifeTime);

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("crontrigger", "crontriggergroup1")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        LOG.debug("just before scheduling job");
        q.getScheduler().scheduleJob(jobDetail, cronTrigger);

    }

}
