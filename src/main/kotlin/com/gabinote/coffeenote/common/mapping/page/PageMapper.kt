package com.gabinote.coffeenote.common.mapping.page

import com.gabinote.coffeenote.common.dto.page.controller.PagedResControllerDto
import com.gabinote.coffeenote.common.dto.sort.controller.SortResControllerDto
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

/**
 * 페이지 매핑을 담당하는 컴포넌트
 * Page<T>를 PagedResControllerDto<T>로 변환하는 기능 제공
 */
@Component
class PageMapper {

    /**
     * Page<T>를 PagedResControllerDto<T>로 변환
     * @param T 페이지 내 항목의 타입
     * @param page 변환할 Page<T> 객체
     * @return 변환된 PagedResControllerDto<T> 객체
     */
    fun <T> toPagedResponse(page: Page<T>): PagedResControllerDto<T> {
        return PagedResControllerDto(
            content = page.content,
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,

            sortKey = page.sort.map {
                SortResControllerDto(
                    key = it.property,
                    direction = it.direction.name.lowercase()
                )
            }.toList()
        )
    }
}