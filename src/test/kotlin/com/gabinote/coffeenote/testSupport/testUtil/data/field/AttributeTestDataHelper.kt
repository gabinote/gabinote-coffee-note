package com.gabinote.coffeenote.testSupport.testUtil.data.field

import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto

object AttributeTestDataHelper {
    fun createTestAttributeCreateReqControllerDto(
        key: String = "test",
        value: Set<String> = setOf("test"),
    ) = AttributeCreateReqControllerDto(
        key = key,
        value = value,
    )

    fun createTestAttributeCreateReqServiceDto(
        key: String = "test",
        value: Set<String> = setOf("test"),
    ) = AttributeCreateReqServiceDto(
        key = key,
        value = value,
    )

    fun createTestAttributeUpdateReqServiceDto(
        key: String = "test",
        value: Set<String> = setOf("test"),
    ) = AttributeUpdateReqServiceDto(
        key = key,
        value = value,
    )

    fun createTestAttributeResControllerDto(
        key: String = "test",
        value: Set<String> = setOf("test"),
    ) = AttributeResControllerDto(
        key = key,
        value = value,
    )
}