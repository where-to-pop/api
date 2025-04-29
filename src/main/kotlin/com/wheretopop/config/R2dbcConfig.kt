package com.wheretopop.config

import com.wheretopop.infrastructure.area.AreaIdToLongConverter
import com.wheretopop.infrastructure.area.LongToAreaIdConverter
import com.wheretopop.infrastructure.area.R2dbcAreaRepository
import com.wheretopop.infrastructure.area.external.opendata.population.AreaPopulationIdToLongConverter
import com.wheretopop.infrastructure.area.external.opendata.population.LongToAreaPopulationIdConverter
import com.wheretopop.infrastructure.area.external.opendata.population.R2dbcAreaPopulationRepository
import com.wheretopop.infrastructure.popup.LongToPopupIdConverter
import com.wheretopop.infrastructure.popup.PopupIdToLongConverter
import com.wheretopop.infrastructure.popup.R2dbcPopupRepository
import com.wheretopop.infrastructure.popup.external.popply.LongToPopupPopplyIdConverter
import com.wheretopop.infrastructure.popup.external.popply.PopupPopplyIdToLongConverter
import com.wheretopop.infrastructure.popup.external.popply.R2dbcPopupPopplyRepository
import io.r2dbc.spi.ConnectionFactory
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.wheretopop.infrastructure"])
@EnableTransactionManagement
class R2dbcConfig : AbstractR2dbcConfiguration() {
    
    @Value("\${DB_HOST:localhost}")
    private lateinit var host: String

    @Value("\${DB_PORT:3306}")
    private var port: Int = 3306

    @Value("\${MYSQL_DATABASE:wheretopop}")
    private lateinit var database: String

    @Value("\${MYSQL_USER:root}")
    private lateinit var username: String

    @Value("\${MYSQL_PASSWORD:password}")
    private lateinit var password: String

    override fun connectionFactory(): ConnectionFactory {
        val configuration = MariadbConnectionConfiguration.builder()
            .host(host)
            .port(port)
            .database(database)
            .username(username)
            .password(password)
            .build()

        return MariadbConnectionFactory(configuration)
    }

    override fun getCustomConverters(): MutableList<Any> = mutableListOf(
        AreaIdToLongConverter(), LongToAreaIdConverter(),
        AreaPopulationIdToLongConverter(), LongToAreaPopulationIdConverter(),
        InstantToLocalDateTimeConverter(), LocalDateTimeToInstantConverter(),
        PopupIdToLongConverter(), LongToPopupIdConverter(),
        PopupPopplyIdToLongConverter(), LongToPopupPopplyIdConverter()
    )


    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    @Bean
    internal fun r2dbcAreaRepository(template: R2dbcEntityTemplate) = R2dbcAreaRepository(template)
    @Bean
    internal fun r2dbcAreaPopulationRepository(template: R2dbcEntityTemplate) = R2dbcAreaPopulationRepository(template)

    @Bean
    internal fun r2dbcPopupRepository(template: R2dbcEntityTemplate) = R2dbcPopupRepository(template)
    @Bean
    internal fun r2dbcPopupPopplyRepository(template: R2dbcEntityTemplate) = R2dbcPopupPopplyRepository(template)

}


@WritingConverter
class InstantToLocalDateTimeConverter : Converter<Instant, LocalDateTime> {
    override fun convert(source: Instant): LocalDateTime = LocalDateTime.ofInstant(source, ZoneOffset.UTC)
}

@ReadingConverter
class LocalDateTimeToInstantConverter : Converter<LocalDateTime, Instant> {
    override fun convert(source: LocalDateTime): Instant = source.toInstant(ZoneOffset.UTC)
}
