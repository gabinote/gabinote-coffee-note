package com.gabinote.coffeenote.common.dto.page.controller

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.gabinote.coffeenote.common.dto.sort.controller.SortResControllerDto

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PagedResControllerDto<T>(
    val content: MutableList<T>? = null,
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val sortKey: List<SortResControllerDto>?
)