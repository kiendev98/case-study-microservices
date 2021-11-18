package com.kien.microservices.composite.product.config

import com.kien.util.logs.logWithClass
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

val LOG = logWithClass<ThreadConfig>()

@Configuration
class ThreadConfig(
    @Value("\${app.threadPoolSize:10}") private val threadPoolSize: Int,
    @Value("\${app.taskQueueSize:100}") private val taskQueueSize: Int
) {

    @Bean
    fun publishEventScheduler(): Scheduler {
        LOG.info("Creates a messageScheduler with connectionPoolSize = {}", threadPoolSize)
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-tool")
    }
}