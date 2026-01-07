package com.gabinote.coffeenote.mail.dto.service

import com.gabinote.coffeenote.mail.enums.MailTemplate
import com.gabinote.coffeenote.mail.enums.MailType

data class MailSendReqServiceDto(
    val type: MailType,
    val recipients: List<String> = emptyList(),
    val title: String,
    val contents: Map<String, List<String>> = emptyMap(),
    val template: MailTemplate,
)