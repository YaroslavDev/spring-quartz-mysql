package hello;

import org.quartz.SimpleTrigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(MyJob.class);
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean(JobDetailFactoryBean jobDetailFactoryBean) {
        SimpleTriggerFactoryBean simpleTriggerFactory = new SimpleTriggerFactoryBean();
        simpleTriggerFactory.setDescription("Just doin' my job!");
        simpleTriggerFactory.setJobDetail(jobDetailFactoryBean.getObject());
        simpleTriggerFactory.setStartDelay(2000);
        simpleTriggerFactory.setRepeatInterval(6000);
        simpleTriggerFactory.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return simpleTriggerFactory;
    }

    @Bean
    public JobFactory jobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,
                                                     PlatformTransactionManager transactionManager,
                                                     JobFactory jobFactory,
                                                     SimpleTriggerFactoryBean simpleTriggerFactoryBean) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setTransactionManager(transactionManager);
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactory.setJobFactory(jobFactory);
        schedulerFactory.setApplicationContextSchedulerContextKey("applicationContext");
        schedulerFactory.setTriggers(simpleTriggerFactoryBean.getObject());
//        Properties quartzProps = new Properties();
//        quartzProps.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
//        quartzProps.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
//        quartzProps.put("org.quartz.dataSource.quartzDataSource.URL", "jdbc:mysql://localhost:3306/quartz");
//        quartzProps.put("org.quartz.dataSource.quartzDataSource.user", "root");
//        quartzProps.put("org.quartz.dataSource.quartzDataSource.password", "mysql");
//        quartzProps.put("org.quartz.dataSource.quartzDataSource.maxConnections", "2");
//        schedulerFactory.setQuartzProperties(quartzProps);
        return schedulerFactory;
    }
}
