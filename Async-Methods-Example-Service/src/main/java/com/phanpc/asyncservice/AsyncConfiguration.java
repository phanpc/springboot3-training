package com.phanpc.asyncservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

//phanpc: @EnableAsync annotation in Spring is used to enable Spring's asynchronous method 
// execution capability. When you annotate a configuration class with @EnableAsync, 
// it allows you to run methods annotated with @Async in a separate thread, thus enabling asynchronous processing

// @Async and run them in a separate thread pool. This can be particularly useful for tasks that are 
// time-consuming or can be performed independently of the main flow, such as sending emails, processing files, 
// or making remote API calls.
@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("CarThread-");
        executor.initialize();
        return executor;
    }

}
