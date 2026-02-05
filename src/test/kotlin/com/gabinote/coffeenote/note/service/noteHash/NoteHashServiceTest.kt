package com.gabinote.coffeenote.note.service.noteHash

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.util.hash.HashHelper
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteHashTestDataHelper
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify


class NoteHashServiceTest : ServiceTestTemplate() {

    lateinit var noteHashService: NoteHashService

    @MockK
    lateinit var hashHelper: HashHelper

    @MockK
    lateinit var objectMapper: ObjectMapper

    init {
        beforeTest {
            clearAllMocks()
            noteHashService = NoteHashService(
                hashHelper = hashHelper,
                objectMapper = objectMapper,
            )
        }

        describe("[Note] NoteHashService Test") {
            context("올바른 Note 객체가 주어졌을 때") {
                val note = NoteTestDataHelper.createTestNote()

                // 원본 Note의 모든 필드를 포함한 Map
                val originalNoteMap = mutableMapOf<String, Any?>(
                    "id" to note.id,
                    "externalId" to note.externalId,
                    "title" to note.title,
                    "thumbnail" to note.thumbnail,
                    "createdDate" to note.createdDate,
                    "modifiedDate" to note.modifiedDate,
                    "fields" to note.fields,
                    "displayFields" to note.displayFields,
                    "isOpen" to note.isOpen,
                    "owner" to note.owner,
                    "hash" to note.hash
                )

                // exclude 필드들을 제외한 Map
                val filteredNoteMap = mutableMapOf<String, Any?>(
                    "title" to note.title,
                    "thumbnail" to note.thumbnail,
                    "fields" to note.fields,
                    "displayFields" to note.displayFields,
                    "owner" to note.owner
                )

                val expectedHash = NoteHashTestDataHelper.TEST_HASH + "-generated"

                beforeTest {
                    every {
                        objectMapper.convertValue(
                            note,
                            any<TypeReference<MutableMap<String, Any?>>>()
                        )
                    } returns originalNoteMap

                    every {
                        hashHelper.generateHash(filteredNoteMap)
                    } returns expectedHash
                }

                it("exclude 필드들을 제외한 해시가 생성된다") {
                    val result = noteHashService.create(note)

                    result shouldBe expectedHash
                    result shouldNotBe note.hash

                    verify(exactly = 1) {
                        objectMapper.convertValue(
                            note,
                            any<TypeReference<MutableMap<String, Any?>>>()
                        )
                    }

                    verify(exactly = 1) {
                        hashHelper.generateHash(filteredNoteMap)
                    }
                }

            }
        }
    }

}