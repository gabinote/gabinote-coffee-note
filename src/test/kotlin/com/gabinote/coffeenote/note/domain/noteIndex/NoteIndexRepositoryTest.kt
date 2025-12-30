package com.gabinote.coffeenote.note.domain.noteIndex

import com.gabinote.coffeenote.note.domain.noteIndex.vo.DateRangeFilter
import com.gabinote.coffeenote.testSupport.testTemplate.MeiliSearchTestTemplate
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        NoteIndexRepository::class,
    ]
)
class NoteIndexRepositoryTest : MeiliSearchTestTemplate() {

    @Autowired
    lateinit var noteIndexRepository: NoteIndexRepository


    override val baseIndexDir = "/testsets/note/domain/noteIndex/index"
    override val baseDataDir = "/testsets/note/domain/noteIndex/data"
    override val baseData = "base.json"
    override val baseIndex = "note-index.json"

    init {
        describe("[Note] NoteIndexRepository Test") {
            describe("NoteIndexRepository.searchNotes") {

                describe("Query에 설정된 검색어가 없는 케이스(* wildcard 검색)") {
                    context("올바른 사용자 ID가 주어진 경우") {
                        useBaseIndex()
                        useBaseData()
                        val validOwnerId = "user_alpha"
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000001",
                                externalId = "a1a1a1a1-0001-4001-8001-000000000001",
                                title = "에티오피아 예가체프 G1 워시드",
                                owner = "user_alpha",
                                createdDate = 1731821801000,
                                modifiedDate = 1731821801000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("에티오피아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("워시드"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "등급", tag = "GRADE_TAG", value = listOf("G1"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000002",
                                externalId = "a1a1a1a1-0002-4002-8002-000000000002",
                                title = "케냐 AA 아이스 드립",
                                owner = "user_alpha",
                                createdDate = 1731821802000,
                                modifiedDate = 1731821802000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("케냐"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "로스팅_포인트",
                                        tag = "ROAST_LEVEL_TAG",
                                        value = listOf("미디엄"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("자몽, 과일향, 산미"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000004",
                                externalId = "a1a1a1a1-0004-4004-8004-000000000004",
                                title = "코만단테 그라인더 테스트 (콜롬비아)",
                                owner = "user_alpha",
                                createdDate = 1731821804000,
                                modifiedDate = 1731821804000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "그라인더",
                                        tag = "GRINDER_TAG",
                                        value = listOf("코만단테"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("93도"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000005",
                                externalId = "a1a1a1a1-0005-4005-8005-000000000005",
                                title = "비오는 날의 라떼",
                                owner = "user_alpha",
                                createdDate = 1731821805000,
                                modifiedDate = 1731821805000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "카페_이름",
                                        tag = "CAFE_NAME_TAG",
                                        value = listOf("동네카페"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "마신_날씨",
                                        tag = "CONTEXT_TAG",
                                        value = listOf("비오는_날"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000006",
                                externalId = "a1a1a1a1-0006-4006-8006-000000000006",
                                title = "콜롬비아 무산소 발효",
                                owner = "user_alpha",
                                createdDate = 1731821806000,
                                modifiedDate = 1731821806000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("무산소 발효"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("와인, 열대과일"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000007",
                                externalId = "a1a1a1a1-0007-4007-8007-000000000007",
                                title = "브라질 산토스 (프렌치 프레스)",
                                owner = "user_alpha",
                                createdDate = 1731821807000,
                                modifiedDate = 1731821807000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("브라질"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("프렌치 프레스"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "바디감", tag = "BODY_TAG", value = listOf("무거움"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000008",
                                externalId = "a1a1a1a1-0008-4008-8008-000000000008",
                                title = "디카페인 커피",
                                owner = "user_alpha",
                                createdDate = 1731821808000,
                                modifiedDate = 1731821808000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "태그",
                                        tag = "GENERAL_TAG",
                                        value = listOf("디카페인"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "구매처",
                                        tag = "STORE_TAG",
                                        value = listOf("온라인"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("푸어오버"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000009",
                                externalId = "a1a1a1a1-0009-4009-8009-000000000009",
                                title = "과테말라 안티구아 (90도)",
                                owner = "user_alpha",
                                createdDate = 1731821809000,
                                modifiedDate = 1731821809000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("과테말라"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("90도"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("스모키, 초콜릿"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000010",
                                externalId = "a1a1a1a1-0010-4010-8010-000000000010",
                                title = "파나마 게이샤 스페셜티",
                                owner = "user_alpha",
                                createdDate = 1731821810000,
                                modifiedDate = 1731821810000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("파나마"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("워시드"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "평점", tag = "RATING_TAG", value = listOf("5점"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("해당 사용자가 소유한 모든 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotes(
                                owner = validOwnerId,
                                query = "*",
                                highlightTag = "em",
                                pageable = pageable
                            )
                            res shouldBe expected

                        }
                    }

                    context("존재하지 않는 사용자 ID가 주어진 경우") {
                        useBaseIndex()
                        useBaseData()
                        val invalidOwnerId = "user_unknown"
                        val pageable = PageRequest.of(0, 10)
                        val expected = SliceImpl<NoteIndex>(
                            emptyList(),
                            pageable,
                            false
                        )
                        it("빈 결과를 반환한다") {
                            val res = noteIndexRepository.searchNotes(
                                owner = invalidOwnerId,
                                query = "*",
                                highlightTag = "em",
                                pageable = pageable
                            )
                            res shouldBe expected

                        }
                    }
                }
                describe("특정 검색어가 Query에 설정된 케이스") {
                    context("title에 존재하는 검색어가 주어진 경우") {
                        useBaseIndex()
                        useBaseData()
                        val validOwnerId = "user_alpha"
                        val searchQuery = "그라인더"
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000004",
                                externalId = "a1a1a1a1-0004-4004-8004-000000000004",
                                title = "코만단테 <em>그라인더</em> 테스트 (콜롬비아)",
                                owner = "user_alpha",
                                createdDate = 1731821804000,
                                modifiedDate = 1731821804000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "그라인더",
                                        tag = "GRINDER_TAG",
                                        value = listOf("코만단테"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("93도"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("title에 검색어가 포함된 노트를 조회하고, 하이라이트 처리한다") {
                            val res = noteIndexRepository.searchNotes(
                                owner = validOwnerId,
                                query = searchQuery,
                                highlightTag = "em",
                                pageable = pageable
                            )
                            res shouldBe expected

                        }
                    }
                    context("filters 에 존재하는 검색어가 주어진 경우") {
                        useBaseIndex()
                        useBaseData()
                        val validOwnerId = "user_alpha"
                        val searchQuery = "자몽"
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000002",
                                externalId = "a1a1a1a1-0002-4002-8002-000000000002",
                                title = "케냐 AA 아이스 드립",
                                owner = "user_alpha",
                                createdDate = 1731821802000,
                                modifiedDate = 1731821802000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("케냐"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "로스팅_포인트",
                                        tag = "ROAST_LEVEL_TAG",
                                        value = listOf("미디엄"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("자몽, 과일향, 산미"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(
                                    "향미" to listOf("<em>자몽</em>", "과일향", "산미"),
                                ),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("filters에 검색어가 포함된 노트를 조회하고, 오로지 해당 필드만을 표시한다") {
                            val res = noteIndexRepository.searchNotes(
                                owner = validOwnerId,
                                query = searchQuery,
                                highlightTag = "em",
                                pageable = pageable
                            )
                            res shouldBe expected
                        }
                    }

                    context("title과 filters 에 모두 존재하는 검색어가 주어진 경우") {
                        useBaseIndex()
                        useBaseData()
                        val validOwnerId = "user_alpha"
                        val searchQuery = "리저브"
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 <em>리저브</em> (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(
                                    "로스터리" to listOf("스타벅스 <em>리저브</em>"),
                                ),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("title과 filters에 검색어가 포함된 노트를 조회하고, 하이라이트 처리한다") {
                            val res = noteIndexRepository.searchNotes(
                                owner = validOwnerId,
                                query = searchQuery,
                                highlightTag = "em",
                                pageable = pageable
                            )
                            res shouldBe expected
                        }
                    }
                }

            }

            describe("NoteIndexRepository.searchNotesWithFilter") {
                describe("단일 필드에 대한 요청인 케이스") {
                    context("한 필드에 대해 한가지 값이 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val filter = mapOf(
                            "로스터리" to listOf("스타벅스 리저브")
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(
                                    "로스터리" to listOf("<em>스타벅스 리저브</em>"),
                                ),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("해당 필터에 맞는 노트를 조회한다") {

                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = filter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    context("한 필드에 여러 값이 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val filter = mapOf(
                            "향미" to listOf("와인", "견과류"),
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000006",
                                externalId = "a1a1a1a1-0006-4006-8006-000000000006",
                                title = "콜롬비아 무산소 발효",
                                owner = "user_alpha",
                                createdDate = 1731821806000,
                                modifiedDate = 1731821806000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("무산소 발효"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("와인, 열대과일"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(
                                    "향미" to listOf("<em>와인</em>", "열대과일"),
                                ),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000007",
                                externalId = "a1a1a1a1-0007-4007-8007-000000000007",
                                title = "브라질 산토스 (프렌치 프레스)",
                                owner = "user_alpha",
                                createdDate = 1731821807000,
                                modifiedDate = 1731821807000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("브라질"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("프렌치 프레스"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "바디감", tag = "BODY_TAG", value = listOf("무거움"), order = 2)
                                ),
                                filters = mapOf(
                                    "향미" to listOf("<em>견과류</em>"),
                                ),
                                synchronizedAt = 1731821810000
                            )
                        )

                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("해당 필터에 맞는 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = filter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                }

                describe("여러 필드에 대한 요청인 케이스") {
                    context("두 필드에 대해 각각 한가지 값이 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val filter = mapOf(
                            "로스터리" to listOf("스타벅스 리저브"),
                            "향미" to listOf("초콜릿")
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(
                                    "로스터리" to listOf("<em>스타벅스 리저브</em>"),
                                    "향미" to listOf("<em>초콜릿</em>", "스모키"),
                                ),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("해당 필터에 맞는 노트를 조회한다") {

                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = filter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    context("두 필드에 대해 여러 값이 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val filter = mapOf(
                            "로스터리" to listOf("스타벅스 리저브", "코스타 커피"),
                            "향미" to listOf("초콜릿", "견과류")
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(
                                    "로스터리" to listOf("<em>스타벅스 리저브</em>"),
                                    "향미" to listOf("<em>초콜릿</em>", "스모키"),
                                ),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("해당 필터에 맞는 노트를 조회한다") {

                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = filter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }
                }

                describe("날짜 범위 요청인 케이스") {
                    //case 1. createdDate 시작 시간만
                    context("createdDate 시작 시간만 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val createdDateFilter = DateRangeFilter(
                            startDate = 1731821805000,
                            endDate = null
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000005",
                                externalId = "a1a1a1a1-0005-4005-8005-000000000005",
                                title = "비오는 날의 라떼",
                                owner = "user_alpha",
                                createdDate = 1731821805000,
                                modifiedDate = 1731821805000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "카페_이름",
                                        tag = "CAFE_NAME_TAG",
                                        value = listOf("동네카페"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "마신_날씨",
                                        tag = "CONTEXT_TAG",
                                        value = listOf("비오는_날"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000006",
                                externalId = "a1a1a1a1-0006-4006-8006-000000000006",
                                title = "콜롬비아 무산소 발효",
                                owner = "user_alpha",
                                createdDate = 1731821806000,
                                modifiedDate = 1731821806000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("무산소 발효"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("와인, 열대과일"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000007",
                                externalId = "a1a1a1a1-0007-4007-8007-000000000007",
                                title = "브라질 산토스 (프렌치 프레스)",
                                owner = "user_alpha",
                                createdDate = 1731821807000,
                                modifiedDate = 1731821807000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("브라질"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("프렌치 프레스"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "바디감", tag = "BODY_TAG", value = listOf("무거움"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000008",
                                externalId = "a1a1a1a1-0008-4008-8008-000000000008",
                                title = "디카페인 커피",
                                owner = "user_alpha",
                                createdDate = 1731821808000,
                                modifiedDate = 1731821808000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "태그",
                                        tag = "GENERAL_TAG",
                                        value = listOf("디카페인"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "구매처",
                                        tag = "STORE_TAG",
                                        value = listOf("온라인"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("푸어오버"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000009",
                                externalId = "a1a1a1a1-0009-4009-8009-000000000009",
                                title = "과테말라 안티구아 (90도)",
                                owner = "user_alpha",
                                createdDate = 1731821809000,
                                modifiedDate = 1731821809000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("과테말라"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("90도"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("스모키, 초콜릿"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000010",
                                externalId = "a1a1a1a1-0010-4010-8010-000000000010",
                                title = "파나마 게이샤 스페셜티",
                                owner = "user_alpha",
                                createdDate = 1731821810000,
                                modifiedDate = 1731821810000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("파나마"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("워시드"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "평점", tag = "RATING_TAG", value = listOf("5점"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("시작 시간 이후에 생성된 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = emptyMap(),
                                createdDateFilter = createdDateFilter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    //case 2. createdDate 종료 시간만
                    context("createdDate 종료 시간만 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val createdDateFilter = DateRangeFilter(
                            startDate = null,
                            endDate = 1731821805000
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000001",
                                externalId = "a1a1a1a1-0001-4001-8001-000000000001",
                                title = "에티오피아 예가체프 G1 워시드",
                                owner = "user_alpha",
                                createdDate = 1731821801000,
                                modifiedDate = 1731821801000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("에티오피아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("워시드"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "등급", tag = "GRADE_TAG", value = listOf("G1"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000002",
                                externalId = "a1a1a1a1-0002-4002-8002-000000000002",
                                title = "케냐 AA 아이스 드립",
                                owner = "user_alpha",
                                createdDate = 1731821802000,
                                modifiedDate = 1731821802000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("케냐"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "로스팅_포인트",
                                        tag = "ROAST_LEVEL_TAG",
                                        value = listOf("미디엄"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("자몽, 과일향, 산미"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000004",
                                externalId = "a1a1a1a1-0004-4004-8004-000000000004",
                                title = "코만단테 그라인더 테스트 (콜롬비아)",
                                owner = "user_alpha",
                                createdDate = 1731821804000,
                                modifiedDate = 1731821804000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "그라인더",
                                        tag = "GRINDER_TAG",
                                        value = listOf("코만단테"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("93도"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000005",
                                externalId = "a1a1a1a1-0005-4005-8005-000000000005",
                                title = "비오는 날의 라떼",
                                owner = "user_alpha",
                                createdDate = 1731821805000,
                                modifiedDate = 1731821805000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "카페_이름",
                                        tag = "CAFE_NAME_TAG",
                                        value = listOf("동네카페"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "마신_날씨",
                                        tag = "CONTEXT_TAG",
                                        value = listOf("비오는_날"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("종료 시간 이전에 생성된 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = emptyMap(),
                                createdDateFilter = createdDateFilter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    //case 3. modifiedDate 시작 시간만
                    context("modifiedDate 시작 시간만 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val modifiedDateFilter = DateRangeFilter(
                            startDate = 1731821806000,
                            endDate = null
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000006",
                                externalId = "a1a1a1a1-0006-4006-8006-000000000006",
                                title = "콜롬비아 무산소 발효",
                                owner = "user_alpha",
                                createdDate = 1731821806000,
                                modifiedDate = 1731821806000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("무산소 발효"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("와인, 열대과일"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000007",
                                externalId = "a1a1a1a1-0007-4007-8007-000000000007",
                                title = "브라질 산토스 (프렌치 프레스)",
                                owner = "user_alpha",
                                createdDate = 1731821807000,
                                modifiedDate = 1731821807000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("브라질"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("프렌치 프레스"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "바디감", tag = "BODY_TAG", value = listOf("무거움"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000008",
                                externalId = "a1a1a1a1-0008-4008-8008-000000000008",
                                title = "디카페인 커피",
                                owner = "user_alpha",
                                createdDate = 1731821808000,
                                modifiedDate = 1731821808000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "태그",
                                        tag = "GENERAL_TAG",
                                        value = listOf("디카페인"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "구매처",
                                        tag = "STORE_TAG",
                                        value = listOf("온라인"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("푸어오버"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000009",
                                externalId = "a1a1a1a1-0009-4009-8009-000000000009",
                                title = "과테말라 안티구아 (90도)",
                                owner = "user_alpha",
                                createdDate = 1731821809000,
                                modifiedDate = 1731821809000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("과테말라"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("90도"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("스모키, 초콜릿"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000010",
                                externalId = "a1a1a1a1-0010-4010-8010-000000000010",
                                title = "파나마 게이샤 스페셜티",
                                owner = "user_alpha",
                                createdDate = 1731821810000,
                                modifiedDate = 1731821810000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("파나마"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("워시드"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "평점", tag = "RATING_TAG", value = listOf("5점"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("시작 시간 이후에 수정된 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = emptyMap(),
                                modifiedDateFilter = modifiedDateFilter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    //case 4. modifiedDate 종료 시간만
                    context("modifiedDate 종료 시간만 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val modifiedDateFilter = DateRangeFilter(
                            startDate = null,
                            endDate = 1731821804000
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000001",
                                externalId = "a1a1a1a1-0001-4001-8001-000000000001",
                                title = "에티오피아 예가체프 G1 워시드",
                                owner = "user_alpha",
                                createdDate = 1731821801000,
                                modifiedDate = 1731821801000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("에티오피아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("워시드"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "등급", tag = "GRADE_TAG", value = listOf("G1"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000002",
                                externalId = "a1a1a1a1-0002-4002-8002-000000000002",
                                title = "케냐 AA 아이스 드립",
                                owner = "user_alpha",
                                createdDate = 1731821802000,
                                modifiedDate = 1731821802000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("케냐"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "로스팅_포인트",
                                        tag = "ROAST_LEVEL_TAG",
                                        value = listOf("미디엄"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("자몽, 과일향, 산미"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000004",
                                externalId = "a1a1a1a1-0004-4004-8004-000000000004",
                                title = "코만단테 그라인더 테스트 (콜롬비아)",
                                owner = "user_alpha",
                                createdDate = 1731821804000,
                                modifiedDate = 1731821804000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "그라인더",
                                        tag = "GRINDER_TAG",
                                        value = listOf("코만단테"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("93도"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("종료 시간 이전에 수정된 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = emptyMap(),
                                modifiedDateFilter = modifiedDateFilter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    //case 5. createdDate 시작 시간과 종료 시간
                    context("createdDate 시작 시간과 종료 시간이 모두 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val createdDateFilter = DateRangeFilter(
                            startDate = 1731821803000,
                            endDate = 1731821807000
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000004",
                                externalId = "a1a1a1a1-0004-4004-8004-000000000004",
                                title = "코만단테 그라인더 테스트 (콜롬비아)",
                                owner = "user_alpha",
                                createdDate = 1731821804000,
                                modifiedDate = 1731821804000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "그라인더",
                                        tag = "GRINDER_TAG",
                                        value = listOf("코만단테"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("93도"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000005",
                                externalId = "a1a1a1a1-0005-4005-8005-000000000005",
                                title = "비오는 날의 라떼",
                                owner = "user_alpha",
                                createdDate = 1731821805000,
                                modifiedDate = 1731821805000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "카페_이름",
                                        tag = "CAFE_NAME_TAG",
                                        value = listOf("동네카페"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "마신_날씨",
                                        tag = "CONTEXT_TAG",
                                        value = listOf("비오는_날"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000006",
                                externalId = "a1a1a1a1-0006-4006-8006-000000000006",
                                title = "콜롬비아 무산소 발효",
                                owner = "user_alpha",
                                createdDate = 1731821806000,
                                modifiedDate = 1731821806000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("무산소 발효"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("와인, 열대과일"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000007",
                                externalId = "a1a1a1a1-0007-4007-8007-000000000007",
                                title = "브라질 산토스 (프렌치 프레스)",
                                owner = "user_alpha",
                                createdDate = 1731821807000,
                                modifiedDate = 1731821807000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("브라질"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("프렌치 프레스"),
                                        order = 1
                                    ),
                                    IndexDisplayField(name = "바디감", tag = "BODY_TAG", value = listOf("무거움"), order = 2)
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("시작 시간과 종료 시간 사이에 생성된 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = emptyMap(),
                                createdDateFilter = createdDateFilter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }

                    //case 6. modifiedDate 시작 시간과 종료 시간
                    context("modifiedDate 시작 시간과 종료 시간이 모두 주어진 경우") {
                        useBaseIndex()
                        useBaseData()

                        val validOwnerId = "user_alpha"
                        val modifiedDateFilter = DateRangeFilter(
                            startDate = 1731821802000,
                            endDate = 1731821806000
                        )
                        val pageable = PageRequest.of(0, 10)
                        val expectedNotes = listOf(
                            NoteIndex(
                                id = "65571b111a00000000000002",
                                externalId = "a1a1a1a1-0002-4002-8002-000000000002",
                                title = "케냐 AA 아이스 드립",
                                owner = "user_alpha",
                                createdDate = 1731821802000,
                                modifiedDate = 1731821802000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("케냐"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "로스팅_포인트",
                                        tag = "ROAST_LEVEL_TAG",
                                        value = listOf("미디엄"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("자몽, 과일향, 산미"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000003",
                                externalId = "a1a1a1a1-0003-4003-8003-000000000003",
                                title = "스타벅스 리저브 (에스프레소)",
                                owner = "user_alpha",
                                createdDate = 1731821803000,
                                modifiedDate = 1731821803000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "로스터리",
                                        tag = "ROASTERY_TAG",
                                        value = listOf("스타벅스 리저브"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("초콜릿, 스모키"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000004",
                                externalId = "a1a1a1a1-0004-4004-8004-000000000004",
                                title = "코만단테 그라인더 테스트 (콜롬비아)",
                                owner = "user_alpha",
                                createdDate = 1731821804000,
                                modifiedDate = 1731821804000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "그라인더",
                                        tag = "GRINDER_TAG",
                                        value = listOf("코만단테"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "물_온도",
                                        tag = "WATER_TEMP_TAG",
                                        value = listOf("93도"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000005",
                                externalId = "a1a1a1a1-0005-4005-8005-000000000005",
                                title = "비오는 날의 라떼",
                                owner = "user_alpha",
                                createdDate = 1731821805000,
                                modifiedDate = 1731821805000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "카페_이름",
                                        tag = "CAFE_NAME_TAG",
                                        value = listOf("동네카페"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "추출_방식",
                                        tag = "BREW_METHOD_TAG",
                                        value = listOf("에스프레소"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "마신_날씨",
                                        tag = "CONTEXT_TAG",
                                        value = listOf("비오는_날"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            ),
                            NoteIndex(
                                id = "65571b111a00000000000006",
                                externalId = "a1a1a1a1-0006-4006-8006-000000000006",
                                title = "콜롬비아 무산소 발효",
                                owner = "user_alpha",
                                createdDate = 1731821806000,
                                modifiedDate = 1731821806000,
                                displayFields = listOf(
                                    IndexDisplayField(
                                        name = "품종",
                                        tag = "BEAN_COUNTRY_TAG",
                                        value = listOf("콜롬비아"),
                                        order = 0
                                    ),
                                    IndexDisplayField(
                                        name = "가공_방식",
                                        tag = "PROCESS_METHOD_TAG",
                                        value = listOf("무산소 발효"),
                                        order = 1
                                    ),
                                    IndexDisplayField(
                                        name = "향미",
                                        tag = "FLAVOR_NOTE_TAG",
                                        value = listOf("와인, 열대과일"),
                                        order = 2
                                    )
                                ),
                                filters = mapOf(),
                                synchronizedAt = 1731821810000
                            )
                        )
                        val expected = SliceImpl<NoteIndex>(
                            expectedNotes,
                            pageable,
                            false
                        )
                        it("시작 시간과 종료 시간 사이에 수정된 노트를 조회한다") {
                            val res = noteIndexRepository.searchNotesWithFilter(
                                owner = validOwnerId,
                                filters = emptyMap(),
                                modifiedDateFilter = modifiedDateFilter,
                                pageable = pageable,
                                highlightTag = "em"
                            )

                            res shouldBe expected
                        }
                    }
                }
            }


            describe("NoteIndexRepository.save") {
                context("올바른 NoteIndex 객체가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/save-before.json")

                    val noteIndex = NoteIndex(
                        id = "65571b111a00000000000002",
                        externalId = "a1a1a1a1-0001-4001-8001-000000000002",
                        title = "에티오피아 예가체프 G1 워시드2",
                        owner = "user_alpha",
                        createdDate = 1731821801000,
                        modifiedDate = 1731821801000,
                        displayFields = listOf(
                            IndexDisplayField(
                                name = "품종",
                                tag = "BEAN_COUNTRY_TAG",
                                value = listOf("에티오피아"),
                                order = 0
                            ),
                            IndexDisplayField(
                                name = "가공_방식",
                                tag = "PROCESS_METHOD_TAG",
                                value = listOf("워시드"),
                                order = 1
                            ),
                            IndexDisplayField(name = "등급", tag = "GRADE_TAG", value = listOf("G1"), order = 2)
                        ),
                        filters = mapOf(
                            "품종" to listOf("에티오피아2"),
                            "가공_방식" to listOf("워시드2"),
                            "추출_방식" to listOf("푸어오버2"),
                            "등급" to listOf("G12")
                        ),
                        synchronizedAt = 1731821810000
                    )

                    it("NoteIndex를 저장하고, 저장된 NoteIndex를 검색할 수 있어야 한다") {

                        val taskInfo = noteIndexRepository.save(noteIndex)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/save-after.json")

                    }
                }
            }

            describe("NoteIndexRepository.saveAll") {
                context("올바른 NoteIndex 객체들이 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/save-all-before.json")

                    val noteIndex = NoteIndex(
                        id = "65571b111a00000000000002",
                        externalId = "a1a1a1a1-0001-4001-8001-000000000002",
                        title = "에티오피아 예가체프 G1 워시드2",
                        owner = "user_alpha",
                        createdDate = 1731821801000,
                        modifiedDate = 1731821801000,
                        displayFields = listOf(
                            IndexDisplayField(
                                name = "품종",
                                tag = "BEAN_COUNTRY_TAG",
                                value = listOf("에티오피아"),
                                order = 0
                            ),
                            IndexDisplayField(
                                name = "가공_방식",
                                tag = "PROCESS_METHOD_TAG",
                                value = listOf("워시드"),
                                order = 1
                            ),
                            IndexDisplayField(name = "등급", tag = "GRADE_TAG", value = listOf("G1"), order = 2)
                        ),
                        filters = mapOf(
                            "품종" to listOf("에티오피아2"),
                            "가공_방식" to listOf("워시드2"),
                            "추출_방식" to listOf("푸어오버2"),
                            "등급" to listOf("G12")
                        ),
                        synchronizedAt = 1731821810000
                    )

                    val noteIndex2 = NoteIndex(
                        id = "65571b111a00000000000003",
                        externalId = "a1a1a1a1-0001-4001-8001-000000000003",
                        title = "에티오피아 예가체프 G1 워시드3",
                        owner = "user_alpha",
                        createdDate = 1731821801000,
                        modifiedDate = 1731821801000,
                        displayFields = listOf(
                            IndexDisplayField(
                                name = "품종",
                                tag = "BEAN_COUNTRY_TAG",
                                value = listOf("에티오피아"),
                                order = 0
                            ),
                            IndexDisplayField(
                                name = "가공_방식",
                                tag = "PROCESS_METHOD_TAG",
                                value = listOf("워시드"),
                                order = 1
                            ),
                            IndexDisplayField(name = "등급", tag = "GRADE_TAG", value = listOf("G1"), order = 2)
                        ),
                        filters = mapOf(
                            "품종" to listOf("에티오피아3"),
                            "가공_방식" to listOf("워시드3"),
                            "추출_방식" to listOf("푸어오버3"),
                            "등급" to listOf("G13")
                        ),
                        synchronizedAt = 1731821810000
                    )

                    val list = listOf(noteIndex, noteIndex2)

                    it("NoteIndex들을 저장한다.") {

                        val taskInfo = noteIndexRepository.saveAll(list)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/save-all-after.json")
                    }
                }
            }

            describe("NoteIndexRepository.delete") {

                context("올바른 NoteIndex의 id가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-before.json")

                    val noteIndexId = "65571b111a00000000000001"

                    it("NoteIndex를 삭제하고, 삭제된 NoteIndex가 더 이상 검색되지 않아야 한다") {

                        val taskInfo = noteIndexRepository.delete(noteIndexId)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/delete-after.json")
                    }
                }

                context("존재하지 않는 NoteIndex의 id가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-before.json")

                    val noteIndexId = "65571b111a00000000000099"

                    it("그 어떤 노트도 삭제되지 않아야 한다") {

                        val taskInfo = noteIndexRepository.delete(noteIndexId)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/delete-before.json")

                    }
                }
            }

            describe("NoteIndexRepository.deleteAllByExternalId") {

                context("올바른 NoteIndex의 externalId가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-externalId-before.json")

                    val noteExternalId = "a1a1a1a1-0001-4001-8001-000000000001"

                    it("해당 NoteIndex가 삭제된다.") {

                        val taskInfo = noteIndexRepository.deleteAllByExternalId(noteExternalId)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/delete-externalId-after.json")
                    }
                }

                context("존재하지 않는 NoteIndex의 externalId가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-externalId-before.json")

                    val noteExternalId = "a1a1a1a1-0099-4099-8099-000000000099"

                    it("그 어떤 노트도 삭제되지 않아야 한다") {

                        val taskInfo = noteIndexRepository.deleteAllByExternalId(noteExternalId)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/delete-externalId-before.json")
                    }
                }

            }

            describe("NoteIndexRepository.deleteAllByOwner") {

                context("올바른 ownerId가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-owner-before.json")

                    val ownerId = "user_alpha"

                    it("해당 owner가 소유한 NoteIndex들이 모두 삭제된다.") {

                        val taskInfo = noteIndexRepository.deleteAllByOwner(ownerId)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/delete-owner-after.json")
                    }
                }

                context("존재하지 않는 ownerId가 주어진 경우") {
                    useBaseIndex()
                    testMeiliSearchHelper.insertData("$baseDataDir/delete-owner-before.json")

                    val ownerId = "user_unknown"

                    it("그 어떤 노트도 삭제되지 않아야 한다") {
                        val taskInfo = noteIndexRepository.deleteAllByOwner(ownerId)
                        meiliSearchClient.waitForTask(taskInfo.taskUid)
                        testMeiliSearchHelper.assertData("$baseDataDir/delete-owner-before.json")
                    }

                }

            }
        }
    }
}