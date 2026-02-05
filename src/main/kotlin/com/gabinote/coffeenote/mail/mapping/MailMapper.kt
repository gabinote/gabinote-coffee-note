package com.gabinote.coffeenote.mail.mapping

import com.gabinote.coffeenote.mail.dto.event.MailSendEvent
import com.gabinote.coffeenote.mail.dto.service.MailSendReqServiceDto
import com.gabinote.coffeenote.mail.enums.MailTemplate
import com.gabinote.coffeenote.mail.enums.MailType
import org.mapstruct.Mapper

@Mapper(
    componentModel = "spring"
)
interface MailMapper {

    fun toEvent(dto: MailSendReqServiceDto, serviceName: String): MailSendEvent

    fun typeToString(type: MailType): String {
        return type.value
    }

    fun templateToString(template: MailTemplate): String {
        return template.value
    }
}