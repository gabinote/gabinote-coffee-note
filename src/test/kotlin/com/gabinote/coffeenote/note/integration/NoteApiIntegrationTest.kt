package com.gabinote.coffeenote.note.integration

import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import io.kotest.assertions.nondeterministic.eventually
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import kotlin.time.Duration.Companion.seconds

class NoteApiIntegrationTest : IntegrationTestTemplate() {

    override val apiPrefix: String = "/api/v1"

    private fun useNoteIndex() {
        testMeiliSearchHelper.insertIndex("testsets/note/integration/note-index.json")
    }

    private fun useNoteFieldsIndex() {
        testMeiliSearchHelper.insertIndex("testsets/note/integration/note-field-index.json")
    }

    private fun useIndexes() {
        testMeiliSearchHelper.insertIndex(
            "testsets/note/integration/note-index.json",
            "testsets/note/integration/note-field-index.json"
        )
    }


    init {


        beforeTest {
            testDebeziumHelper.deleteAllConnectors()
            testKafkaHelper.deleteAllTopics()
        }

        afterTest {
            testDebeziumHelper.deleteAllConnectors()
            testKafkaHelper.deleteAllTopics()
        }

        feature("[Note] Note API Integration Test") {

            feature("[GET] /api/v1/notes/me") {
                scenario("내 노트 목록을 요청하면, 요청자가 소유한 노트만 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/note/integration/get-api-v1-notes-me.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/notes/me")
                    }.Then {
                        statusCode(200)
                        body("content.size()", equalTo(1))
                        body("content[0].external_id", equalTo("b2c3d4e5-f6a7-4890-8345-678901bcdef0"))
                        body("content[0].owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))

                    }
                }
            }

            feature("[GET] /api/v1/note/me/{externalId}") {
                scenario("내 노트를 외부 ID로 조회하면, 해당 노트를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/note/integration/get-api-v1-note-me-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/note/me/c3d4e5f6-a7b8-4901-8456-789012cdef01")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("c3d4e5f6-a7b8-4901-8456-789012cdef01"))
                        body("title", equalTo("Detailed Coffee Note"))
                        body("thumbnail", equalTo("c1d2e3f4-a5b6-4789-bcde-f012345678ab.jpg"))
                        body("is_open", equalTo(true))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("fields.size()", equalTo(2))
                        body("fields[0].name", equalTo("Coffee Name"))
                        body("fields[0].values[0]", equalTo("Colombian Supremo"))
                        body("display_fields.size()", equalTo(2))
                        body("display_fields[0].name", equalTo("Coffee Name"))
                        body("display_fields[0].values[0]", equalTo("Colombian Supremo"))
                    }
                }
            }

            feature("[GET] /api/v1/notes/me/search") {
                scenario("검색 조건으로 내 노트를 검색하면, 조건에 맞는 노트를 반환해야 한다.") {
                    Given {
                        useNoteIndex()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/get-api-v1-notes-me-search.json")

                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        queryParam("query", "Ethio")
                        queryParam("highlightTag", "em")
                    }.When {
                        get("/notes/me/search")
                    }.Then {
                        statusCode(200)
                        body("content.size()", greaterThanOrEqualTo(1))
                        //Origin에 Ethiopia가 포함
                        body("content[0].id", equalTo("11111111-1111-4111-8111-111111111111"))

                    }
                }
            }

            feature("[GET] /api/v1/notes/me/filter") {
                scenario("필터 조건으로 내 노트를 필터링하면, 조건에 맞는 노트를 반환해야 한다.") {
                    Given {
                        useNoteIndex()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/get-api-v1-notes-me-filter.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        queryParam("fieldOptions[Origin]", "Ethiopia")
                        queryParam("fieldOptions[Roast Level]", "Medium")
                        queryParam("highlightTag", "em")
                    }.When {
                        get("/notes/me/filter")
                    }.Then {
                        statusCode(200)
                        body("content.size()", greaterThanOrEqualTo(1))
                    }
                }
            }

            feature("[GET] /api/v1/notes/me/facets/fields/search") {
                scenario("필드 이름으로 검색하면, 해당하는 필드 패싯을 반환해야 한다.") {
                    Given {
                        useNoteFieldsIndex()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/get-api-v1-notes-me-facets-fields-search.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        queryParam("query", "Coffee")
                    }.When {
                        get("/notes/me/facets/fields/search")
                    }.Then {
                        statusCode(200)
                        body("facets.size()", greaterThanOrEqualTo(1))
                    }
                }
            }

            feature("[GET] /api/v1/notes/me/facets/fields/{fieldName}/values/search") {
                scenario("필드 값으로 검색하면, 해당하는 필드 값 패싯을 반환해야 한다.") {
                    Given {
                        useNoteFieldsIndex()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/get-api-v1-notes-me-facets-fields-fieldName-values-search.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        queryParam("query", "Ethiopia")
                    }.When {
                        get("/notes/me/facets/fields/Origin/values/search")
                    }.Then {
                        statusCode(200)
                        body("field_name", equalTo("Origin"))
                        body("facets.size()", greaterThanOrEqualTo(1))
                    }
                }
            }

            feature("[GET] /api/v1/notes/me/facets/fields/values/search") {
                scenario("모든 필드 값으로 검색하면, 요청자 소유의 모든 필드 값 패싯을 반환해야 한다.") {
                    Given {
                        useNoteFieldsIndex()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/get-api-v1-notes-me-facets-fields-values-search.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        queryParam("query", "Ethiopia")
                    }.When {
                        get("/notes/me/facets/fields/values/search")
                    }.Then {
                        statusCode(200)
                        body("facets.size()", equalTo(3))
                    }
                }
            }

            feature("[POST] /api/v1/note/me") {
                scenario("올바른 노트 생성 요청이 주어지면, 새로운 노트를 생성하여 반환해야 한다.") {
                    Given {

                        testDataHelper.enablePreImages("notes")
                        testDataHelper.setData(
                            "/testsets/note/integration/post-api-v1-note-me-before.json"
                        )
                        testDataHelper.setData(
                            "/testsets/note/integration/policy.json"
                        )

                        useIndexes()

                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "title" to "New Coffee Note"
                                "thumbnail" to "ee60b567-ba13-4414-908a-6d901ae3892f.jpg"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000001"
                                        "name" to "Coffee Name"
                                        "icon" to "coffee"
                                        "type" to "SHORT_TEXT"
                                        "order" to 0
                                        "is_display" to true
                                        "attributes" arr {}
                                        "values" arr {
                                            +"New Coffee"
                                        }
                                    }
                                }
                            }
                        )
                    }.When {
                        post("/note/me")
                    }.Then {
                        statusCode(201)
                        testDataHelper.assertData("/testsets/note/integration/post-api-v1-note-me-after.json")
                        body("title", equalTo("New Coffee Note"))
                        body("thumbnail", equalTo("ee60b567-ba13-4414-908a-6d901ae3892f.jpg"))
                        body("is_open", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("external_id", equalTo("00000000-0000-0000-0000-000000000000"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Coffee Name"))
                        body("fields[0].values[0]", equalTo("New Coffee"))
                    }

                    eventually(15.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/integration/post-api-v1-note-me-index-after.json",
                            verbose = false
                        )
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/integration/post-api-v1-note-me-field-index-after.json",
                            verbose = false
                        )
                    }
                }
            }

            feature("[PUT] /api/v1/note/me/{externalId}") {
                scenario("올바른 노트 수정 요청이 주어지면, 해당 노트를 수정하여 반환해야 한다.") {
                    Given {

                        testDataHelper.setData("/testsets/note/integration/put-api-v1-note-me-before.json")

                        testDataHelper.enablePreImages("notes")
                        useIndexes()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/put-api-v1-note-me-index-before.json")
                        testMeiliSearchHelper.insertData("/testsets/note/integration/put-api-v1-note-me-field-index-before.json")

                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                        contentType("application/json")
                        body(
                            jsonBuilder {
                                "title" to "Updated Note Title"
                                "thumbnail" to "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to "00000000-0000-0000-0000-000000000002"
                                        "name" to "Updated Field"
                                        "icon" to "edit"
                                        "type" to "SHORT_TEXT"
                                        "order" to 0
                                        "is_display" to true
                                        "attributes" arr {}
                                        "values" arr {
                                            +"Updated Value"
                                        }
                                    }
                                }
                            }
                        )
                    }.When {
                        put("/note/me/e5f6a7b8-c9d0-4123-8678-901234ef0123")
                    }.Then {
                        statusCode(200)
                        testDataHelper.assertData("/testsets/note/integration/put-api-v1-note-me-after.json")
                        body("title", equalTo("Updated Note Title"))
                        body("thumbnail", equalTo("a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"))
                        body("is_open", equalTo(false))
                        body("owner", equalTo("d15cdbf8-22bc-47e2-9e9a-4d171cb6522e"))
                        body("external_id", equalTo("e5f6a7b8-c9d0-4123-8678-901234ef0123"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Updated Field"))
                        body("fields[0].values[0]", equalTo("Updated Value"))
                    }

                    eventually(15.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/integration/put-api-v1-note-me-index-after.json",
                            verbose = false
                        )
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/integration/put-api-v1-note-me-field-index-after.json",
                            verbose = false
                        )
                    }
                }
            }

            feature("[DELETE] /api/v1/note/me/{externalId}") {
                scenario("올바른 자신의 노트 외부 ID를 제공하면, 해당 노트를 삭제한다.") {
                    Given {

                        useIndexes()
                        testMeiliSearchHelper.insertData("/testsets/note/integration/delete-api-v1-note-me-index-before.json")
                        testMeiliSearchHelper.insertData("/testsets/note/integration/delete-api-v1-note-me-field-index-before.json")
                        testDataHelper.setData("/testsets/note/integration/delete-api-v1-note-me-before.json")

                        testDataHelper.enablePreImages("notes")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        delete("/note/me/f6a7b8c9-d0e1-4234-8789-012345f01234")
                    }.Then {
                        statusCode(204)
                        testDataHelper.assertData("/testsets/note/integration/delete-api-v1-note-me-after.json")
                    }

                    eventually(15.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/integration/delete-api-v1-note-me-index-after.json",
                            verbose = false
                        )
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/integration/delete-api-v1-note-me-field-index-after.json",
                            verbose = false
                        )
                    }
                }
            }

            feature("[GET] /api/v1/note/open/{externalId}") {
                scenario("공개 노트를 외부 ID로 조회하면, 해당 공개 노트를 반환해야 한다.") {
                    Given {
                        testDataHelper.setData("/testsets/note/integration/get-api-v1-note-open-ext.json")
                        basePath(apiPrefix)
                        accept("application/json")
                        header("X-Token-Sub", "d15cdbf8-22bc-47e2-9e9a-4d171cb6522e")
                        header("X-Token-Roles", "user")
                    }.When {
                        get("/note/open/d4e5f6a7-b8c9-4012-8567-890123def012")
                    }.Then {
                        statusCode(200)
                        body("external_id", equalTo("d4e5f6a7-b8c9-4012-8567-890123def012"))
                        body("title", equalTo("Open Coffee Note"))
                        body("thumbnail", equalTo("d1e2f3a4-b5c6-4789-9012-3456789abcde.jpg"))
                        body("is_open", equalTo(true))
                        body("owner", equalTo("695b8482-c259-4493-82e2-8988599d21e9"))
                        body("fields.size()", equalTo(1))
                        body("fields[0].name", equalTo("Coffee Type"))
                        body("fields[0].values[0]", equalTo("Arabica"))
                    }
                }
            }
        }
    }
}