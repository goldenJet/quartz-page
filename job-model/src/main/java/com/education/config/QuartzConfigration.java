package com.education.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by leon.huang on 2017/5/19.
 */
@Configuration
public class QuartzConfigration {


    @Autowired
    DataSource dateSource;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    JobFactory jobFactory;

    @Bean
    public SchedulerFactoryBean startQuertz() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dateSource);
        schedulerFactoryBean.setTransactionManager(transactionManager);
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/quartz.properties"));
        //schedulerFactoryBean.setQuartzProperties(quartzProperties());
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }

    @Component
    class JobFactory extends AdaptableJobFactory {

        @Autowired
        private AutowireCapableBeanFactory capableBeanFactory;

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            //调用父类的方法
            Object jobInstance = super.createJobInstance(bundle);
            //进行注入
            capableBeanFactory.autowireBean(jobInstance);
            return jobInstance;
        }

    }



    //@Value("${org.quartz.scheduler.instanceName}")
    //private String quartzInstanceName;
    //
    //@Value("${org.quartz.dataSource.myDS.driver}")
    //private String myDSDriver;
    //
    //@Value("${org.quartz.dataSource.myDS.URL}")
    //private String myDSURL;
    //
    //@Value("${org.quartz.dataSource.myDS.user}")
    //private String myDSUser;
    //
    //@Value("${org.quartz.dataSource.myDS.password}")
    //private String myDSPassword;
    //
    //@Value("${org.quartz.dataSource.myDS.maxConnections}")
    //private String myDSMaxConnections;



    /**
     * @Author: Jet
     * @Description: 设置quartz属性
     * @Date: 2018/1/30 14:48
     */
    public Properties quartzProperties() {
        Properties prop = new Properties();

        // org.quartz.scheduler.instanceName属性可为任何值，用在 JDBC JobStore
        // 中来唯一标识实例，但是所有集群节点中必须相同。
        prop.put("quartz.scheduler.instanceName", "ServerScheduler");
        // instanceId 属性为 AUTO即可，基于主机名和时间戳来产生实例 ID。
        prop.put("org.quartz.scheduler.instanceId", "AUTO");

        //
        // Quartz内置了一个“更新检查”特性，因此Quartz项目每次启动后都会检查官网，Quartz是否存在新版本。这个检查是异步的，不影响Quartz项目本身的启动和初始化。
        // 设置org.quartz.scheduler.skipUpdateCheck的属性为true来跳过更新检查
        prop.put("org.quartz.scheduler.skipUpdateCheck", "false");

        //
        prop.put("org.quartz.scheduler.jobFactory.class", "org.quartz.simpl.SimpleJobFactory");

        // org.quartz.jobStore.class属性为 JobStoreTX，将任务持久化到数据中。因为集群中节点依赖于数据库来传播
        // Scheduler 实例的状态，你只能在使用 JDBC JobStore 时应用 Quartz 集群。
        // 这意味着你必须使用 JobStoreTX 或是 JobStoreCMT 作为 Job 存储；你不能在集群中使用 RAMJobStore。
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        // isClustered属性为 true，你就告诉了Scheduler实例要它参与到一个集群当中。这一属性会贯穿于调度框架的始终
        prop.put("org.quartz.jobStore.isClustered", "true");

        //
        // clusterCheckinInterval属性定义了Scheduler实例检入到数据库中的频率(单位：毫秒)。Scheduler查是否其他的实例到了它们应当检入的时候未检入；
        // 这能指出一个失败的 Scheduler 实例，且当前 Scheduler 会以此来接管任何执行失败并可恢复的 Job。
        // 通过检入操作，Scheduler 也会更新自身的状态记录。clusterChedkinInterval 越小，Scheduler
        // 节点检查失败的 Scheduler 实例就越频繁。默认值是 15000 (即15 秒)
        prop.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "10");
        prop.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
        prop.put("org.quartz.dataSource.quartzDataSource.URL", "jdbc:mysql://192.168.168.170:3306/wailian_crm?useUnicode=true&characterEncoding=utf8");
        prop.put("org.quartz.dataSource.quartzDataSource.user", "root");
        prop.put("org.quartz.dataSource.quartzDataSource.password", "wailianvisa0728");
        prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", 10);
        return prop;
    }

}
