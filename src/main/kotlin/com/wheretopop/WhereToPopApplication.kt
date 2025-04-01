package com.wheretopop

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@OpenAPIDefinition(info = Info(title = "WhereToPop API", version = "1.0", description = "WhereToPop API"))
@SpringBootApplication
@ConfigurationPropertiesScan
class WhereToPopApplication

fun main(args: Array<String>) {
	runApplication<WhereToPopApplication>(*args)
}
