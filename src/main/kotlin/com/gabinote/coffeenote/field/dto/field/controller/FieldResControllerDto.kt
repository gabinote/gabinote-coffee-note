package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto

/**
 * 필드 응답을 위한 컨트롤러 계층 DTO
 * @author 황준서
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldResControllerDto(
    /**
     * 필드 외부 식별자
     */
    val externalId: String,

    /**
     * 기본 필드 여부
     */
    val default: Boolean,

    /**
     * 필드 이름
     */
    val name: String,

    /**
     * 필드 아이콘
     */
    val icon: String,

    /**
     * 필드 타입
     */
    val type: String,

    /**
     * 필드 속성 목록
     */
    val attributes: Set<AttributeResControllerDto>,

    /**
     * 필드 소유자
     */
    val owner: String?,
)