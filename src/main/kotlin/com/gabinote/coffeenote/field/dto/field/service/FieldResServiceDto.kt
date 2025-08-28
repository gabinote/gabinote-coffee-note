package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.common.dto.attribute.service.AttributeResServiceDto
import org.bson.types.ObjectId

data class FieldResServiceDto(
    val id: ObjectId,
    val externalId: String,
    val default: Boolean,
    val name: String,
    val icon: String,
    val type: String,
    val attributes: Set<AttributeResServiceDto>,
    val owner: String?,
)