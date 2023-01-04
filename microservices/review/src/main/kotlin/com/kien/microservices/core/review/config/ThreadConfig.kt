package com.kien.microservices.core.review.config

import com.kien.util.logs.logWithClass
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

private val logger = logWithClass<ThreadConfig>()

@Configuration
class ThreadConfig(
    @Value("\${app.threadPoolSize:10}") private val threadPoolSize: Int,
    @Value("\${app.taskQueueSize:100}") private val taskQueueSize: Int
) {

    @Bean
    fun jdbcScheduler(): Scheduler {
        logger.info("Creates a jdbcScheduler with thread pool size = {}", threadPoolSize)
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "jdbc-pool")
    }
}
