package com.gabinote.coffeenote.note.domain.noteFieldIndex

import com.gabinote.coffeenote.common.util.meiliSearch.helper.data.FacetWithCount
import com.gabinote.coffeenote.testSupport.testTemplate.MeiliSearchTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.validation.ListHelper.isSameElements
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        NoteFieldIndexRepository::class,
    ]
)
class NoteFieldIndexRepositoryTest : MeiliSearchTestTemplate() {

    @Autowired
    lateinit var noteFieldIndexRepository: NoteFieldIndexRepository


    override val baseIndexDir = "/testsets/note/domain/noteFieldIndex/index"
    override val baseDataDir = "/testsets/note/domain/noteFieldIndex/data"
    override val baseData = "base.json"
    override val baseIndex = "note-field-index.json"


    init {
        describe("[Note] NoteFieldIndexRepository Test") {
            describe("NoteFieldIndexRepository.searchFieldNameFacets") {

                context("와일드 카드와 올바른 owner 가 주어지면") {
                    useBaseData()

                    val query = "*"
                    val owner = "user_coffee_lover_01"
                    val except = listOf(
                        FacetWithCount(facet = "가공방식", count = 1),
                        FacetWithCount(facet = "도징", count = 1),
                        FacetWithCount(facet = "로스터리", count = 1),
                        FacetWithCount(facet = "원두 (에스프레소)", count = 1),
                        FacetWithCount(facet = "원두명", count = 1),
                        FacetWithCount(facet = "추출 머신", count = 1),
                        FacetWithCount(facet = "추출 시간", count = 1),
                        FacetWithCount(facet = "향미", count = 1)
                    )
                    it("해당 Owner 소유의 필드 이름 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldNameFacets(owner = owner, query = query)

                        res.isSameElements(except) shouldBe true
                    }
                }

                context("특정 문자열과 올바른 owner 가 주어지면") {
                    useBaseData()
                    val query = "원두"
                    val owner = "user_coffee_lover_01"
                    val except = listOf(
                        FacetWithCount(facet = "원두 (에스프레소)", count = 1),
                        FacetWithCount(facet = "원두명", count = 1),
                    )
                    it("해당 Owner의 특정 문자열을 포함하는  필드 이름 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldNameFacets(owner = owner, query = query)

                        res.isSameElements(except) shouldBe true
                    }
                }

                context("존재하지 않는 owner 가 주어지면") {
                    useBaseData()
                    val query = "*"
                    val owner = "user_not_exist"
                    val except = emptyList<FacetWithCount>()
                    it("빈 필드 이름 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldNameFacets(owner = owner, query = query)

                        res.isSameElements(except) shouldBe true
                    }
                }
            }

            describe("NoteFieldIndexRepository.searchFieldValueFacets") {

                context("와일드 카드와 올바른 owner 및 필드 이름이 주어지면") {
                    useBaseData()
                    val query = "*"
                    val owner = "barista_park_02"
                    val fieldName = "물 온도"
                    val except = listOf(
                        FacetWithCount(facet = "92°C", count = 1),
                    )
                    it("해당 Owner 소유의 특정 필드 이름에 대한 필드 값 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldValueFacets(
                            owner = owner,
                            query = query,
                            fieldName = fieldName
                        )

                        res.isSameElements(except) shouldBe true
                    }
                }

                context("특정 문자열과 올바른 owner 및 필드 이름이 주어지면") {
                    useBaseData()
                    val query = "9"
                    val owner = "barista_park_02"
                    val fieldName = "물 온도"
                    val except = listOf(
                        FacetWithCount(facet = "92°C", count = 1),
                    )
                    it("해당 Owner의 특정 필드 이름에 대해 특정 문자열을 포함하는 필드 값 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldValueFacets(
                            owner = owner,
                            query = query,
                            fieldName = fieldName
                        )

                        res.isSameElements(except) shouldBe true
                    }
                }

                context("존재하지 않는 owner 가 주어지면") {
                    useBaseData()
                    val query = "*"
                    val owner = "user_not_exist"
                    val fieldName = "물 온도"
                    val except = emptyList<FacetWithCount>()
                    it("빈 필드 값 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldValueFacets(
                            owner = owner,
                            query = query,
                            fieldName = fieldName
                        )

                        res.isSameElements(except) shouldBe true
                    }
                }

                context("존재하지 않는 필드 이름이 주어지면") {
                    useBaseData()
                    val query = "*"
                    val owner = "barista_park_02"
                    val fieldName = "로스터리"
                    val except = emptyList<FacetWithCount>()
                    it("빈 필드 값 facet 목록을 반환한다") {

                        val res = noteFieldIndexRepository.searchFieldValueFacets(
                            owner = owner,
                            query = query,
                            fieldName = fieldName
                        )

                        res.isSameElements(except) shouldBe true
                    }
                }
            }

            describe("NoteFieldIndexRepository.save") {

                context("노트 필드 인덱스가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/save-before.json")
                    val noteFieldIndex = NoteFieldIndex(
                        id = "a1b2c3d4-0001-4000-8000-000000000003",
                        owner = "user_coffee_lover_01",
                        name = "신규 필드",
                        value = "신규 값",
                        noteId = "65321a0b1f2e3d4c5a6b7c80",
                        synchronizedAt = 1731821801000,
                        noteHash = "hash001"
                    )
                    it("노트 필드 인덱스를 저장한다") {

                        val task = noteFieldIndexRepository.save(noteFieldIndex)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/save-after.json")
                    }
                }
            }

            describe("NoteFieldIndexRepository.saveAll") {

                context("여러 노트 필드 인덱스가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/save-before.json")
                    val noteFieldIndex = NoteFieldIndex(
                        id = "a1b2c3d4-0001-4000-8000-000000000003",
                        owner = "user_coffee_lover_01",
                        name = "신규 필드1",
                        value = "신규 값1",
                        noteId = "65321a0b1f2e3d4c5a6b7c80",
                        synchronizedAt = 1731821801000,
                        noteHash = "hash001"
                    )
                    val noteFieldIndex2 = NoteFieldIndex(
                        id = "a1b2c3d4-0001-4000-8000-000000000004",
                        owner = "user_coffee_lover_01",
                        name = "신규 필드2",
                        value = "신규 값2",
                        noteId = "65321a0b1f2e3d4c5a6b7c80",
                        synchronizedAt = 1731821801000,
                        noteHash = "hash001"
                    )
                    it("모든 노트 필드 인덱스를 저장한다") {

                        val task = noteFieldIndexRepository.saveAll(listOf(noteFieldIndex, noteFieldIndex2))
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/save-all-after.json")
                    }
                }
            }

            describe("NoteFieldIndexRepository.delete") {

                context("존재하는 노트 필드 인덱스 ID 가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-before.json")
                    val noteFieldIndexId = "a1b2c3d4-0001-4000-8000-000000000001"
                    it("해당 노트 필드 인덱스를 삭제한다") {

                        val task = noteFieldIndexRepository.delete(noteFieldIndexId)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/delete-after.json")
                    }
                }

                context("존재하지 않는 노트 필드 인덱스 ID 가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-before.json")
                    val noteFieldIndexId = "not_exist_id"
                    it("아무 일도 일어나지 않는다") {

                        val task = noteFieldIndexRepository.delete(noteFieldIndexId)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/delete-before.json")
                    }
                }
            }

            describe("NoteFieldIndexRepository.deleteAllByNoteId") {
                context("존재하는 노트 ID 가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-noteId-before.json")
                    val noteId = "65321a0b1f2e3d4c5a6b7c80"
                    it("해당 노트 ID 와 연관된 모든 노트 필드 인덱스를 삭제한다") {

                        val task = noteFieldIndexRepository.deleteAllByNoteId(noteId)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/delete-noteId-after.json")
                    }
                }

                context("존재하지 않는 노트 ID 가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-noteId-before.json")
                    val noteId = "not_exist_note_id"
                    it("아무 일도 일어나지 않는다") {

                        val task = noteFieldIndexRepository.deleteAllByNoteId(noteId)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/delete-noteId-before.json")
                    }
                }
            }

            describe("NoteFieldIndexRepository.deleteAllByOwner") {
                context("존재하는 owner 가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-owner-before.json")
                    val owner = "user_coffee_lover_01"
                    it("해당 owner 가 소유한 모든 노트 필드 인덱스를 삭제한다") {

                        val task = noteFieldIndexRepository.deleteAllByOwner(owner)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/delete-owner-after.json")
                    }
                }

                context("존재하지 않는 owner 가 주어지면") {

                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-owner-before.json")
                    val owner = "user_not_exist"
                    it("아무 일도 일어나지 않는다") {

                        val task = noteFieldIndexRepository.deleteAllByOwner(owner)
                        meiliSearchClient.waitForTask(task.taskUid)

                        testMeiliSearchHelper.assertData("$baseDataDir/delete-owner-before.json")
                    }
                }
            }

        }
    }

}