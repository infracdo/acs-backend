package com.acs_tr069.test_tr069.Config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@EnableAsync
public class Tr069AppConfiguration {
    @Value("${CoreThreads}")
    private int corethreads;

    @Value("${MaxCoreThreads}")
    private int maxcorethreads;

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corethreads);
        executor.setMaxPoolSize(maxcorethreads);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(1000000000);
        return multipartResolver;
    }
}
