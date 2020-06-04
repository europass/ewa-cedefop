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
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;

/**
 * @author jos Created on 7/18/2017
 */
public class CreateZipFromExportedJsonJobActivator {

    private static final Logger LOG = LoggerFactory.getLogger(CreateZipFromExportedJsonJobActivator.class);

    @Inject
    public CreateZipFromExportedJsonJobActivator(final Quartz q,
            final @Named("europass-ewa-services.files.export.repository.json.path") String uploadDir,
            final @Named("europass-ewa-services.files.export.repository.json.quartz.cron") String cronScheduler,
            final @Named("europass-ewa-services.files.export.repository.zip.cleanup.days") String cleanupTime,
            final @Named("europass-ewa-services.host.id") String hostID) throws SchedulerException {

        if (StringUtils.isNotBlank(cronScheduler)) {
            final JobDetail jobDetail = JobBuilder.newJob(CreateZipFromExportedJsonJob.class)
                    .withIdentity("myZipExportJob").build();

            jobDetail.getJobDataMap().put("uploads", uploadDir);
            jobDetail.getJobDataMap().put("cleanupTime", cleanupTime);
            jobDetail.getJobDataMap().put("hostID", hostID);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("triggerZipJson", "groupZipJson")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronScheduler))
                    //.withSchedule(CronScheduleBuilder.cronSchedule("0 0 8-19 * * ?")) //example scheduler for testing
                    .build();

            LOG.debug("Just before scheduling job for compressing json files..");
            q.getScheduler().scheduleJob(jobDetail, trigger);
        }
    }

}
