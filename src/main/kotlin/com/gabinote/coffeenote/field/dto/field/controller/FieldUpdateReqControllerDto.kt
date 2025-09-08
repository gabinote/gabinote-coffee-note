package com.gabinote.coffeenote.field.dto.field.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.validation.string.blank.NullableNotBlank
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

/**
 * 필드 업데이트 요청을 위한 컨트롤러 계층 DTO
 * @author 황준서
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FieldUpdateReqControllerDto(
    /**
     * 필드 이름
     * 값이 제공될 경우 빈 값이 아니어야 하며 최대 50자까지 허용
     */
    @field:NullableNotBlank(resourceName = "name")
    @field:Length(max = 50, message = "name must be at most 50 characters")
    val name: String? = null,

    /**
     * 필드 아이콘
     * 값이 제공될 경우 빈 값이 아니어야 하며 최대 50자까지 허용
     */
    @field:NullableNotBlank(resourceName = "icon")
    @field:Length(max = 50, message = "icon must be at most 50 characters")
    val icon: String? = null,

    /**
     * 필드 속성 업데이트 목록
     * 최대 5개까지 허용
     */
    @field:Valid
    @field:Size(max = 5, message = "attributes must be at most 5")
    val attributes: Set<AttributeUpdateReqControllerDto> = emptySet(),
)