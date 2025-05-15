package com.wheretopop.config

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebDriverConfig {

    // Chrome 옵션 설정을 위한 프로퍼티 값 주입 (application.yml/properties 에서 설정)
    @Value("\${selenium.chrome.headless:true}")
    private var headless: Boolean = true

    @Value("\${selenium.chrome.arguments:--disable-gpu,--no-sandbox,--disable-dev-shm-usage}") // 기본 인자값
    private lateinit var chromeArguments: List<String>

    /**
     * ChromeOptions Bean 설정
     * 필요한 Chrome 브라우저 실행 옵션을 설정합니다.
     */
    @Bean
    fun chromeOptions(): ChromeOptions {
        val options = ChromeOptions()
        if (headless) {
            options.addArguments("--headless")
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
            options.addArguments("--disable-gpu")
            options.addArguments("--remote-allow-origins=*")
        }
        options.addArguments(chromeArguments)
        return options
    }

    @Bean(destroyMethod = "quit")
    fun chromeDriver(chromeOptions: ChromeOptions): WebDriver {
        return ChromeDriver(chromeOptions)
    }
}