package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import java.util.*

/**
 * 필드 업데이트 요청을 위한 서비스 계층 DTO
 * @author 황준서
 */
data class FieldUpdateReqServiceDto(
    /**
     * 필드 외부 식별자
     */
    val externalId: UUID,

    /**
     * 필드 이름 (선택적)
     */
    val name: String?,

    /**
     * 필드 아이콘 (선택적)
     */
    val icon: String?,

    /**
     * 필드 속성 업데이트 목록
     */
    val attributes: Set<AttributeUpdateReqServiceDto> = emptySet(),

    /**
     * 필드 소유자
     */
    val owner: String,
)