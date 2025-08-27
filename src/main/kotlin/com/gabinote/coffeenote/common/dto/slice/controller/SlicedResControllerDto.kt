package com.gabinote.coffeenote.common.dto.slice.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.dto.sort.controller.SortResControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SlicedResControllerDto<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val isLast: Boolean,
    val sortKey: List<SortResControllerDto>?
)