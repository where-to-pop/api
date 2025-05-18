package com.wheretopop.infrastructure.popup.external.x

import com.wheretopop.domain.popup.Popup
import com.wheretopop.shared.infrastructure.entity.XEntity
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class XProcessorImpl(
    private val xRepository: XRepository
) : XProcessor {

    override fun crawlAndSaveByPopup(popup: Popup) {
        val randomXResponses = mockXResponses.shuffled(Random).take(Random.nextInt(3, 6))
        val randomXs = randomXResponses.map { xResponse ->  XEntity.of(xResponse, popup.id)}

        xRepository.save(randomXs)
    }
}