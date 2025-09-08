package com.gabinote.coffeenote.field.dto.attribute.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.util.validation.collection.CollectionElementLength
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length

/**
 * 속성 생성 요청을 위한 컨트롤러 계층 DTO
 * @author 황준서
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AttributeCreateReqControllerDto(
    /**
     * 속성 키
     * 빈 값이 아니어야 하며 최대 50자까지 허용
     */
    @field:NotBlank(message = "key must not be blank")
    @field:Length(max = 50, message = "key must be at most 50 characters")
    val key: String,

    /**
     * 속성 값 집합
     * 각 값은 최대 50자까지 허용되며 전체 집합은 최대 100개 요소까지 허용
     */
    @field:CollectionElementLength(length = 50, resourceName = "attribute value")
    @field:Size(max = 100, message = "value must be at most 100")
    val value: Set<String> = setOf()
)