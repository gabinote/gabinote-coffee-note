package com.gabinote.coffeenote.note.domain.noteIndex

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.meiliSearch.client.data.MatchesPosition
import com.gabinote.coffeenote.common.util.meiliSearch.helper.MeiliSearchResHelper.searchAsPage
import com.gabinote.coffeenote.common.util.meiliSearch.helper.MeiliSearchResHelper.validationInput
import com.gabinote.coffeenote.note.domain.noteIndex.vo.DateRangeFilter
import com.gabinote.coffeenote.note.domain.noteIndex.vo.QueriedNoteIndex
import com.meilisearch.sdk.Client
import com.meilisearch.sdk.Index
import com.meilisearch.sdk.SearchRequest
import com.meilisearch.sdk.model.TaskInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}


//TODO: 리팩토링 필요
@Repository
class NoteIndexRepository(
    private val meiliSearchClient: Client,
    private val objectMapper: ObjectMapper,
) {

    private val index = meiliSearchClient.index("noteIndex")

    private val globalHighlightPreTag = "<em>"
    private val globalHighlightPostTag = "</em>"
    private val globalAttributesToCrop = listOf("filters", "title")
    private val globalAttributeToHighlight = listOf("filters", "title")
    private val globalCropLength = 100
    private val globalShowMatchesPosition = true

    /**
     * 소유자와 검색어를 기반으로 NoteIndex를 검색합니다.
     * @param owner 노트 소유자
     * @param query 검색어
     * @param pageable 페이징 정보
     * @param highlightTag 하이라이트 태그 ex) em 으로 받으면, <em>검색어</em> 형태로 하이라이트 처리
     * @return 검색된 NoteIndex 리스트를 포함하는 Page 객체
     */
    fun searchNotes(
        owner: String,
        query: String,
        highlightTag: String,
        pageable: Pageable,
    ): Slice<NoteIndex> {
        validationInput(owner, query, highlightTag)

        val filter = listOf("owner = \"$owner\"")
        val req =
            setupSearchReq(
                query = query,
                filter = filter,
                highlightTag = highlightTag
            )
        logger.debug { "Search Request: $req" }
        return index.searchAndConvertToNoteIndex(searchRequest = req, pageable = pageable)
    }

    fun searchNotesWithFilter(
        owner: String,
        filters: Map<String, List<String>>,
        createdDateFilter: DateRangeFilter? = null,
        modifiedDateFilter: DateRangeFilter? = null,
        pageable: Pageable,
        highlightTag: String,
    ): Slice<NoteIndex> {
        validationInput(owner, highlightTag)

        // 입력 필터는 setupFilters 메서드에서 검증 처리
        val searchFilters = mutableListOf("owner = \"$owner\"")
        setupFilters(filters, searchFilters)
        createdDateFilter?.let {
            setupDateRangeFilter(
                dateRangeFilter = createdDateFilter,
                fieldName = "createdDate",
                searchFilters = searchFilters
            )
        }
        modifiedDateFilter?.let {
            setupDateRangeFilter(
                dateRangeFilter = modifiedDateFilter,
                fieldName = "modifiedDate",
                searchFilters = searchFilters
            )
        }
        val req =
            setupFilterSearchReq(
                filter = searchFilters,
            )
        logger.debug { "Search Request with filters: $req" }
        return index.searchWithFilterAndConvertToNoteIndex(
            searchRequest = req,
            filters = filters,
            highlightTag = highlightTag,
            pageable = pageable
        )
    }

    //TODO: 중복 로직 리팩토링
    fun save(noteIndex: NoteIndex): TaskInfo {
        val jsonString = objectMapper.writeValueAsString(noteIndex)
        return index.addDocuments(jsonString)
    }

    fun saveAll(noteIndexes: List<NoteIndex>): TaskInfo {
        val jsonString = objectMapper.writeValueAsString(noteIndexes)
        return index.addDocuments(jsonString)
    }

    fun delete(noteIndexUid: String): TaskInfo {
        return index.deleteDocument(noteIndexUid)
    }

    fun deleteAllByExternalId(externalId: String): TaskInfo {
        val filter = "externalId = \"$externalId\""
        return index.deleteDocumentsByFilter(filter)
    }

    fun deleteAllByOwner(owner: String): TaskInfo {
        val filter = "owner = \"$owner\""
        return index.deleteDocumentsByFilter(filter)
    }


    /**
     * NoteIndexRepository 전역에서 사용하는 SearchRequest 설정 메서드
     * @param query 검색어
     * @param filter 필터 조건 리스트
     * @return 설정된 SearchRequest 객체
     */
    private fun setupSearchReq(
        query: String,
        filter: List<String>? = null,
        highlightTag: String? = null,
    ): SearchRequest {
        val globalHighlightPreTag = highlightTag?.let { "<$it>" } ?: this.globalHighlightPreTag
        val globalHighlightPostTag = highlightTag?.let { "</$it>" } ?: this.globalHighlightPostTag
        return SearchRequest(query).apply {
            filter?.let {
                this.filter = it.toTypedArray()
            }
            this.attributesToCrop = globalAttributesToCrop.toTypedArray()
            this.attributesToHighlight = globalAttributeToHighlight.toTypedArray()
            this.cropLength = globalCropLength
            this.highlightPreTag = globalHighlightPreTag
            this.highlightPostTag = globalHighlightPostTag
            this.showMatchesPosition = globalShowMatchesPosition
        }
    }

    private fun setupFilterSearchReq(
        filter: List<String>? = null,
    ): SearchRequest {
        return SearchRequest("*").apply {
            filter?.let {
                this.filter = it.toTypedArray()
            }
        }
    }

    /**
     * MeiliSearch의 Index에서 검색을 수행하고 NoteIndex 리스트로 변환하여 Page 형태로 반환합니다.
     * @param searchRequest MeiliSearch에 전달할 검색 요청 객체
     * @return NoteIndex 리스트를 포함하는 Page 객체
     */
    private fun Index.searchAndConvertToNoteIndex(searchRequest: SearchRequest, pageable: Pageable): Slice<NoteIndex> {
        val res = this.searchAsPage(searchRequest = searchRequest, pageable = pageable)
        logger.debug { "Search Response: $res" }
        val noteIndexes = convertToNoteIndexes(res.hits)
        return SliceImpl(
            noteIndexes,
            PageRequest.of(
                searchRequest.page - 1,
                searchRequest.hitsPerPage
            ),
            res.totalPages > searchRequest.page
        )
    }

    private fun Index.searchWithFilterAndConvertToNoteIndex(
        searchRequest: SearchRequest,
        filters: Map<String, List<String>>,
        highlightTag: String,
        pageable: Pageable,
    ): Slice<NoteIndex> {
        val res = this.searchAsPage(searchRequest = searchRequest, pageable = pageable)
        logger.debug { "Search Response: $res" }
        val noteIndexes = convertToNoteIndexes(
            res.hits,
            filters = filters,
            highlightTag = highlightTag
        )
        return SliceImpl(
            noteIndexes,
            PageRequest.of(
                searchRequest.page - 1,
                searchRequest.hitsPerPage
            ),
            res.totalPages > searchRequest.page
        )
    }

    /**
     * MeiliSearch의 검색 결과 hit 리스트를 NoteIndex 리스트로 변환합니다.
     * @param hit MeiliSearch에서 반환된 검색 결과 hit 리스트
     * @return NoteIndex 리스트
     */
    private fun convertToNoteIndexes(hit: List<Map<String, Any>>): List<NoteIndex> {
        val noteIndexes = mutableListOf<NoteIndex>()
        for (item in hit) {
            val queriedNoteIndex = objectMapper.convertValue(item, QueriedNoteIndex::class.java)
            val formatted = queriedNoteIndex.formatted
                ?: throw IllegalArgumentException("Cannot convert $item to a note index")
            filteringMatchedAttributes(
                matches = queriedNoteIndex.matchesPosition,
                origin = formatted
            )
            noteIndexes.add(formatted)
        }
        return noteIndexes
    }

    private fun convertToNoteIndexes(
        hit: List<Map<String, Any>>,
        filters: Map<String, List<String>>,
        highlightTag: String,
    ): List<NoteIndex> {
        val noteIndexes = mutableListOf<NoteIndex>()
        for (item in hit) {
            val noteIndex = objectMapper.convertValue(item, NoteIndex::class.java)
            filteringWithUserFilter(
                origin = noteIndex,
                filters = filters,
                highlightTag = highlightTag
            )
            noteIndexes.add(noteIndex)
        }
        return noteIndexes
    }

    /**
     * filters 내부 요소중 오직 매칭된 값들만 필터링하여 원본 NoteIndex의 filters에 반영합니다.
     * @param matches MeiliSearch에서 반환된 매치된 필드와 위치 정보 맵
     * @param origin 원본 NoteIndex 객체
     */
    private fun filteringMatchedAttributes(matches: Map<String, List<MatchesPosition>>, origin: NoteIndex) {
        val filtered = buildFilteredMatches(matches, origin)
        origin.changeFilters(filtered)
    }

    private fun filteringWithUserFilter(
        origin: NoteIndex,
        filters: Map<String, List<String>>,
        highlightTag: String,
    ) {
        val filtered = mutableMapOf<String, List<String>>()
        for ((key, values) in filters) {
            val originValues = origin.filters[key]
            if (originValues != null) {
                val matchedValues = originValues.intersect(values.toSet()).toList()
                val filteredValue = originValues.map {
                    if (matchedValues.contains(it)) {
                        return@map "<$highlightTag>$it</$highlightTag>"
                    }

                    return@map it
                }

                filtered[key] = filteredValue
            }
        }
        origin.changeFilters(filtered)
    }

    /**
     * 매치된 필드와 원본 NoteIndex의 filters를 기반으로 필터링된 맵을 생성합니다.
     * @param matches MeiliSearch에서 반환된 매치된 필드와 위치 정보 맵
     * @param originFilters 원본 NoteIndex의 filters 맵
     * @return 필터링된 맵
     * @throws IllegalArgumentException 지원되지 않는 matchKey가 있는 경우 발생
     */
    private fun buildFilteredMatches(
        matches: Map<String, List<MatchesPosition>>,
        origin: NoteIndex,
    ): Map<String, List<String>> {
        val originFilters = origin.filters
        val filtered = mutableMapOf<String, List<String>>()
        for (matchKey in matches.keys) {
            if (matchKey.startsWith("filters.")) {
                insertToFiltered(matchKey, originFilters, filtered)
            }
        }
        return filtered
    }


    /**
     * filters.XXX 형태의 matchKey를 받아서 originFilters에서 해당 필드의 값을 찾아 filtered에 삽입합니다.
     * @param matchKey MeiliSearch에서 반환된 매치된 필드 이름 (예: "filters.color")
     * @param originFilters 원본 NoteIndex의 filters 맵
     * @param filtered 필터링된 결과를 저장할 맵
     * @throws IllegalArgumentException originFilters에 해당 필드가 없는 경우 발생
     */
    private fun insertToFiltered(
        matchKey: String,
        originFilters: Map<String, List<String>>,
        filtered: MutableMap<String, List<String>>,
    ) {
        val fieldName = matchKey.removePrefix("filters.")
        val matchedValues = originFilters[fieldName]
        if (matchedValues != null) {
            filtered[fieldName] = matchedValues
        } else {
            // origin의 filters에 해당 필드가 없는 경우 예외처리
            throw IllegalArgumentException("Field $fieldName not found in origin filters")
        }
    }


    /**
     * 필터 맵에 대한 입력값 검증 후, MeiliSearch에서 요구하는 형식의 필터 문자열 리스트로 변환합니다.
     * @param filters 필터 맵
     * @param searchFilters 변환된 필터 문자열을 추가할 리스트
     */
    private fun setupFilters(
        filters: Map<String, List<String>>,
        searchFilters: MutableList<String>,
    ) {
        for ((key, values) in filters) {
            validationInput(key, *values.toTypedArray())
            val valueFilters = "filters.$key IN [${values.joinToString(",") { "\"$it\"" }}]"
            searchFilters.add(valueFilters)
        }
    }

    private fun setupDateRangeFilter(
        dateRangeFilter: DateRangeFilter,
        fieldName: String,
        searchFilters: MutableList<String>,
    ) {
        val rangeConditions = mutableListOf<String>()
        dateRangeFilter.startDate?.let {
            rangeConditions.add("$fieldName >= $it")
        }
        dateRangeFilter.endDate?.let {
            rangeConditions.add("$fieldName <= $it")
        }
        if (rangeConditions.isNotEmpty()) {
            val rangeFilter = rangeConditions.joinToString(" AND ")
            searchFilters.add("($rangeFilter)")
        }
    }


}

