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
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class CreateZipFromNotImportedFilesJobActivator {

    private static final Logger LOG = LoggerFactory.getLogger(CreateZipFromNotImportedFilesJobActivator.class);

    @Inject
    public CreateZipFromNotImportedFilesJobActivator(final Quartz q,
            final @Named("europass-ewa-services.files.not.imported.repository.path") String repoPath,
            final @Named("europass-ewa-services.files.not.imported.repository.quartz.cron") String cronScheduler,
            final @Named("europass-ewa-services.files.not.imported.repository.zip.cleanup.days") String cleanupTime,
            final @Named("europass-ewa-services.host.id") String hostID) throws SchedulerException {

        if (StringUtils.isNotBlank(cronScheduler)) {
            final JobDetail jobDetail = JobBuilder.newJob(CreateZipFromNotImportedFilesJob.class)
                    .withIdentity("myZipNotImportedJob").build();

            jobDetail.getJobDataMap().put("repoPath", repoPath);
            jobDetail.getJobDataMap().put("cleanupTime", cleanupTime);
            jobDetail.getJobDataMap().put("hostID", hostID);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("triggerZipNotImported", "groupZipNotImported")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronScheduler))
                    //.withSchedule(CronScheduleBuilder.cronSchedule("0 57 8-20 * * ?")) //example scheduler for testing
                    .build();

            LOG.debug("Just before scheduling job for compressing not imported files..");
            q.getScheduler().scheduleJob(jobDetail, trigger);
        }
    }
}
