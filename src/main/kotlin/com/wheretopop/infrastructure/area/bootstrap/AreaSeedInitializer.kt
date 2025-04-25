package com.wheretopop.infrastructure.area.bootstrap

import com.wheretopop.infrastructure.area.AreaRepository
import kotlinx.coroutines.runBlocking
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AreaSeedInitializer(
    private val areaRepository: AreaRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        runBlocking {
            val seedAreas = AreaSeedData.createDefaultAreas()
            val seedCount = seedAreas.size.toLong()

            val existingCount = areaRepository.findAll().size.toLong()
            if (existingCount == 0L || existingCount < seedCount) {
                areaRepository.save(seedAreas)
            }
        }
    }
}
