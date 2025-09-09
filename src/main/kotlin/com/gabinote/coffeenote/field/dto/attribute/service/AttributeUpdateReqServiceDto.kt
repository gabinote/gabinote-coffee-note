package com.gabinote.coffeenote.field.dto.attribute.service

/**
 * 속성 업데이트 요청을 위한 서비스 계층 DTO
 * @author 황준서
 */
data class AttributeUpdateReqServiceDto(
    /**
     * 속성 키
     */
    val key: String,

    /**
     * 속성 값 집합
     */
    val value: Set<String>
)