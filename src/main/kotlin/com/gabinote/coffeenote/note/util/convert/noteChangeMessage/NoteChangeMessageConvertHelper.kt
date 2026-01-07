package com.gabinote.coffeenote.note.util.convert.noteChangeMessage

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.debezium.json.parser.MongoDateParser
import com.gabinote.coffeenote.note.domain.note.Note
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Debezium Change Message의 Note JSON을 Note 도메인으로 변환하는 헬퍼 클래스
 */
@Component
class NoteChangeMessageConvertHelper(
    private val mongoDateParser: MongoDateParser,
    private val objectMapper: ObjectMapper,
) {
    /**
     * Change Message의 JSON 문자열을 Note 도메인으로 변환
     */
    fun parseFromChangeMessageJson(
        jsonString: String,
    ): Note {
        val tmpNote = objectMapper.readValue(jsonString, TmpChangeMessageNote::class.java)
        val node = objectMapper.readTree(jsonString)
        val createdDate = parseDateTime(node, "createdDate")
        val modifiedDate = parseDateTime(node, "modifiedDate")
        return parseToNote(
            tmp = tmpNote,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
        )
    }

    /**
     * 임시 Note 객체를 실제 Note 도메인으로 변환
     */
    private fun parseToNote(
        tmp: TmpChangeMessageNote,
        createdDate: LocalDateTime,
        modifiedDate: LocalDateTime,
    ): Note {
        return Note(
            id = tmp.id,
            owner = tmp.owner,
            title = tmp.title,
            externalId = tmp.externalId,
            thumbnail = tmp.thumbnail,
            fields = tmp.fields,
            displayFields = tmp.displayFields,
            isOpen = tmp.isOpen,
            hash = tmp.hash,
            createdDate = createdDate,
            modifiedDate = modifiedDate,
        )

    }

    /**
     * JSON 노드에서 날짜/시간 필드를 파싱
     */
    private fun parseDateTime(
        node: JsonNode,
        fieldName: String,
    ): LocalDateTime {
        val dateNode = node.get(fieldName) ?: throw IllegalArgumentException("$fieldName field does not exist")
        return mongoDateParser.parseDateTime(dateNode)
    }

}