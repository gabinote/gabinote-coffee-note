package com.gabinote.coffeenote.field.dto.field.service

import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto

/**
 * 필드 생성 요청을 위한 서비스 계층 DTO
 * @author 황준서
 */
data class FieldCreateReqServiceDto(

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
    val type: FieldType,

    /**
     * 필드 속성 목록
     */
    val attributes: Set<AttributeCreateReqServiceDto>,

    /**
     * 필드 소유자
     */
    val owner: String,
)