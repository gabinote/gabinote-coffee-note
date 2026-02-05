package com.gabinote.coffeenote.note.domain.noteFieldIndex

import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.meiliSearch.helper.MeiliSearchResHelper.filterText
import com.gabinote.coffeenote.common.util.meiliSearch.helper.MeiliSearchResHelper.filterTextIn
import com.gabinote.coffeenote.common.util.meiliSearch.helper.MeiliSearchResHelper.searchFacetWithName
import com.gabinote.coffeenote.common.util.meiliSearch.helper.MeiliSearchResHelper.validationInput
import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.note.dto.noteFieldIndex.vo.NoteFieldIndexNoteIdHash
import com.meilisearch.sdk.Client
import com.meilisearch.sdk.SearchRequest
import com.meilisearch.sdk.model.TaskInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

@Repository
class NoteFieldIndexRepository(
    private val meiliSearchClient: Client,
    private val objectMapper: ObjectMapper,
) {

    private val index = meiliSearchClient.index("noteFieldIndex")

    fun searchFieldNameFacets(
        owner: String,
        query: String,
    ): List<FacetWithCount> {
        validationInput(owner, query)

        val filter = listOf(
            filterText("owner", owner)
        )
        val facets = listOf("name")
        return index.searchFacetWithName(
            query = query,
            filter = filter,
            facets = facets,
            facetName = "name"
        )
    }

    fun searchFieldValueFacets(
        owner: String,
        fieldName: String,
        query: String,
    ): List<FacetWithCount> {
        validationInput(owner, fieldName, query)
        val filter = listOf(
            filterText("owner", owner),
            filterText("name", fieldName)
        )
        val facets = listOf("value")

        return index.searchFacetWithName(
            query = query,
            filter = filter,
            facets = facets,
            facetName = "value"
        )
    }

    fun findAllByNoteIds(noteIds: List<String>): List<NoteFieldIndexNoteIdHash> {
        val filter = listOf(
            filterTextIn("noteId", noteIds)
        )
        val attributesToRetrieve = listOf("id", "noteId", "name", "value", "fieldId")
        val req = SearchRequest("*").apply {
            this.filter = filter.toTypedArray()
            this.attributesToRetrieve = attributesToRetrieve.toTypedArray()
        }
        val res = index.search(req)
        return convertToNoteFieldNoteIdHash(res.hits)
    }

    //TODO: 중복 로직 리팩토링
    fun save(noteFieldIndex: NoteFieldIndex): TaskInfo {
        val jsonString = objectMapper.writeValueAsString(noteFieldIndex)
        return index.addDocuments(jsonString)
    }

    fun saveAll(noteFieldIndexes: List<NoteFieldIndex>): TaskInfo {
        val jsonString = objectMapper.writeValueAsString(noteFieldIndexes)
        return index.addDocuments(jsonString)
    }

    fun delete(noteFieldIndexUid: String): TaskInfo {
        return index.deleteDocument(noteFieldIndexUid)
    }

    fun deleteAllByNoteId(noteId: String): TaskInfo {
        val filter = filterText("noteId", noteId)
        return index.deleteDocumentsByFilter(filter)
    }

    fun deleteAllByOwner(owner: String): TaskInfo {
        val filter = filterText("owner", owner)
        return index.deleteDocumentsByFilter(filter)
    }

    private fun convertToNoteFieldNoteIdHash(hit: List<Map<String, Any>>): List<NoteFieldIndexNoteIdHash> {
        return hit.map {
            objectMapper.convertValue(it, NoteFieldIndexNoteIdHash::class.java)
        }
    }


}