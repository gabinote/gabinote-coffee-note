package com.gabinote.coffeenote.note.integration

import com.gabinote.coffeenote.note.scheduler.NoteSinkScheduler
import com.gabinote.coffeenote.testSupport.testTemplate.IntegrationTestTemplate
import io.kotest.assertions.nondeterministic.eventually
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.time.Duration.Companion.seconds

class NoteSinkSchedulerIntegrationTest : IntegrationTestTemplate() {

    @Autowired
    lateinit var noteSinkScheduler: NoteSinkScheduler


    private fun useNoteIndex() {
        testMeiliSearchHelper.insertIndex("testsets/note/scheduler/note-index.json")
    }

    private fun useNoteFieldIndex() {
        testMeiliSearchHelper.insertIndex("testsets/note/scheduler/note-field-index.json")
    }

    private fun useIndexes() {
        testMeiliSearchHelper.insertIndex(
            "testsets/note/scheduler/note-index.json",
            "testsets/note/scheduler/note-field-index.json"
        )
    }

    init {


        feature("[Note] NoteSinkScheduler Integration Test") {

            feature("runMinorNoteIndexSink - Minor Note Index 동기화") {

                scenario("범위 내 노트가 있을 때, 인덱스가 없는 노트는 생성되고 삭제된 노트의 인덱스는 제거된다") {
                    testDataHelper.setData("/testsets/note/scheduler/minor-sink-notes.json")
                    useNoteIndex()
                    testMeiliSearchHelper.insertData("/testsets/note/scheduler/minor-sink-note-index-before.json")

                    noteSinkScheduler.runMinorNoteIndexSink()

                    eventually(10.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/scheduler/minor-sink-note-index-after.json",
                            verbose = false
                        )
                    }
                }
            }

            feature("runMinorNoteFieldIndexSink - Minor Note Field Index 동기화") {

                scenario("범위 내 노트가 있을 때, 필드 인덱스가 없는 노트는 생성되고 삭제된 노트의 필드 인덱스는 제거된다") {
                    testDataHelper.setData("/testsets/note/scheduler/minor-sink-notes.json")
                    useNoteFieldIndex()
                    testMeiliSearchHelper.insertData("/testsets/note/scheduler/minor-sink-note-field-index-before.json")
                    // invalid -> not created -> not delete 순
                    testUuidSource.enableQueueMode(
                        listOf(
                            //"Minor Sink 테스트 노트 3 - 해시 불일치 노트"
                            // "field_004"
                            UUID.fromString("00000000-0000-0000-0000-000000000003"),
                            //"Minor Sink 테스트 노트 1 - 인덱스 없는 신규 노트" 쪽 uuid
                            // "field_001"
                            UUID.fromString("00000000-0000-0000-0000-000000000001"),
                            // "field_002"
                            UUID.fromString("00000000-0000-0000-0000-000000000002"),

                            )
                    )
                    noteSinkScheduler.runMinorNoteFieldIndexSink()

                    eventually(15.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/scheduler/minor-sink-note-field-index-after.json",
                            verbose = false
                        )
                    }
                }
            }

            feature("runMajorNoteIndexSink - Major Note Index 동기화") {

                scenario("범위 이전의 모든 노트에 대해, 인덱스가 없는 노트는 생성되고 삭제된 노트의 인덱스는 제거된다") {
                    testDataHelper.setData("/testsets/note/scheduler/major-sink-notes.json")
                    useNoteIndex()
                    testMeiliSearchHelper.insertData("/testsets/note/scheduler/major-sink-note-index-before.json")

                    noteSinkScheduler.runMajorNoteIndexSink()

                    eventually(10.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/scheduler/major-sink-note-index-after.json",
                            verbose = false
                        )
                    }
                }
            }

            feature("runMajorNoteFieldIndexSink - Major Note Field Index 동기화") {

                scenario("범위 이전의 모든 노트에 대해, 필드 인덱스가 없는 노트는 생성되고 삭제된 노트의 필드 인덱스는 제거된다") {
                    testDataHelper.setData("/testsets/note/scheduler/major-sink-notes.json")
                    useNoteFieldIndex()
                    testMeiliSearchHelper.insertData("/testsets/note/scheduler/major-sink-note-field-index-before.json")
                    // invalid -> not created -> not delete 순
                    testUuidSource.enableQueueMode(
                        listOf(
                            //"Major Sink 테스트 노트 3 - 해쉬 다른 노트",
                            // "field_012"
                            UUID.fromString("00000000-0000-0000-0000-000000000001"),
                            //"Major Sink 테스트 노트 1 - 인덱스 없는 노트"
                            // "field_010"
                            UUID.fromString("00000000-0000-0000-0000-000000000002")
                        )
                    )
                    noteSinkScheduler.runMajorNoteFieldIndexSink()

                    eventually(10.seconds) {
                        testMeiliSearchHelper.assertData(
                            "/testsets/note/scheduler/major-sink-note-field-index-after.json",
                            verbose = false
                        )
                    }
                }
            }
        }
    }
}