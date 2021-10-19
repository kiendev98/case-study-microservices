package com.kien.licensing.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
class R2dbcConfig {

    @Bean
    fun databaseInitializer (factory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(factory)
        val resourcePopulator = ResourceDatabasePopulator(ClassPathResource("db/schema.sql"))
        initializer.setDatabasePopulator(resourcePopulator)
        return initializer
    }
}
