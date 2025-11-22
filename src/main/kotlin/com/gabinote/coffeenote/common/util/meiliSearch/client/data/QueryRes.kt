package com.gabinote.coffeenote.common.util.meiliSearch.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount

@JsonIgnoreProperties(ignoreUnknown = true)
data class QueryRes(

    val hits: List<Object>?,

    /** 건너뛴 문서의 수 */
    val offset: Int?,

    /** 가져올 문서의 수 */
    val limit: Int?,

    /** 추정된 총 매치 수 (Long 타입이 안전합니다) */
    val estimatedTotalHits: Long?,

    /** 정확한 총 매치 수 (Long 타입이 안전합니다) */
    val totalHits: Long?,

    /** (AI 검색 전용) 시맨틱 검색 매치 수 (Nullable) */
    val semanticHitCount: Long?,

    /** 전체 페이지 수 */
    val totalPages: Int?,

    /** 페이지 당 결과 수 */
    val hitsPerPage: Int?,

    /** 현재 페이지 번호 */
    val page: Int?,

    /**
     * 패싯 분포 (예: "genre": { "action": 10, "comedy": 5 })
     * Map<String, Map<String, Int>> 형태입니다.
     */
    val facetDistribution: Map<String, Map<String, Int>>?,

    /**
     * 패싯 통계 (예: "price": { "min": 10.0, "max": 100.0 })
     * 별도의 헬퍼 클래스 MeiliFacetStat를 사용합니다.
     */
    val facetStats: Map<String, FacetStat>?,

    /** 쿼리 처리 시간 (ms) */
    val processingTimeMs: Int?,

    /** 원본 쿼리 문자열 */
    val query: String?,

    /** 검색 요청을 식별하는 UUID v7 */
    val requestUid: String?
) {
    fun getFacetByName(facetName: String): List<FacetWithCount> {
        val facetMap = this.facetDistribution ?: return emptyList()
        val nameFacets: Map<String, Int> = facetMap[facetName] ?: return emptyList()
        return nameFacets.map { (value, count) -> FacetWithCount(value, count) }
    }
}