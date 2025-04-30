package com.wheretopop.config

import io.github.bonigarcia.wdm.WebDriverManager
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
        }
        options.addArguments(chromeArguments)
        return options
    }

    /**
     * WebDriver Bean 설정
     * - chromeOptions Bean을 주입받아 사용합니다.
     * - WebDriverManager를 통해 ChromeDriver를 설정합니다.
     * - Spring이 Bean을 소멸시킬 때(애플리케이션 종료 등) 자동으로 driver.quit()를 호출하도록 destroyMethod 설정합니다.
     * - 기본적으로 Singleton 스코프로 생성
     */
    @Bean(destroyMethod = "quit") // Bean 소멸 시 quit 메소드 자동 호출
    fun chromeDriver(chromeOptions: ChromeOptions): WebDriver {
        WebDriverManager.chromedriver().setup()
        val driver = ChromeDriver(chromeOptions)
        return driver
        // 참고: 만약 여러 요청이 동시에 이 드라이버를 사용한다면 스레드 안전성 문제가 발생할 수 있음
        // 동시성 문제가 우려된다면 WebDriver Pooling 등을 고려해야 함
        // 기본적인 스케줄러 기반의 단일 스크래핑 작업 등에는 Singleton 스코프가 효율적
    }
}