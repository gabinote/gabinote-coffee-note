package com.gabinote.coffeenote.common.util.meiliSearch.helper

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.meilisearch.sdk.Index
import com.meilisearch.sdk.SearchRequest
import com.meilisearch.sdk.model.SearchResultPaginated
import com.meilisearch.sdk.model.Searchable
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable


private val logger = KotlinLogging.logger {}

object MeiliSearchResHelper {
    fun Searchable.facetMap(): Map<String, Map<String, Int>>? {
        return try {
            this.facetDistribution as Map<String, Map<String, Int>>
        } catch (e: Exception) {
            null
        }


    }

    fun Searchable.getFacetByName(facetName: String): List<FacetWithCount> {
        val facetMap = this.facetMap() ?: return emptyList()
        val nameFacets: Map<String, Int> = facetMap[facetName] ?: return emptyList()
        return nameFacets.map { (value, count) -> FacetWithCount(value, count) }
    }

    /**
     * facet 검색을 수행합니다.
     * @param query 검색어
     * @param filter 필터 조건 배열
     * @param facets 검색할 facet 이름 배열
     * @return 검색 결과(Searchable)
     */
    fun Index.searchFacet(
        query: String,
        filter: List<String>,
        facets: List<String>,
    ): Searchable {
        val req = SearchRequest(query).apply {
            this.filter = filter.toTypedArray()
            this.facets = facets.toTypedArray()
            limit = 0
        }


        val res = this.search(req)

        logger.debug { "Search result: $res" }

        return res
    }

    /**
     * 특정 이름의 facet 결과만 반환합니다.
     * @param query 검색어
     * @param filter 필터 조건 배열
     * @param facets 검색할 facet 이름 배열
     * @param facetName 반환할 facet 이름
     * @return facetName에 해당하는 facet 결과 리스트
     */
    fun Index.searchFacetWithName(
        query: String,
        filter: List<String>,
        facets: List<String>,
        facetName: String,
    ): List<FacetWithCount> {
        val searchable = this.searchFacet(
            query = query,
            filter = filter,
            facets = facets,
        )


        return searchable.getFacetByName(facetName)
    }

    fun Index.searchAsPage(
        searchRequest: SearchRequest,
        pageable: Pageable,
    ): SearchResultPaginated {
        searchRequest.apply {
            this.page = pageable.pageNumber + 1
            this.hitsPerPage = pageable.pageSize
        }

        val res = this.search(searchRequest) as SearchResultPaginated
        return res
    }


    fun String.escapeForMeili(): String {
        return this
            .replace("\\", "\\\\")
            .replace("'", "\\'")
    }

    /**

     */
    fun String.validString(): Boolean {

        if (this == "*") return true

        return true
    }

    /**
     * 입력값이 유효한지 검사합니다.
     * 유효하지 않은 값이 있을 경우 IllegalArgumentException을 던집니다.
     * @param inputs 검사할 입력값들
     * @throws IllegalArgumentException 유효하지 않은 값이 있을 경우
     */
    fun validationInput(vararg inputs: String) {
        inputs.forEach {
            if (!it.validString()) {

                // controller에서 처리 못한 예외이므로, IllegalArgumentException 던짐
                throw IllegalArgumentException("Input string does not match regex: $it")
            }
        }
    }

    fun filterText(key: String, value: String): String {
        return "'$key' = '${value.escapeForMeili()}'"
    }

    fun filterTextIn(key: String, values: List<String>): String {
        val escapedValues = values.joinToString(", ") { "'${it.escapeForMeili()}'" }
        return "'$key' IN [$escapedValues]"
    }

    // >=
    fun filterTextGte(key: String, value: String): String {
        return "'$key' >= '${value.escapeForMeili()}'"
    }

    fun filterTextLte(key: String, value: String): String {
        return "'$key' <= '${value.escapeForMeili()}'"
    }
}