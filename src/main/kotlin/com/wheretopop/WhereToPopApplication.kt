package com.wheretopop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class WhereToPopApplication

fun main(args: Array<String>) {
	runApplication<WhereToPopApplication>(*args)
}
