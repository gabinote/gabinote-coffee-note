package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.util.validation.fieldType.RequiredFieldType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

/**
 * 기본 필드 생성 요청을 위한 컨트롤러 계층 DTO
 * @author 황준서
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldCreateDefaultReqControllerDto(
    /**
     * 필드 이름
     * 빈 값이 아니어야 하며 최대 50자까지 허용
     */
    @field:NotBlank(message = "name must not be blank")
    @field:Length(max = 50, message = "name must be at most 50 characters")
    val name: String,

    /**
     * 필드 아이콘
     * 빈 값이 아니어야 하며 최대 50자까지 허용
     */
    @field:NotBlank(message = "icon must not be blank")
    @field:Length(max = 50, message = "icon must be at most 50 characters")
    val icon: String,

    /**
     * 필드 타입
     * 유효한 필드 타입이어야 함
     */
    @field:RequiredFieldType
    val type: String,

    /**
     * 필드 속성 목록
     * 최대 5개까지 허용
     */
    @field:Valid
    @field:Size(max = 5, message = "attributes must be at most 5")
    val attributes: Set<AttributeCreateReqControllerDto> = setOf(),
)