package hello.quartz;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(ApplicationInitializer.class);

    @Autowired
    private SimpleTriggerFactoryBean triggerFactory;

    @Autowired
    private SchedulerFactoryBean schedulerFactory;

    @Autowired
    private JobProperties jobProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Updating Quartz jobs ...");
        TriggerBuilder<SimpleTrigger> triggerBuilder = triggerFactory.getObject().getTriggerBuilder();
        SimpleTrigger simpleTrigger = triggerBuilder
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(jobProperties.getRepeatInterval()))
                .build();
        try {
            schedulerFactory.getObject().rescheduleJob(triggerFactory.getObject().getKey(), simpleTrigger);
        } catch (SchedulerException ex) {
            logger.error("Scheduler exception: " + ex);
        }
    }
}
