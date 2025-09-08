package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import org.bson.types.ObjectId

/**
 * 필드 응답을 위한 서비스 계층 DTO
 * @author 황준서
 */
data class FieldResServiceDto(
    /**
     * 필드 내부 식별자
     */
    val id: ObjectId,

    /**
     * 필드 외부 식별자
     */
    val externalId: String,

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
    val attributes: Set<AttributeResServiceDto>,

    /**
     * 필드 소유자
     */
    val owner: String?,

    /**
     * 기본 필드 여부
     */
    val default: Boolean,
)