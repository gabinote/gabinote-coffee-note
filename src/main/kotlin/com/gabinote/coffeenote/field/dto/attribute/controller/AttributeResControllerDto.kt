package com.gabinote.coffeenote.field.dto.attribute.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * 속성 응답을 위한 컨트롤러 계층 DTO
 * @author 황준서
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttributeResControllerDto(
    /**
     * 속성 키
     */
    val key: String,

    /**
     * 속성 값 집합
     */
    val value: Set<String>
)