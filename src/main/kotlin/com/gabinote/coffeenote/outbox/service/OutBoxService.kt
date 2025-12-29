package com.gabinote.coffeenote.outbox.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.outbox.domain.OutBox
import com.gabinote.coffeenote.outbox.domain.OutBoxRepository
import org.springframework.stereotype.Service

@Service
class OutBoxService(
    private val outBoxRepository: OutBoxRepository,
    private val objectMapper: ObjectMapper,
) {

    fun create(eventType: String, payload: Any) {
        val payloadString = objectMapper.writeValueAsString(payload)
        val outBox = OutBox(
            eventType = eventType,
            payload = payloadString,
        )
        outBoxRepository.save(outBox)
    }


}