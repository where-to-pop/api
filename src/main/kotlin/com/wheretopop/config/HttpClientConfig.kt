package com.wheretopop.config

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.util.Timeout
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.util.function.Supplier

@Configuration
class HttpClientConfig {

    private fun createHttpClient(connectTimeoutMillis: Int, readTimeoutMillis: Int): CloseableHttpClient {
        val socketConfig = SocketConfig.custom()
            .setSoTimeout(Timeout.ofMilliseconds(readTimeoutMillis.toLong()))
            .build()

        val connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMillis.toLong()))
            .build()

        val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setDefaultSocketConfig(socketConfig)
            .setDefaultConnectionConfig(connectionConfig)
            .build()

        return HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build()
    }

    private fun createRequestFactory(connectTimeoutMillis: Int, readTimeoutMillis: Int): Supplier<ClientHttpRequestFactory> {
        return Supplier {
            val httpClient = createHttpClient(connectTimeoutMillis, readTimeoutMillis)
            HttpComponentsClientHttpRequestFactory(httpClient)
        }
    }

    @Bean
    fun seoulApiRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .rootUri("http://openapi.seoul.go.kr:8088")
            .requestFactory(createRequestFactory(5000, 30000))
            .build()
    }

    @Bean
    fun koreaDataPortalApiRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .rootUri("https://apis.data.go.kr")
            .requestFactory(createRequestFactory(5000, 30000))
            .build()
    }

    @Bean
    fun vWorldOpenApiRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .rootUri("https://api.vworld.kr")
            .requestFactory(createRequestFactory(5000, 30000))
            .build()
    }

    @Bean
    fun popupApiRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .rootUri("https://www.popply.co.kr/popup")
            .requestFactory(createRequestFactory(5000, 30000))
            .build()
    }
}