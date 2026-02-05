package com.gabinote.coffeenote.note.consumer.noteUserWithdraw

import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEvent
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE
import com.gabinote.coffeenote.note.event.userWithdraw.UserWithdrawEventHelper.USER_WITHDRAW_EVENT_TYPE_DLQ
import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.testSupport.testConfig.meiliSearch.MeiliSearchContainerInitializer
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import com.gabinote.coffeenote.user.domain.withdrawProcessHistory.WithdrawProcessHistoryRepository
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import kotlin.time.Duration.Companion.seconds


class NoteUserWithdrawConsumerTest() : IntegrationTestTemplate() {


    private val baseDir = "/testsets/note/consumer/note-user-withdraw"

    @Autowired
    lateinit var withdrawProcessHistoryRepository: WithdrawProcessHistoryRepository

    init {

        beforeTest {
            testKafkaHelper.deleteAllTopics()
        }

        feature("[Note] NoteUserWithdrawConsumer 테스트") {
            scenario("$USER_WITHDRAW_EVENT_TYPE topic에 메시지가 발행되면, 해당 사용자의 노트와 인덱스를 모두 삭제하고,NOTE_DELETE, NOTE_INDEX_DELETE, NOTE_FIELD_INDEX_DELETE 이력히스토리가 기록되어야 한다.") {


                testDataHelper.setData("$baseDir/base-note.json")
                testDataHelper.setData("$baseDir/base-req.json")
                testDataHelper.setData("$baseDir/base-histories.json")

                testMeiliSearchHelper.insertIndex("$baseDir/note-index.json", "$baseDir/note-field-index.json")
                testMeiliSearchHelper.insertData(
                    "$baseDir/delete-noteIndex-before.json",
                    "$baseDir/delete-noteFieldIndex-before.json"
                )
                val targetOwner = "user_beta_5678"
                val userWithdrawEvent = UserWithdrawEvent(
                    uid = targetOwner,
                )

                val payload = objectMapper.nodeFactory.objectNode().apply {
                    // 실제로는 outbox ObjectId가 들어감
                    put("id", userWithdrawEvent.uid)
                    put("payload", objectMapper.writeValueAsString(userWithdrawEvent))
                }

                testKafkaHelper.sendMessage(
                    topic = USER_WITHDRAW_EVENT_TYPE,
                    key = userWithdrawEvent.uid,
                    value = payload.toString(),
                )

                eventually(15.seconds) {

                    // 노트가 삭제되어야함.
                    testDataHelper.assertData("$baseDir/delete-note-after.json")

                    // 노트 인덱스도 삭제
                    testMeiliSearchHelper.assertData(
                        "$baseDir/delete-noteIndex-after.json", verbose = false
                    )
                    // 필드 인덱스도 삭제
                    testMeiliSearchHelper.assertData(
                        "$baseDir/delete-noteFieldIndex-after.json", verbose = false
                    )

                    // NOTE_DELETE, NOTE_INDEX_DELETE, NOTE_FIELD_INDEX_DELETE 프로세스 히스토리 기록이 추가되야함.
                    val histories = withdrawProcessHistoryRepository.findAll()

                    histories.size shouldBe 3
                    histories.count { it.process == WithdrawProcess.NOTE_DELETE.value && it.isPassed } shouldBe 1
                    histories.count { it.process == WithdrawProcess.NOTE_INDEX_DELETE.value && it.isPassed } shouldBe 1
                    histories.count { it.process == WithdrawProcess.NOTE_FIELD_INDEX_DELETE.value && it.isPassed } shouldBe 1
                }
            }
            feature("장애 테스트") {
                scenario("$USER_WITHDRAW_EVENT_TYPE topic에 메시지가 발행되었으나, MeiliSearch 연결 장애가 발생하면 , NOTE_DELETE만 정상 처리되고, NOTE_INDEX_DELETE, NOTE_FIELD_INDEX_DELETE는 dlq에 쌓여야 한다.") {
                    testDataHelper.setData("$baseDir/base-note.json")
                    testDataHelper.setData("$baseDir/base-req.json")
                    testDataHelper.setData("$baseDir/base-histories.json")

                    testMeiliSearchHelper.insertIndex("$baseDir/note-index.json", "$baseDir/note-field-index.json")
                    testMeiliSearchHelper.insertData(
                        "$baseDir/delete-noteIndex-before.json",
                        "$baseDir/delete-noteFieldIndex-before.json"
                    )
                    val targetOwner = "user_beta_5678"
                    val userWithdrawEvent = UserWithdrawEvent(
                        uid = targetOwner,
                    )

                    val payload = objectMapper.nodeFactory.objectNode().apply {
                        // 실제로는 outbox ObjectId가 들어감
                        put("id", userWithdrawEvent.uid)
                        put("payload", objectMapper.writeValueAsString(userWithdrawEvent))
                    }

                    // MeiliSearch 연결 장애 시뮬레이션
                    MeiliSearchContainerInitializer.meiliSearch.stop()

                    eventually(15.seconds) {
                        testMeiliSearchHelper.checkConnection() shouldBe false
                    }

                    testKafkaHelper.sendMessage(
                        topic = USER_WITHDRAW_EVENT_TYPE,
                        key = userWithdrawEvent.uid,
                        value = payload.toString(),
                    )

                    eventually(30.seconds) {
                        testDataHelper.assertData("$baseDir/delete-note-after.json")

                        // dlq에 2건의 메시지가 쌓여야함. (NOTE_INDEX_DELETE, NOTE_FIELD_INDEX_DELETE)
                        val message = testKafkaHelper.getMessages(USER_WITHDRAW_EVENT_TYPE_DLQ)
                        message.size shouldBe 2


                        // NOTE_DELETE, NOTE_INDEX_DELETE, NOTE_FIELD_INDEX_DELETE 프로세스 히스토리 기록이 추가되야함.
                        val histories = withdrawProcessHistoryRepository.findAll()
                        histories.size shouldBe 3
                        // NOTE_DELETE만 성공 이력
                        histories.count { it.process == WithdrawProcess.NOTE_DELETE.value && it.isPassed } shouldBe 1
                        // NOTE_INDEX_DELETE, NOTE_FIELD_INDEX_DELETE는 실패 이력
                        histories.count { it.process == WithdrawProcess.NOTE_INDEX_DELETE.value && !it.isPassed } shouldBe 1
                        histories.count { it.process == WithdrawProcess.NOTE_FIELD_INDEX_DELETE.value && !it.isPassed } shouldBe 1
                    }
                }
            }

        }
    }
}