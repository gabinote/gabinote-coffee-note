package com.gabinote.coffeenote.outbox.dto.service

data class OutBoxCreateReqServiceDto(
    val eventType: String,
    val payload: String,
)