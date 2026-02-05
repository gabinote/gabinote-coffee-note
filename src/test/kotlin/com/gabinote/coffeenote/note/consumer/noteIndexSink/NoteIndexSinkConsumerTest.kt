package com.gabinote.coffeenote.note.consumer.noteIndexSink

import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation
import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.note.NoteStatus
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_CHANGE_TOPIC
import com.gabinote.coffeenote.note.event.noteCreated.NoteCreateEventHelper.NOTE_CHANGE_TOPIC_DLT
import com.gabinote.coffeenote.testSupport.testConfig.meiliSearch.MeiliSearchContainerInitializer
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.debezium.TestDebeziumDataHelper.createChangeMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


private val logger = KotlinLogging.logger {}

class NoteIndexSinkConsumerTest : IntegrationTestTemplate() {
    private val baseData = "/testsets/note/sink/"


    init {
        beforeTest {
            testDebeziumHelper.deleteAllConnectors()
            testKafkaHelper.deleteAllTopics()
        }

        afterTest {
            testDebeziumHelper.deleteAllConnectors()
            testKafkaHelper.deleteAllTopics()
        }

        feature("[Note] NoteIndexSinkConsumer 테스트") {

            feature("NoteIndexSinkConsumer.sinkNoteIndex") {
                feature("Create operation 테스트") {
                    scenario("Note 가 새롭게 생성되어 $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 해당 Note 의 색인 정보가 생성되어야 한다.") {
                        testDataHelper.setData("${baseData}/noteIndex/new-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteIndex/note-index.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        // 새롭게 생성된 Note
                        val newNoteId = "66a7b2a60e0a514d2a1c0002"
                        val newNoteNode = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", "66a7b2a60e0a514d2a1c0002")
                            put("externalId", "b2c3d4e5-f6a7-4890-1234-567890abcdef")
                            put("title", "핸드드립 레시피: 케냐 AA")
                            putNull("thumbnail")
                            putObject("createdDate").put("\$date", "2024-07-28T15:30:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T11:05:20Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                addObject().apply {
                                    put("_id", "field_003")
                                    put("name", "추출 온도")
                                    put("icon", "temp")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("92")
                                }
                                addObject().apply {
                                    put("_id", "field_004")
                                    put("name", "총 추출 시간")
                                    put("icon", "clock")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 2)
                                    put("isDisplay", false)
                                    putArray("values").add("03:15")
                                }
                                addObject().apply {
                                    put("_id", "field_005")
                                    put("name", "메모")
                                    put("icon", "clock")
                                    put("type", "LONG_TEXT")
                                    putArray("attributes")
                                    put("order", 3)
                                    put("isDisplay", false)
                                    putArray("values").add("밸런스가 좋고 과일 향이 풍부함")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "추출 온도")
                                    put("icon", "temp")
                                    putArray("values").add("92")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", false)
                            put("owner", "user_beta_5678")
                            put("hash", "cf9c1ef54fd2af552cca2e4f443d1d7f")
                        }
                        // 해당 노트 생성 메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = null,
                            after = newNoteNode.toString(),
                            op = DebeziumOperation.CREATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = newNoteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 색인 정보가 생성되었는지 검증

                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteIndex/new-note-index-after.json",
                                verbose = false
                            )
                        }
                    }
                }

                feature("Update operation 테스트") {
                    scenario("Note가 수정되어 $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 해당 Note의 색인 정보가 업데이트되어야 한다.") {

                        // 그냥 데이터베이스에 update 되어있는 노트가 들어있다고 가정함.
                        // 전체 프로세스는 api 통합테스트에서 검증함.
                        testDataHelper.setData("${baseData}/noteIndex/update-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteIndex/note-index.json")

                        // 여기에는 변경 전 다른 값의 노트가 들어있음.
                        testMeiliSearchHelper.insertData("${baseData}/noteIndex/update-note-index.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        // 기존에 존재하는 Note
                        val baseNodeId = "66a7b2a60e0a514d2a1c0002"
                        val baseNoteNode = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", baseNodeId)
                            put("externalId", "b2c3d4e5-f6a7-4890-1234-567890abcdef")
                            put("title", "핸드드립 레시피: 케냐 AA")
                            putNull("thumbnail")
                            putObject("createdDate").put("\$date", "2024-07-28T15:30:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T11:05:20Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                addObject().apply {
                                    put("_id", "field_003")
                                    put("name", "추출 온도")
                                    put("icon", "temp")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("100")
                                }
                                addObject().apply {
                                    put("_id", "field_004")
                                    put("name", "총 추출 시간")
                                    put("icon", "clock")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 2)
                                    put("isDisplay", false)
                                    putArray("values").add("03:20")
                                }
                                addObject().apply {
                                    put("_id", "field_005")
                                    put("name", "메모")
                                    put("icon", "clock")
                                    put("type", "LONG_TEXT")
                                    putArray("attributes")
                                    put("order", 3)
                                    put("isDisplay", false)
                                    putArray("values").add("밸런스가 좋고 과일 향이 풍부함 !!")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "추출 온도123")
                                    put("icon", "temp")
                                    putArray("values").add("100")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", false)
                            put("owner", "user_beta_5678")
                        }

                        val existingNote = baseNoteNode.deepCopy().apply {
                            put("hash", "290ec684c1d1b32ea48603ca54f32dd2")
                        }

                        val updatedNote = baseNoteNode.deepCopy().apply {
                            put("hash", "Updated content")
                        }
                        // 해당 노트 수정 메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = existingNote.toString(),
                            after = updatedNote.toString(),
                            op = DebeziumOperation.UPDATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = baseNodeId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 색인 정보가 업데이트되었는지 검증
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteIndex/update-note-index-after.json",
                                verbose = false
                            )
                        }
                    }

                    scenario("Note가 수정되었지만, Hash가 변경된 데이터 변경이 아닐때, $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 무시되어야한다.") {

                        testDataHelper.setData("${baseData}/noteIndex/update-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteIndex/note-index.json")

                        testMeiliSearchHelper.insertData("${baseData}/noteIndex/update-note-index.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        // 기존에 존재하는 Note
                        val noteId = "66a7b2a60e0a514d2a1c0002"
                        val sameNoteNode = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", "66a7b2a60e0a514d2a1c0002")
                            put("externalId", "b2c3d4e5-f6a7-4890-1234-567890abcdef")
                            put("title", "핸드드립 레시피: 케냐 AA")
                            putNull("thumbnail")
                            putObject("createdDate").put("\$date", "2024-07-28T15:30:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T11:05:20Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                addObject().apply {
                                    put("_id", "field_003")
                                    put("name", "추출 온도")
                                    put("icon", "temp")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("100")
                                }
                                addObject().apply {
                                    put("_id", "field_004")
                                    put("name", "총 추출 시간")
                                    put("icon", "clock")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 2)
                                    put("isDisplay", false)
                                    putArray("values").add("03:20")
                                }
                                addObject().apply {
                                    put("_id", "field_005")
                                    put("name", "메모")
                                    put("icon", "clock")
                                    put("type", "LONG_TEXT")
                                    putArray("attributes")
                                    put("order", 3)
                                    put("isDisplay", false)
                                    putArray("values").add("밸런스가 좋고 과일 향이 풍부함 !!")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "추출 온도123")
                                    put("icon", "temp")
                                    putArray("values").add("100")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", false)
                            put("owner", "user_beta_5678")
                            put("hash", "390ec684c1d1b32ea48603ca54f32dd3")
                        }

                        // 같은 노트 수정 메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = sameNoteNode.toString(),
                            after = sameNoteNode.toString(),
                            op = DebeziumOperation.UPDATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = noteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 색인 정보가 그대로여야함
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteIndex/update-note-index.json",
                                verbose = false
                            )
                        }
                    }
                }

                feature("Delete operation 테스트") {
                    scenario("Note가 삭제되어 $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 해당 Note의 색인 정보가 삭제되어야 한다.") {

                        // 데이터베이스에 노트가 삭제된 상황을 가정함.
                        testDataHelper.setData("${baseData}/noteIndex/delete-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteIndex/note-index.json")

                        // 여기에는 삭제된 노트가 들어있음.
                        testMeiliSearchHelper.insertData("${baseData}/noteIndex/delete-note-index.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        // 기존에 존재하는 Note
                        val existingNoteId = "66a7b2a60e0a514d2a1c0001"
                        val existingNote = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", existingNoteId)
                            put("externalId", "a1b2c3d4-e5f6-4789-0123-4567890abcde")
                            put("title", "카페인 분석 노트: 에티오피아 예가체프")
                            put("thumbnail", "https://images.example.com/thumbnails/coffee_yirgacheffe.jpg")
                            putObject("createdDate").put("\$date", "2024-07-29T10:00:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T10:00:00Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                // 첫 번째 필드: 원산지
                                addObject().apply {
                                    put("_id", "field_001")
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes") // 빈 배열
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("에티오피아")
                                }

                                // 두 번째 필드: 로스팅 정도 (RADIO 타입, attributes 있음)
                                addObject().apply {
                                    put("_id", "field_002")
                                    put("name", "로스팅 정도")
                                    put("icon", "fire")
                                    put("type", "RADIO")

                                    // attributes 내부 구조 처리
                                    putArray("attributes").apply {
                                        addObject().apply {
                                            put("key", "options")
                                            putArray("value").apply {
                                                add("라이트")
                                                add("미디엄")
                                                add("다크")
                                            }
                                        }
                                    }

                                    put("order", 2)
                                    put("isDisplay", true)
                                    putArray("values").add("라이트")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    putArray("values").add("에티오피아")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", true)
                            put("owner", "user_alpha_1234")
                            put("hash", "190ec684c1d1b32ea48603ca54f32dd1")
                        }

                        val deleteNote = existingNote.deepCopy().apply {
                            put("status", NoteStatus.DELETED.name)
                        }

                        // 해당 노트 삭제  메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = existingNote.toString(),
                            after = deleteNote.toString(),
                            op = DebeziumOperation.UPDATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = existingNoteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )
                        delay(5.seconds)

                        // MeiliSearch 에 색인 정보가 업데이트되었는지 검증
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteIndex/delete-note-index-after.json",
                                verbose = false
                            )
                        }
                    }
                }

            }

            feature("NoteIndexSinkConsumer.sinkNoteFieldIndex") {
                feature("Create operation 테스트") {
                    scenario("Note 가 새롭게 생성되어 $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 해당 Note 의 필드 색인 정보가 생성되어야 한다.") {
                        testDataHelper.setData("${baseData}/noteFieldIndex/new-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteFieldIndex/note-field-index.json")

                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")
                        // 해당 테스트는 2개의 필드에 uuid를 할당하기 때문에 queue모드 켜줘야함.
                        testUuidSource.enableQueueMode()
                        // 새롭게 생성된 Note
                        val newNoteId = "66a7b2a60e0a514d2a1c0001"
                        val newNote = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", "66a7b2a60e0a514d2a1c0001")
                            put("externalId", "a1b2c3d4-e5f6-4789-0123-4567890abcde")
                            put("title", "카페인 분석 노트: 에티오피아 예가체프")
                            put("thumbnail", "https://images.example.com/thumbnails/coffee_yirgacheffe.jpg")
                            putObject("createdDate").put("\$date", "2024-07-29T10:00:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T10:00:00Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                addObject().apply {
                                    put("_id", "field_001")
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("에티오피아")
                                }
                                addObject().apply {
                                    put("_id", "field_002")
                                    put("name", "로스팅 정도")
                                    put("icon", "fire")
                                    put("type", "LONG_TEXT")
                                    putArray("attributes")
                                    put("order", 2)
                                    put("isDisplay", true)
                                    putArray("values").add("라이트")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    putArray("values").add("에티오피아")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", true)
                            put("owner", "user_alpha_1234")
                            put("hash", "190ec684c1d1b32ea48603ca54f32dd1")
                        }
                        // 해당 노트 생성 메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = null,
                            after = newNote.toString(),
                            op = DebeziumOperation.CREATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = newNoteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 필드 색인 정보가 생성되었는지 검증
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteFieldIndex/new-note-index-after.json",
                                verbose = false
                            )
                        }
                    }
                }

                feature("Update operation 테스트") {
                    scenario("Note가 수정되어 $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 해당 Note의 필드 색인 정보가 업데이트되어야 한다.") {

                        // 그냥 데이터베이스에 update 되어있는 노트가 들어있다고 가정함.
                        // 전체 프로세스는 api 통합테스트에서 검증함.
                        testDataHelper.setData("${baseData}/noteFieldIndex/update-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteFieldIndex/note-field-index.json")

                        // 여기에는 변경 전 다른 값의 필드 인덱스가 들어있음.
                        testMeiliSearchHelper.insertData("${baseData}/noteFieldIndex/update-note-field-index-before.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        testUuidSource.enableQueueMode()

                        // 기존에 존재하는 Note
                        val baseNoteId = "66a7b2a60e0a514d2a1c0002"
                        val baseNote = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", "66a7b2a60e0a514d2a1c0002")
                            put("externalId", "b2c3d4e5-f6a7-4890-1234-567890abcdef")
                            put("title", "업데이트할 노트")
                            put("thumbnail", "https://images.example.com/thumbnails/updated.jpg")
                            putObject("createdDate").put("\$date", "2024-07-29T10:00:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T10:00:00Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                addObject().apply {
                                    put("_id", "field_001")
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("케냐")
                                }
                                addObject().apply {
                                    put("_id", "field_002")
                                    put("name", "로스팅 정도")
                                    put("icon", "fire")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 2)
                                    put("isDisplay", true)
                                    putArray("values").add("미디엄")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    putArray("values").add("케냐")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", true)
                            put("owner", "user_alpha_1234")
                        }

                        val existingNote = baseNote.deepCopy().apply {
                            put("hash", "390ec684c1d1b32ea48603ca54f32dd2")
                        }

                        val updateNote = baseNote.deepCopy().apply {
                            put("hash", "390ec684c1d1b32ea48603ca54f32dd3")
                        }

                        // 해당 노트 수정 메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = existingNote.toString(),
                            after = updateNote.toString(),
                            op = DebeziumOperation.UPDATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = baseNoteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 필드 색인 정보가 업데이트되었는지 검증
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteFieldIndex/update-note-field-index-after.json",
                                verbose = false
                            )
                        }
                    }

                    scenario("Note가 수정되었지만, Hash가 변경된 데이터 변경이 아닐때, $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 무시되어야한다.") {

                        testDataHelper.setData("${baseData}/noteFieldIndex/update-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteFieldIndex/note-field-index.json")

                        testMeiliSearchHelper.insertData("${baseData}/noteFieldIndex/update-note-field-index-before.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        // 기존에 존재하는 Note
                        val baseNoteId = "66a7b2a60e0a514d2a1c0002"
                        val existingNote = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", "66a7b2a60e0a514d2a1c0002")
                            put("externalId", "b2c3d4e5-f6a7-4890-1234-567890abcdef")
                            put("title", "업데이트할 노트")
                            put("thumbnail", "https://images.example.com/thumbnails/updated.jpg")
                            putObject("createdDate").put("\$date", "2024-07-29T10:00:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T10:00:00Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields").apply {
                                addObject().apply {
                                    put("_id", "field_001")
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 1)
                                    put("isDisplay", true)
                                    putArray("values").add("케냐")
                                }
                                addObject().apply {
                                    put("_id", "field_002")
                                    put("name", "로스팅 정도")
                                    put("icon", "fire")
                                    put("type", "SHORT_TEXT")
                                    putArray("attributes")
                                    put("order", 2)
                                    put("isDisplay", true)
                                    putArray("values").add("미디엄")
                                }
                            }

                            putArray("displayFields").apply {
                                addObject().apply {
                                    put("name", "원산지")
                                    put("icon", "globe")
                                    putArray("values").add("케냐")
                                    put("order", 1)
                                }
                            }

                            put("isOpen", true)
                            put("owner", "user_alpha_1234")
                            put("hash", "390ec684c1d1b32ea48603ca54f32dd2")
                        }


                        // 같은 노트 수정 메시지 발행
                        val same = existingNote.toString()
                        val changeEvent = createChangeMessage<Note>(
                            before = same,
                            after = same,
                            op = DebeziumOperation.UPDATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = baseNoteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 필드 색인 정보가 그대로여야함
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteFieldIndex/update-note-field-index-before.json",
                                verbose = false
                            )
                        }
                    }
                }

                feature("Delete operation 테스트") {
                    scenario("Note가 삭제되어 $NOTE_CHANGE_TOPIC 토픽에 메시지가 발행될 때, 해당 Note의 필드 색인 정보가 삭제되어야 한다.") {

                        // 데이터베이스에 노트가 삭제된 상황을 가정함.
                        testDataHelper.setData("${baseData}/noteFieldIndex/delete-note.json")
                        testMeiliSearchHelper.insertIndex("${baseData}/noteFieldIndex/note-field-index.json")

                        // 여기에는 삭제될 노트의 필드 인덱스가 들어있음.
                        testMeiliSearchHelper.insertData("${baseData}/noteFieldIndex/delete-note-field-index.json")
                        testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                        // 기존에 존재하는 Note
                        val existingNoteId = "66a7b2a60e0a514d2a1c0001"
                        val existingNote = objectMapper.createObjectNode().apply {
                            putObject("_id").put("\$oid", existingNoteId)
                            put("externalId", "a1b2c3d4-e5f6-4789-0123-4567890abcde")
                            put("title", "카페인 분석 노트: 에티오피아 예가체프")
                            put("thumbnail", "https://images.example.com/thumbnails/coffee_yirgacheffe.jpg")
                            putObject("createdDate").put("\$date", "2024-07-29T10:00:00Z")
                            putObject("modifiedDate").put("\$date", "2024-07-29T10:00:00Z")
                            put("status", NoteStatus.ACTIVE.name)
                            putArray("fields") // 빈 배열 생성
                            putArray("displayFields") // 빈 배열 생성

                            put("isOpen", true)
                            put("owner", "user_alpha_1234")
                            put("hash", "190ec684c1d1b32ea48603ca54f32dd1")
                        }

                        val deleteNote = existingNote.deepCopy().apply {
                            put("status", NoteStatus.DELETED.name)
                        }

                        // 해당 노트 삭제 메시지 발행
                        val changeEvent = createChangeMessage<Note>(
                            before = existingNote.toString(),
                            after = deleteNote.toString(),
                            op = DebeziumOperation.UPDATE
                        )

                        testKafkaHelper.sendMessage(
                            topic = NOTE_CHANGE_TOPIC,
                            key = existingNoteId,
                            value = objectMapper.writeValueAsString(changeEvent)
                        )

                        // MeiliSearch 에 필드 색인 정보가 삭제되었는지 검증
                        eventually(15.seconds) {
                            testMeiliSearchHelper.assertData(
                                "$baseData/noteFieldIndex/delete-note-field-index-after.json",
                                verbose = false
                            )
                        }
                    }
                }
            }


            feature("dlq 테스트 ") {
                scenario("MeiliSearch 연결 오류가 발생할 때, DLQ로 전송되어야 한다.") {
                    testDataHelper.setData("${baseData}/noteIndex/new-note.json")
                    testMeiliSearchHelper.insertIndex("${baseData}/noteIndex/note-index.json")
                    testDebeziumHelper.registerConnector("testsets/debezium/mongo-note-connector.json")

                    // MeiliSearch 연결 차단 시뮬레이션
                    MeiliSearchContainerInitializer.meiliSearch.stop()

                    // 새롭게 생성된 Note
                    val newNoteId = "66a7b2a60e0a514d2a1c0002"
                    val newNoteNode = objectMapper.createObjectNode().apply {
                        putObject("_id").put("\$oid", "66a7b2a60e0a514d2a1c0002")
                        put("externalId", "b2c3d4e5-f6a7-4890-1234-567890abcdef")
                        put("title", "핸드드립 레시피: 케냐 AA")
                        putNull("thumbnail")
                        putObject("createdDate").put("\$date", "2024-07-28T15:30:00Z")
                        putObject("modifiedDate").put("\$date", "2024-07-29T11:05:20Z")
                    }
                    // 해당 노트 생성 메시지 발행
                    val changeEvent = createChangeMessage<Note>(
                        before = null,
                        after = newNoteNode.toString(),
                        op = DebeziumOperation.CREATE
                    )

                    testKafkaHelper.sendMessage(
                        topic = NOTE_CHANGE_TOPIC,
                        key = newNoteId,
                        value = objectMapper.writeValueAsString(changeEvent)
                    )

                    // DLQ에 메시지가 전송되었는지 검증
                    eventually(30.seconds) {
                        val dlqMessages = testKafkaHelper.getMessages(
                            topic = NOTE_CHANGE_TOPIC_DLT,
                        )
                        logger.debug { "DLQ Messages: $dlqMessages" }
                        dlqMessages.size shouldBe 2
                    }
                }
            }
        }
    }

}