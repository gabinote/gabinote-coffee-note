package com.gabinote.coffeenote.common.mapping.slice

import com.gabinote.coffeenote.common.dto.slice.controller.SlicedResControllerDto
import com.gabinote.coffeenote.common.dto.sort.controller.SortResControllerDto
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

/**
 * 슬라이스 매핑을 담당하는 컴포넌트
 * Slice<T>를 SlicedResControllerDto<T>로 변환하는 기능 제공
 */
@Component
class SliceMapper {

    /*
    * Slice<T>를 SlicedResControllerDto<T>로 변환
    * @param T 슬라이스 내 항목의 타입
    * @param slice 변환할 Slice<T> 객체
    * @return 변환된 SlicedResControllerDto<T> 객체
     */
    fun <T> toSlicedResponse(slice: Slice<T>): SlicedResControllerDto<T> {
        return SlicedResControllerDto(
            content = slice.content,
            page = slice.number,
            size = slice.size,
            isLast = slice.isLast,
            sortKey = slice.sort.map {
                SortResControllerDto(
                    key = it.property,
                    direction = it.direction.name.lowercase()
                )
            }.toList()
        )
    }
}