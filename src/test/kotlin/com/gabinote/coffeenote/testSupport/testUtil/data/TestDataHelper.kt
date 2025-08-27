package com.gabinote.coffeenote.testSupport.testUtil.data


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.boot.test.context.TestComponent
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@TestComponent
class TestDataHelper(
    private val mongoTemplate: MongoTemplate,
    private val objectMapper: ObjectMapper
){
    fun setData(jsonFile: String) {
        val input = ClassPathResource(jsonFile).inputStream
        val root = objectMapper.readTree(input)

        root.properties().forEach { (collection, nodes) ->
            mongoTemplate.dropCollection(collection)

            nodes.forEach { doc ->
                val map = toMongoMap(doc)
                mongoTemplate.insert(map, collection)
            }
        }
    }

    private fun toMongoMap(node: JsonNode): Map<String, Any?> {
        val map = objectMapper.convertValue(node, Map::class.java) as MutableMap<String, Any?>

        // _id 변환 처리
        val idNode = node.get("_id")
        if (idNode != null && idNode.has("\$oid")) {
            map["_id"] = ObjectId(idNode["\$oid"].asText())
        }

        return map
    }

    fun assertData(expectedJsonFile: String) {
        val input = ClassPathResource(expectedJsonFile).inputStream
        val expected = objectMapper.readTree(input)

        expected.properties().forEach { (collection, expectedDocs) ->
            val actualDocs = mongoTemplate.findAll(Map::class.java, collection)
            val actualTree: List<JsonNode> = objectMapper.convertValue(
                actualDocs,
                object : TypeReference<List<JsonNode>>() {}
            )

            if (expectedDocs.size() != actualTree.size) {
                throw AssertionError("Collection '$collection' size mismatch: expected=${expectedDocs.size()}, actual=${actualTree.size}")
            }

            expectedDocs.zip(actualTree).forEachIndexed { idx, (expectedDoc, actualDoc) ->
                if (!matchNode(expectedDoc, actualDoc)) {
                    throw AssertionError(
                        "Mismatch in collection '$collection' at index $idx\n" +
                                "Expected: $expectedDoc\n" +
                                "Actual:   $actualDoc"
                    )
                }
            }
        }
    }

    private fun matchNode(expected: JsonNode, actual: JsonNode): Boolean {
        return when {
            // 객체라면 필드별로 재귀 검사
            expected.isObject -> {
                expected.properties().all { (field, expVal) ->
                    val actVal = actual.get(field)
                    actVal != null && matchNode(expVal, actVal)
                }
            }
            // 배열이면 각 원소 비교
            expected.isArray -> {
                expected.zip(actual).all { (exp, act) -> matchNode(exp, act) }
            }
            // 패턴 문자열 검사
            expected.isTextual -> matchPattern(expected.asText(), actual)
            else -> expected == actual
        }
    }

    private fun matchPattern(expected: String, actual: JsonNode): Boolean {
        return when {
            expected == "\$anyObject()" -> !actual.isNull
            expected.startsWith("\$anyObject(") -> {
                val size = expected.removePrefix("\$anyObject(").removeSuffix(")").toInt()
                actual.isObject && actual.size() == size
            }
            expected == "\$anyString()" -> actual.isTextual
            expected.startsWith("\$anyString(/") -> {
                val regex = expected.removePrefix("\$anyString(/").removeSuffix("/)").toRegex()
                actual.isTextual && regex.matches(actual.asText())
            }
            else -> expected == actual.asText()
        }
    }
}