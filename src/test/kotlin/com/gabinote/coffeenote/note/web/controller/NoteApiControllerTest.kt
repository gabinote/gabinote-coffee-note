package com.gabinote.coffeenote.note.web.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.note.dto.note.controller.NoteUpdateReqControllerDto
import com.gabinote.coffeenote.note.dto.note.service.NoteListResServiceDto
import com.gabinote.coffeenote.note.dto.note.service.NoteResServiceDto
import com.gabinote.coffeenote.note.dto.noteField.controller.NoteFieldCreateReqControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetListResControllerDto
import com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldValueFacetWithCountResControllerDto
import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
import com.gabinote.coffeenote.note.mapping.note.NoteMapper
import com.gabinote.coffeenote.note.mapping.noteFieldIndex.NoteFieldIndexMapper
import com.gabinote.coffeenote.note.mapping.noteIndex.NoteIndexMapper
import com.gabinote.coffeenote.note.service.note.NoteService
import com.gabinote.coffeenote.note.service.noteFieldIndex.NoteFieldIndexService
import com.gabinote.coffeenote.note.service.noteIndex.NoteIndexService
import com.gabinote.coffeenote.testSupport.testDocs.note.NoteDocsSchema
import com.gabinote.coffeenote.testSupport.testTemplate.WebMvcTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteIndexTestDataHelper.createTestNoteIndexResControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestNoteListResControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.note.NoteTestDataHelper.createTestNoteResControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil.createPageable
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSlice
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSliceResponse
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Sort
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@WebMvcTest(controllers = [NoteApiController::class])
class NoteApiControllerTest : WebMvcTestTemplate() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var noteService: NoteService

    @MockkBean
    private lateinit var noteFieldIndexService: NoteFieldIndexService

    @MockkBean
    private lateinit var noteMapper: NoteMapper

    @MockkBean
    private lateinit var sliceMapper: SliceMapper

    @MockkBean
    private lateinit var userContext: UserContext

    @MockkBean
    private lateinit var noteFieldIndexMapper: NoteFieldIndexMapper

    @MockkBean
    private lateinit var noteIndexService: NoteIndexService

    @MockkBean
    private lateinit var noteIndexMapper: NoteIndexMapper

    @SpykBean
    lateinit var fieldTypeFactory: FieldTypeFactory

    private val apiPrefix = "/api/v1"


    init {
        beforeTest {
            every { fieldTypeFactory.getFieldType("DROP_DOWN") } returns TestFieldType
        }

        describe("[Note] NoteApiController Test") {
            describe("NoteApiController.getAllMyNotes") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    // 기본 pageable 값: size = 20, page = 0, sort = createdDate,desc
                    val defaultPageable = createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                    val notes = mockk<NoteListResServiceDto>()
                    val data = createTestNoteListResControllerDto(owner = requestor)
                    val expected = listOf(data).toSliceResponse(defaultPageable)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteService.getAllByOwner(
                                owner = requestor,
                                pageable = defaultPageable
                            )
                        } returns listOf(notes).toSlice(defaultPageable)
                        every { noteMapper.toListResControllerDto(notes) } returns data
                        every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                    }

                    it("노트 목록을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/notes/me"))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/getAllMyNotes",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 목록 조회")
                                            .queryParameters(
                                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)")
                                                    .optional(),
                                                parameterWithName("size").description("페이지 크기 (최소 1, 최대 100, 기본값 20)")
                                                    .optional(),
                                                parameterWithName("sort").description("정렬 기준 및 방향 (기본값 createdDate,desc)")
                                                    .optional()
                                            )
                                            .responseFields(
                                                *NoteDocsSchema.noteListSliceResponseSchema
                                            )
                                            .responseSchema(Schema("NoteListSliceResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteService.getAllByOwner(owner = requestor, pageable = defaultPageable)
                            noteMapper.toListResControllerDto(notes)
                            sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable))
                        }
                    }
                }

                describe("페이징 사이즈 테스트") {
                    context("올바른 사이즈가 주어지면") {
                        val requestor = UUID.randomUUID().toString()
                        val validSize = 50
                        val pageable =
                            createPageable(
                                sortKey = "createdDate",
                                sortDirection = Sort.Direction.DESC,
                                size = validSize
                            )
                        val notes = mockk<NoteListResServiceDto>()
                        val data = createTestNoteListResControllerDto(owner = requestor)
                        val expected = listOf(data).toSliceResponse(pageable)

                        beforeTest {
                            every { userContext.uid } returns requestor
                            every {
                                noteService.getAllByOwner(
                                    owner = requestor,
                                    pageable = pageable
                                )
                            } returns listOf(notes).toSlice(pageable)
                            every { noteMapper.toListResControllerDto(notes) } returns data
                            every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                        }

                        it("노트 목록을 조회하고, 200 OK를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me")
                                    .param("size", validSize.toString())
                            )
                                .andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                            verify(exactly = 1) {
                                userContext.uid
                                noteService.getAllByOwner(owner = requestor, pageable = pageable)
                                noteMapper.toListResControllerDto(notes)
                                sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable))
                            }
                        }
                    }

                    context("사이즈가 100보다 크면") {
                        val invalidSize = 101

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me")
                                    .param("size", invalidSize.toString())
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }

                describe("정렬 키 테스트") {
                    context("올바른 정렬 키가 주어지면") {
                        val requestor = UUID.randomUUID().toString()
                        val validSortKey = "title"
                        val pageable = createPageable(sortKey = validSortKey, sortDirection = Sort.Direction.ASC)
                        val notes = mockk<NoteListResServiceDto>()
                        val data = createTestNoteListResControllerDto(owner = requestor)
                        val expected = listOf(data).toSliceResponse(pageable)

                        beforeTest {
                            every { userContext.uid } returns requestor
                            every {
                                noteService.getAllByOwner(
                                    owner = requestor,
                                    pageable = pageable
                                )
                            } returns listOf(notes).toSlice(pageable)
                            every { noteMapper.toListResControllerDto(notes) } returns data
                            every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                        }

                        it("노트 목록을 조회하고, 200 OK를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me")
                                    .param("sort", "$validSortKey,asc")
                            )
                                .andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                            verify(exactly = 1) {
                                userContext.uid
                                noteService.getAllByOwner(owner = requestor, pageable = pageable)
                                noteMapper.toListResControllerDto(notes)
                                sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable))
                            }
                        }
                    }

                    context("잘못된 정렬 키가 주어지면") {
                        val invalidSortKey = "invalidKey"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me")
                                    .param("sort", "$invalidSortKey,asc")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }
            }

            describe("NoteApiController.getMyNoteByExternalId") {
                context("올바른 externalId가 주어지면") {
                    val validExternalId = UUID.randomUUID()
                    val requestor = UUID.randomUUID().toString()
                    val serviceRes = mockk<NoteResServiceDto>()
                    val expected = createTestNoteResControllerDto(externalId = validExternalId, owner = requestor)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteService.getOwnedByExternalId(
                                externalId = validExternalId,
                                requestor = requestor
                            )
                        } returns serviceRes
                        every { noteMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("노트를 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/note/me/{externalId}", validExternalId))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/getMyNoteByExternalId",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 조회")
                                            .pathParameters(
                                                parameterWithName("externalId").description("조회할 노트의 외부 ID (UUID)")
                                            )
                                            .responseFields(
                                                *NoteDocsSchema.noteResponseSchema
                                            )
                                            .responseSchema(Schema("NoteResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteService.getOwnedByExternalId(
                                externalId = validExternalId,
                                requestor = requestor
                            )
                            noteMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/note/me/{externalId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

            describe("NoteApiController.searchMyNotes") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val query = "테스트 검색"
                    val highlightTag = "highlight"
                    val defaultPageable = createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                    val noteIndex = mockk<NoteIndexResServiceDto>()
                    val data = createTestNoteIndexResControllerDto(owner = requestor)
                    val expected = listOf(data).toSliceResponse(defaultPageable)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteIndexMapper.toNoteSearchCondition(
                                condition = any(),
                                owner = requestor,
                                pageable = defaultPageable
                            )
                        } returns mockk()
                        every {
                            noteIndexService.searchByCondition(searchCondition = any())
                        } returns listOf(noteIndex).toSlice(defaultPageable)
                        every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                        every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                    }

                    it("노트를 검색하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            get("$apiPrefix/notes/me/search")
                                .param("query", query)
                                .param("highlightTag", highlightTag)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/searchMyNotes",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 검색")
                                            .queryParameters(
                                                parameterWithName("query").description("검색 쿼리 (최대 50자, 한글/영어/숫자/공백/와일드카드(*) 허용)"),
                                                parameterWithName("highlightTag").description("하이라이트 태그 (최대 20자, 영문자만 허용)"),
                                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)")
                                                    .optional(),
                                                parameterWithName("size").description("페이지 크기 (최소 1, 최대 100, 기본값 20)")
                                                    .optional(),
                                                parameterWithName("sort").description("정렬 기준 및 방향 (기본값 createdDate,desc)")
                                                    .optional()
                                            )
                                            .responseFields(
                                                *NoteDocsSchema.noteIndexSliceResponseSchema
                                            )
                                            .responseSchema(Schema("NoteIndexSliceResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteIndexMapper.toNoteSearchCondition(
                                condition = any(),
                                owner = requestor,
                                pageable = defaultPageable
                            )
                            noteIndexService.searchByCondition(searchCondition = any())
                            noteIndexMapper.toResControllerDto(noteIndex)
                            sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable))
                        }
                    }
                }

                describe("query 검증 테스트") {
                    context("query가 빈 문자열이면") {
                        val highlightTag = "highlight"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me/search")
                                    .param("query", "")
                                    .param("highlightTag", highlightTag)
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("query가 공백만 있으면") {
                        val highlightTag = "highlight"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me/search")
                                    .param("query", "   ")
                                    .param("highlightTag", highlightTag)
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("query가 50자를 초과하면") {
                        val longQuery = "a".repeat(51)
                        val highlightTag = "highlight"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me/search")
                                    .param("query", longQuery)
                                    .param("highlightTag", highlightTag)
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("query에 특수문자가 포함되면") {
                        val invalidQuery = "test@query"
                        val highlightTag = "highlight"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me/search")
                                    .param("query", invalidQuery)
                                    .param("highlightTag", highlightTag)
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }

                    context("query가 와일드카드(*)만 있으면") {
                        val requestor = UUID.randomUUID().toString()
                        val wildcardQuery = "*"
                        val highlightTag = "highlight"
                        val defaultPageable =
                            createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                        val noteIndex = mockk<NoteIndexResServiceDto>()
                        val data = createTestNoteIndexResControllerDto(owner = requestor)
                        val expected = listOf(data).toSliceResponse(defaultPageable)

                        beforeTest {
                            every { userContext.uid } returns requestor
                            every {
                                noteIndexMapper.toNoteSearchCondition(
                                    condition = any(),
                                    owner = requestor,
                                    pageable = defaultPageable
                                )
                            } returns mockk()
                            every {
                                noteIndexService.searchByCondition(searchCondition = any())
                            } returns listOf(noteIndex).toSlice(defaultPageable)
                            every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                            every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                        }

                        it("200 OK를 응답한다 (와일드카드는 허용됨)") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me/search")
                                    .param("query", wildcardQuery)
                                    .param("highlightTag", highlightTag)
                            )
                                .andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                        }
                    }

                    context("query가 한글, 영어, 숫자, 공백 조합이면") {
                        val requestor = UUID.randomUUID().toString()
                        val validQuery = "한글 test 123"
                        val highlightTag = "highlight"
                        val defaultPageable =
                            createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                        val noteIndex = mockk<NoteIndexResServiceDto>()
                        val data = createTestNoteIndexResControllerDto(owner = requestor)
                        val expected = listOf(data).toSliceResponse(defaultPageable)

                        beforeTest {
                            every { userContext.uid } returns requestor
                            every {
                                noteIndexMapper.toNoteSearchCondition(
                                    condition = any(),
                                    owner = requestor,
                                    pageable = defaultPageable
                                )
                            } returns mockk()
                            every {
                                noteIndexService.searchByCondition(searchCondition = any())
                            } returns listOf(noteIndex).toSlice(defaultPageable)
                            every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                            every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                        }

                        it("200 OK를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/notes/me/search")
                                    .param("query", validQuery)
                                    .param("highlightTag", highlightTag)
                            )
                                .andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                        }
                    }
                }

                describe("highlightTag 검증 테스트") {
                    describe("성공케이스") {
                        context("highlightTag가 영문자만 있으면") {
                            val requestor = UUID.randomUUID().toString()
                            val query = "test"
                            val validTag = "highlight"
                            val defaultPageable =
                                createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(defaultPageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteSearchCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = defaultPageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.searchByCondition(searchCondition = any())
                                } returns listOf(noteIndex).toSlice(defaultPageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", validTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("highlightTag가 빈 문자열이면") {
                            val query = "test"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", "")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag가 20자를 초과하면") {
                            val query = "test"
                            val longTag = "a".repeat(21)

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", longTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag에 숫자가 포함되면") {
                            val query = "test"
                            val invalidTag = "highlight123"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", invalidTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag에 특수문자가 포함되면") {
                            val query = "test"
                            val invalidTag = "high-light"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", invalidTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag에 한글이 포함되면") {
                            val query = "test"
                            val invalidTag = "하이라이트"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", invalidTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("페이징 및 정렬 테스트") {
                    describe("성공케이스") {
                        context("올바른 페이징 파라미터가 주어지면") {
                            val requestor = UUID.randomUUID().toString()
                            val query = "test"
                            val highlightTag = "highlight"
                            val validSize = 50
                            val pageable = createPageable(
                                sortKey = "createdDate",
                                sortDirection = Sort.Direction.DESC,
                                size = validSize
                            )
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(pageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteSearchCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = pageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.searchByCondition(searchCondition = any())
                                } returns listOf(noteIndex).toSlice(pageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", highlightTag)
                                        .param("size", validSize.toString())
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }

                    }

                    describe("실패케이스") {
                        context("사이즈가 100보다 크면") {
                            val query = "test"
                            val highlightTag = "highlight"
                            val invalidSize = 101

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", highlightTag)
                                        .param("size", invalidSize.toString())
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("잘못된 정렬 키가 주어지면") {
                            val query = "test"
                            val highlightTag = "highlight"
                            val invalidSortKey = "invalidKey"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/search")
                                        .param("query", query)
                                        .param("highlightTag", highlightTag)
                                        .param("sort", "$invalidSortKey,asc")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("NoteApiController.filterMyNotes") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val highlightTag = "highlight"
                    val defaultPageable = createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                    val noteIndex = mockk<NoteIndexResServiceDto>()
                    val data = createTestNoteIndexResControllerDto(owner = requestor)
                    val expected = listOf(data).toSliceResponse(defaultPageable)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteIndexMapper.toNoteFilterCondition(
                                condition = any(),
                                owner = requestor,
                                pageable = defaultPageable
                            )
                        } returns mockk()
                        every {
                            noteIndexService.filterByCondition(condition = any())
                        } returns listOf(noteIndex).toSlice(defaultPageable)
                        every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                        every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                    }

                    it("노트를 필터링하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            get("$apiPrefix/notes/me/filter")
                                .param("highlightTag", highlightTag)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/filterMyNotes",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 필터링")
                                            .queryParameters(
                                                parameterWithName("highlightTag").description("하이라이트 태그 (최대 20자, 영문자만 허용)"),
                                                parameterWithName("fieldOptions").description("필드 옵션 맵").optional(),
                                                parameterWithName("createdDateStart").description("생성일 시작 (ISO 8601 형식)")
                                                    .optional(),
                                                parameterWithName("createdDateEnd").description("생성일 종료 (ISO 8601 형식)")
                                                    .optional(),
                                                parameterWithName("modifiedDateStart").description("수정일 시작 (ISO 8601 형식)")
                                                    .optional(),
                                                parameterWithName("modifiedDateEnd").description("수정일 종료 (ISO 8601 형식)")
                                                    .optional(),
                                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)")
                                                    .optional(),
                                                parameterWithName("size").description("페이지 크기 (최소 1, 최대 100, 기본값 20)")
                                                    .optional(),
                                                parameterWithName("sort").description("정렬 기준 및 방향 (기본값 createdDate,desc)")
                                                    .optional()
                                            )
                                            .responseFields(
                                                *NoteDocsSchema.noteIndexSliceResponseSchema
                                            )
                                            .responseSchema(Schema("NoteIndexSliceResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteIndexMapper.toNoteFilterCondition(
                                condition = any(),
                                owner = requestor,
                                pageable = defaultPageable
                            )
                            noteIndexService.filterByCondition(condition = any())
                            noteIndexMapper.toResControllerDto(noteIndex)
                            sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable))
                        }
                    }
                }

                describe("highlightTag 검증 테스트") {
                    describe("성공케이스") {
                        context("highlightTag가 영문자만 있으면") {
                            val requestor = UUID.randomUUID().toString()
                            val validTag = "highlight"
                            val defaultPageable =
                                createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(defaultPageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteFilterCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = defaultPageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.filterByCondition(condition = any())
                                } returns listOf(noteIndex).toSlice(defaultPageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", validTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("highlightTag가 빈 문자열이면") {
                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", "")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag가 20자를 초과하면") {
                            val longTag = "a".repeat(21)

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", longTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag에 숫자가 포함되면") {
                            val invalidTag = "highlight123"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", invalidTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag에 특수문자가 포함되면") {
                            val invalidTag = "high-light"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", invalidTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("highlightTag에 한글이 포함되면") {
                            val invalidTag = "하이라이트"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", invalidTag)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("날짜 범위 검증 테스트") {
                    describe("성공케이스") {
                        context("createdDate 범위가 올바르면") {
                            val requestor = UUID.randomUUID().toString()
                            val highlightTag = "highlight"
                            val createdDateStart = "2024-01-01T00:00:00"
                            val createdDateEnd = "2024-12-31T23:59:59"
                            val defaultPageable =
                                createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(defaultPageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteFilterCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = defaultPageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.filterByCondition(condition = any())
                                } returns listOf(noteIndex).toSlice(defaultPageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("createdDateStart", createdDateStart)
                                        .param("createdDateEnd", createdDateEnd)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }

                        context("modifiedDate 범위가 올바르면") {
                            val requestor = UUID.randomUUID().toString()
                            val highlightTag = "highlight"
                            val modifiedDateStart = "2024-01-01T00:00:00"
                            val modifiedDateEnd = "2024-12-31T23:59:59"
                            val defaultPageable =
                                createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(defaultPageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteFilterCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = defaultPageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.filterByCondition(condition = any())
                                } returns listOf(noteIndex).toSlice(defaultPageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("modifiedDateStart", modifiedDateStart)
                                        .param("modifiedDateEnd", modifiedDateEnd)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }

                        context("createdDateStart만 있으면") {
                            val requestor = UUID.randomUUID().toString()
                            val highlightTag = "highlight"
                            val createdDateStart = "2024-01-01T00:00:00"
                            val defaultPageable =
                                createPageable(sortKey = "createdDate", sortDirection = Sort.Direction.DESC)
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(defaultPageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteFilterCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = defaultPageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.filterByCondition(condition = any())
                                } returns listOf(noteIndex).toSlice(defaultPageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("createdDateStart", createdDateStart)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("createdDateStart가 createdDateEnd보다 이후면") {
                            val highlightTag = "highlight"
                            val createdDateStart = "2024-12-31T23:59:59"
                            val createdDateEnd = "2024-01-01T00:00:00"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("createdDateStart", createdDateStart)
                                        .param("createdDateEnd", createdDateEnd)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("modifiedDateStart가 modifiedDateEnd보다 이후면") {
                            val highlightTag = "highlight"
                            val modifiedDateStart = "2024-12-31T23:59:59"
                            val modifiedDateEnd = "2024-01-01T00:00:00"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("modifiedDateStart", modifiedDateStart)
                                        .param("modifiedDateEnd", modifiedDateEnd)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("페이징 및 정렬 테스트") {
                    describe("성공케이스") {
                        context("올바른 페이징 파라미터가 주어지면") {
                            val requestor = UUID.randomUUID().toString()
                            val highlightTag = "highlight"
                            val validSize = 50
                            val pageable = createPageable(
                                sortKey = "createdDate",
                                sortDirection = Sort.Direction.DESC,
                                size = validSize
                            )
                            val noteIndex = mockk<NoteIndexResServiceDto>()
                            val data = createTestNoteIndexResControllerDto(owner = requestor)
                            val expected = listOf(data).toSliceResponse(pageable)

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteIndexMapper.toNoteFilterCondition(
                                        condition = any(),
                                        owner = requestor,
                                        pageable = pageable
                                    )
                                } returns mockk()
                                every {
                                    noteIndexService.filterByCondition(condition = any())
                                } returns listOf(noteIndex).toSlice(pageable)
                                every { noteIndexMapper.toResControllerDto(noteIndex) } returns data
                                every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("size", validSize.toString())
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("사이즈가 100보다 크면") {
                            val highlightTag = "highlight"
                            val invalidSize = 101

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("size", invalidSize.toString())
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }



                        context("잘못된 정렬 키가 주어지면") {
                            val highlightTag = "highlight"
                            val invalidSortKey = "invalidKey"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/filter")
                                        .param("highlightTag", highlightTag)
                                        .param("sort", "$invalidSortKey,asc")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("NoteApiController.getMyNotesFieldValuesFacets") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val fieldName = "test-field"
                    val query = "test"
                    val facetWithCount1 =
                        NoteFieldValueFacetWithCountResControllerDto(
                            facet = "value1",
                            count = 5
                        )
                    val facetWithCount2 =
                        NoteFieldValueFacetWithCountResControllerDto(
                            facet = "value2",
                            count = 10
                        )
                    val expected =
                        NoteFieldValueFacetListResControllerDto(
                            fieldName = fieldName,
                            facets = listOf(facetWithCount1, facetWithCount2)
                        )

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteFieldIndexService.searchNoteFieldValueFacets(
                                query = query,
                                fieldName = fieldName,
                                owner = requestor
                            )
                        } returns listOf(mockk(), mockk())
                        every {
                            noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(any())
                        } returnsMany listOf(facetWithCount1, facetWithCount2)
                        every {
                            noteFieldIndexMapper.toNoteFieldValueListResControllerDto(
                                facets = listOf(facetWithCount1, facetWithCount2),
                                fieldName = fieldName
                            )
                        } returns expected
                    }

                    it("필드 값 패싯을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                .param("query", query)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/getMyNotesFieldValuesFacets",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트의 특정 필드 값 패싯 검색")
                                            .pathParameters(
                                                parameterWithName("fieldName").description("필드 이름 (최대 50자, 한글/영어/숫자/언더바/하이픈/공백 허용)")
                                            )
                                            .queryParameters(
                                                parameterWithName("query").description("검색 쿼리 (최대 100자, 한글/영어/숫자/하이픈/언더바 허용)")
                                            )
                                            .responseFields(
                                                fieldWithPath("field_name").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 이름"),
                                                fieldWithPath("facets[]").description("패싯 목록"),
                                                fieldWithPath("facets[].facet").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("패싯 값"),
                                                fieldWithPath("facets[].count").type(com.epages.restdocs.apispec.SimpleType.NUMBER)
                                                    .description("패싯 카운트")
                                            )
                                            .responseSchema(Schema("NoteFieldValueFacetListResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteFieldIndexService.searchNoteFieldValueFacets(
                                query = query,
                                fieldName = fieldName,
                                owner = requestor
                            )
                            noteFieldIndexMapper.toNoteFieldValueListResControllerDto(
                                facets = listOf(facetWithCount1, facetWithCount2),
                                fieldName = fieldName
                            )
                        }
                    }
                }

                describe("fieldName 검증 테스트") {
                    describe("성공케이스") {
                        context("fieldName이 한글, 영어, 숫자, 언더바, 하이픈, 공백 조합이면") {
                            val requestor = UUID.randomUUID().toString()
                            val validFieldName = "테스트 필드_name-01"
                            val query = "test"
                            val facetWithCount =
                                NoteFieldValueFacetWithCountResControllerDto(
                                    facet = "value1",
                                    count = 5
                                )
                            val expected =
                                NoteFieldValueFacetListResControllerDto(
                                    fieldName = validFieldName,
                                    facets = listOf(facetWithCount)
                                )

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteFieldIndexService.searchNoteFieldValueFacets(
                                        query = query,
                                        fieldName = validFieldName,
                                        owner = requestor
                                    )
                                } returns listOf(mockk())
                                every {
                                    noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(any())
                                } returns facetWithCount
                                every {
                                    noteFieldIndexMapper.toNoteFieldValueListResControllerDto(
                                        facets = listOf(facetWithCount),
                                        fieldName = validFieldName
                                    )
                                } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", validFieldName)
                                        .param("query", query)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {


                        context("fieldName이 50자를 초과하면") {
                            val longFieldName = "a".repeat(51)
                            val query = "test"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", longFieldName)
                                        .param("query", query)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("fieldName에 허용되지 않는 특수문자가 포함되면") {
                            val invalidFieldName = "field@name"
                            val query = "test"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", invalidFieldName)
                                        .param("query", query)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("query 검증 테스트") {
                    describe("성공케이스") {
                        context("query가 와일드카드(*)만 있으면") {
                            val requestor = UUID.randomUUID().toString()
                            val fieldName = "test-field"
                            val wildcardQuery = "*"
                            val facetWithCount =
                                NoteFieldValueFacetWithCountResControllerDto(
                                    facet = "value1",
                                    count = 5
                                )
                            val expected =
                                NoteFieldValueFacetListResControllerDto(
                                    fieldName = fieldName,
                                    facets = listOf(facetWithCount)
                                )

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteFieldIndexService.searchNoteFieldValueFacets(
                                        query = wildcardQuery,
                                        fieldName = fieldName,
                                        owner = requestor
                                    )
                                } returns listOf(mockk())
                                every {
                                    noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(any())
                                } returns facetWithCount
                                every {
                                    noteFieldIndexMapper.toNoteFieldValueListResControllerDto(
                                        facets = listOf(facetWithCount),
                                        fieldName = fieldName
                                    )
                                } returns expected
                            }

                            it("200 OK를 응답한다 (와일드카드는 허용됨)") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", wildcardQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }

                        context("query가 한글, 영어, 숫자, 하이픈, 언더바 조합이면") {
                            val requestor = UUID.randomUUID().toString()
                            val fieldName = "test-field"
                            val validQuery = "한글test123-_"
                            val facetWithCount =
                                NoteFieldValueFacetWithCountResControllerDto(
                                    facet = "value1",
                                    count = 5
                                )
                            val expected =
                                NoteFieldValueFacetListResControllerDto(
                                    fieldName = fieldName,
                                    facets = listOf(facetWithCount)
                                )

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteFieldIndexService.searchNoteFieldValueFacets(
                                        query = validQuery,
                                        fieldName = fieldName,
                                        owner = requestor
                                    )
                                } returns listOf(mockk())
                                every {
                                    noteFieldIndexMapper.toNoteFieldValueFacetWithCountResControllerDto(any())
                                } returns facetWithCount
                                every {
                                    noteFieldIndexMapper.toNoteFieldValueListResControllerDto(
                                        facets = listOf(facetWithCount),
                                        fieldName = fieldName
                                    )
                                } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", validQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("query가 빈 문자열이면") {
                            val fieldName = "test-field"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", "")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query가 공백만 있으면") {
                            val fieldName = "test-field"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", "   ")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query가 100자를 초과하면") {
                            val fieldName = "test-field"
                            val longQuery = "a".repeat(101)

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", longQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query에 허용되지 않는 특수문자가 포함되면") {
                            val fieldName = "test-field"
                            val invalidQuery = "test@value"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", invalidQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query에 공백이 포함되면") {
                            val fieldName = "test-field"
                            val invalidQuery = "test value"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/{fieldName}/values/search", fieldName)
                                        .param("query", invalidQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("NoteApiController.getMyNotesFieldFacets") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val query = "test"
                    val facetWithCount1 =
                        com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto(
                            facet = "field1",
                            count = 3
                        )
                    val facetWithCount2 =
                        com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto(
                            facet = "field2",
                            count = 7
                        )
                    val expected =
                        com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto(
                            facets = listOf(facetWithCount1, facetWithCount2)
                        )

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteFieldIndexService.searchNoteFieldNameFacets(
                                query = query,
                                owner = requestor
                            )
                        } returns listOf(mockk(), mockk())
                        every {
                            noteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto(any())
                        } returnsMany listOf(facetWithCount1, facetWithCount2)
                        every {
                            noteFieldIndexMapper.toNoteFieldNameListResControllerDto(
                                facets = listOf(facetWithCount1, facetWithCount2)
                            )
                        } returns expected
                    }

                    it("필드 이름 패싯을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            get("$apiPrefix/notes/me/facets/fields/search")
                                .param("query", query)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/getMyNotesFieldFacets",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트의 필드 이름 패싯 검색")
                                            .queryParameters(
                                                parameterWithName("query").description("검색 쿼리 (최대 50자, 한글/영어/숫자 또는 와일드카드(*) 허용, 공백/하이픈/언더바 불허)")
                                            )
                                            .responseFields(
                                                fieldWithPath("facets[]").description("필드 이름 패싯 목록"),
                                                fieldWithPath("facets[].facet").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 이름"),
                                                fieldWithPath("facets[].count").type(com.epages.restdocs.apispec.SimpleType.NUMBER)
                                                    .description("필드 카운트")
                                            )
                                            .responseSchema(Schema("NoteFieldNameFacetListResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteFieldIndexService.searchNoteFieldNameFacets(
                                query = query,
                                owner = requestor
                            )
                            noteFieldIndexMapper.toNoteFieldNameListResControllerDto(
                                facets = listOf(facetWithCount1, facetWithCount2)
                            )
                        }
                    }
                }

                describe("query 검증 테스트") {
                    describe("성공케이스") {
                        context("query가 와일드카드(*)만 있으면") {
                            val requestor = UUID.randomUUID().toString()
                            val wildcardQuery = "*"
                            val facetWithCount =
                                com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto(
                                    facet = "field1",
                                    count = 5
                                )
                            val expected =
                                com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto(
                                    facets = listOf(facetWithCount)
                                )

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteFieldIndexService.searchNoteFieldNameFacets(
                                        query = wildcardQuery,
                                        owner = requestor
                                    )
                                } returns listOf(mockk())
                                every {
                                    noteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto(any())
                                } returns facetWithCount
                                every {
                                    noteFieldIndexMapper.toNoteFieldNameListResControllerDto(
                                        facets = listOf(facetWithCount)
                                    )
                                } returns expected
                            }

                            it("200 OK를 응답한다 (와일드카드는 허용됨)") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", wildcardQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }

                        context("query가 한글만 있으면") {
                            val requestor = UUID.randomUUID().toString()
                            val koreanQuery = "한글"
                            val facetWithCount =
                                com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto(
                                    facet = "field1",
                                    count = 5
                                )
                            val expected =
                                com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto(
                                    facets = listOf(facetWithCount)
                                )

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteFieldIndexService.searchNoteFieldNameFacets(
                                        query = koreanQuery,
                                        owner = requestor
                                    )
                                } returns listOf(mockk())
                                every {
                                    noteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto(any())
                                } returns facetWithCount
                                every {
                                    noteFieldIndexMapper.toNoteFieldNameListResControllerDto(
                                        facets = listOf(facetWithCount)
                                    )
                                } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", koreanQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }

                        context("query가 한글, 영어, 숫자 조합이면") {
                            val requestor = UUID.randomUUID().toString()
                            val validQuery = "한글test123"
                            val facetWithCount =
                                com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetWithCountResControllerDto(
                                    facet = "field1",
                                    count = 5
                                )
                            val expected =
                                com.gabinote.coffeenote.note.dto.noteFieldIndex.controller.NoteFieldNameFacetListResControllerDto(
                                    facets = listOf(facetWithCount)
                                )

                            beforeTest {
                                every { userContext.uid } returns requestor
                                every {
                                    noteFieldIndexService.searchNoteFieldNameFacets(
                                        query = validQuery,
                                        owner = requestor
                                    )
                                } returns listOf(mockk())
                                every {
                                    noteFieldIndexMapper.toNoteFieldNameFacetWithCountResControllerDto(any())
                                } returns facetWithCount
                                every {
                                    noteFieldIndexMapper.toNoteFieldNameListResControllerDto(
                                        facets = listOf(facetWithCount)
                                    )
                                } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", validQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("query가 빈 문자열이면") {
                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", "")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query가 공백만 있으면") {
                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", "   ")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query가 50자를 초과하면") {
                            val longQuery = "a".repeat(51)

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", longQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query에 공백이 포함되면") {
                            val invalidQuery = "test field"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", invalidQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query에 하이픈이 포함되면") {
                            val invalidQuery = "test-field"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", invalidQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query에 언더바가 포함되면") {
                            val invalidQuery = "test_field"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", invalidQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query에 특수문자가 포함되면") {
                            val invalidQuery = "test@field"

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                        .param("query", invalidQuery)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("query 파라미터가 없으면") {
                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/notes/me/facets/fields/search")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("NoteApiController.createMyNote") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val fieldId = UUID.randomUUID().toString()
                    val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                        title = "테스트 노트",
                        thumbnail = null,
                        fields = listOf(
                            NoteFieldCreateReqControllerDto(
                                id = fieldId,
                                name = "필드명",
                                icon = "icon",
                                type = TestFieldType,
                                attributes = emptySet(),
                                order = 0,
                                isDisplay = true,
                                values = setOf("값1", "값2")
                            )
                        ),
                        isOpen = false
                    )
                    val serviceRes = mockk<NoteResServiceDto>()
                    val expected = createTestNoteResControllerDto(owner = requestor)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every { noteMapper.toCreateReqServiceDto(dto = any(), owner = requestor) } returns mockk()
                        every { noteService.create(any()) } returns serviceRes
                        every { noteMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("노트를 생성하고, 201 Created를 응답한다") {
                        mockMvc.perform(
                            post("$apiPrefix/note/me")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        )
                            .andDo(print())
                            .andExpect(status().isCreated)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/createMyNote",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 생성")
                                            .requestFields(
                                                fieldWithPath("title").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("노트 제목 (최대 100자, 필수)"),
                                                fieldWithPath("thumbnail").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("썸네일 URL (최대 36자, 선택)").optional(),
                                                fieldWithPath("is_open").type(com.epages.restdocs.apispec.SimpleType.BOOLEAN)
                                                    .description("공개 여부"),
                                                fieldWithPath("fields[]").description("필드 목록 (최소 1개, 최대 50개)"),
                                                fieldWithPath("fields[].id").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 ID (UUID 형식)"),
                                                fieldWithPath("fields[].name").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 이름"),
                                                fieldWithPath("fields[].icon").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 아이콘"),
                                                fieldWithPath("fields[].type").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 타입"),
                                                fieldWithPath("fields[].attributes[]").description("필드 속성 목록")
                                                    .optional(),
                                                fieldWithPath("fields[].order").type(com.epages.restdocs.apispec.SimpleType.NUMBER)
                                                    .description("필드 순서"),
                                                fieldWithPath("fields[].is_display").type(com.epages.restdocs.apispec.SimpleType.BOOLEAN)
                                                    .description("필드 표시 여부"),
                                                fieldWithPath("fields[].values[]").description("필드 값 목록").optional()
                                            )
                                            .responseFields(*NoteDocsSchema.noteResponseSchema)
                                            .responseSchema(Schema("NoteResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteMapper.toCreateReqServiceDto(dto = any(), owner = requestor)
                            noteService.create(any())
                            noteMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                describe("title 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val fieldId = UUID.randomUUID().toString()
                    val validField =
                        NoteFieldCreateReqControllerDto(
                            id = fieldId,
                            name = "필드명",
                            icon = "icon",
                            type = TestFieldType,
                            attributes = emptySet(),
                            order = 0,
                            isDisplay = true,
                            values = setOf("값1")
                        )

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("실패케이스") {
                        context("title이 빈 문자열이면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("title이 공백만 있으면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "   ",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("title이 100자를 초과하면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "a".repeat(101),
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("thumbnail 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val fieldId = UUID.randomUUID().toString()
                    val validField =
                        NoteFieldCreateReqControllerDto(
                            id = fieldId,
                            name = "필드명",
                            icon = "icon",
                            type = TestFieldType,
                            attributes = emptySet(),
                            order = 0,
                            isDisplay = true,
                            values = setOf("값1")
                        )

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("성공케이스") {
                        context("thumbnail이 null이면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toCreateReqServiceDto(
                                        dto = any(),
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.create(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("201 Created를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isCreated)
                            }
                        }

                        context("thumbnail이 36자 이하면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = "a".repeat(36),
                                fields = listOf(validField),
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toCreateReqServiceDto(
                                        dto = any(),
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.create(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("201 Created를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isCreated)
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("thumbnail이 36자를 초과하면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = "a".repeat(37),
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("fields 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val fieldId = UUID.randomUUID().toString()
                    val validField =
                        NoteFieldCreateReqControllerDto(
                            id = fieldId,
                            name = "필드명",
                            icon = "icon",
                            type = TestFieldType,
                            attributes = emptySet(),
                            order = 0,
                            isDisplay = true,
                            values = setOf("값1")
                        )

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("성공케이스") {
                        context("fields가 1개면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toCreateReqServiceDto(
                                        dto = any(),
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.create(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("201 Created를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isCreated)
                            }
                        }

                        context("fields가 50개면") {
                            val fields = (1..50).map {
                                NoteFieldCreateReqControllerDto(
                                    id = UUID.randomUUID().toString(),
                                    name = "필드$it",
                                    icon = "icon",
                                    type = TestFieldType,
                                    attributes = emptySet(),
                                    order = it,
                                    isDisplay = true,
                                    values = setOf("값$it")
                                )
                            }
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = fields,
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toCreateReqServiceDto(
                                        dto = any(),
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.create(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("201 Created를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isCreated)
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("fields가 비어있으면") {
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = emptyList(),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("fields가 50개를 초과하면") {
                            val fields = (1..51).map {
                                NoteFieldCreateReqControllerDto(
                                    id = UUID.randomUUID().toString(),
                                    name = "필드$it",
                                    icon = "icon",
                                    type = TestFieldType,
                                    attributes = emptySet(),
                                    order = it,
                                    isDisplay = true,
                                    values = setOf("값$it")
                                )
                            }
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = fields,
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("field.id 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("실패케이스") {
                        context("field.id가 빈 문자열이면") {
                            val invalidField =
                                NoteFieldCreateReqControllerDto(
                                    id = "",
                                    name = "필드명",
                                    icon = "icon",
                                    type = TestFieldType,
                                    attributes = emptySet(),
                                    order = 0,
                                    isDisplay = true,
                                    values = setOf("값1")
                                )
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = listOf(invalidField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("field.id가 UUID 형식이 아니면") {
                            val invalidField =
                                NoteFieldCreateReqControllerDto(
                                    id = "not-a-uuid",
                                    name = "필드명",
                                    icon = "icon",
                                    type = TestFieldType,
                                    attributes = emptySet(),
                                    order = 0,
                                    isDisplay = true,
                                    values = setOf("값1")
                                )
                            val request = com.gabinote.coffeenote.note.dto.note.controller.NoteCreateReqControllerDto(
                                title = "테스트 노트",
                                thumbnail = null,
                                fields = listOf(invalidField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/note/me")
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("NoteApiController.updateMyNoteByExternalId") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val externalId = UUID.randomUUID()
                    val fieldId = UUID.randomUUID().toString()
                    val request = NoteUpdateReqControllerDto(
                        title = "수정된 노트",
                        thumbnail = null,
                        fields = listOf(
                            NoteFieldCreateReqControllerDto(
                                id = fieldId,
                                name = "수정된 필드",
                                icon = "icon",
                                type = TestFieldType,
                                attributes = emptySet(),
                                order = 0,
                                isDisplay = true,
                                values = setOf("수정값1", "수정값2")
                            )
                        ),
                        isOpen = true
                    )
                    val serviceRes = mockk<NoteResServiceDto>()
                    val expected = createTestNoteResControllerDto(externalId = externalId, owner = requestor)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteMapper.toUpdateReqServiceDto(
                                dto = any(),
                                externalId = externalId,
                                owner = requestor
                            )
                        } returns mockk()
                        every { noteService.update(any()) } returns serviceRes
                        every { noteMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("노트를 수정하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            put("$apiPrefix/note/me/{externalId}", externalId)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/updateMyNoteByExternalId",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 수정")
                                            .pathParameters(
                                                parameterWithName("externalId").description("수정할 노트의 외부 ID (UUID)")
                                            )
                                            .requestFields(
                                                fieldWithPath("title").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("노트 제목 (최대 100자, 필수)"),
                                                fieldWithPath("thumbnail").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("썸네일 URL (최대 36자, 선택)").optional(),
                                                fieldWithPath("is_open").type(com.epages.restdocs.apispec.SimpleType.BOOLEAN)
                                                    .description("공개 여부"),
                                                fieldWithPath("fields[]").description("필드 목록 (최소 1개, 최대 50개)"),
                                                fieldWithPath("fields[].id").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 ID (UUID 형식)"),
                                                fieldWithPath("fields[].name").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 이름"),
                                                fieldWithPath("fields[].icon").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 아이콘"),
                                                fieldWithPath("fields[].type").type(com.epages.restdocs.apispec.SimpleType.STRING)
                                                    .description("필드 타입"),
                                                fieldWithPath("fields[].attributes[]").description("필드 속성 목록")
                                                    .optional(),
                                                fieldWithPath("fields[].order").type(com.epages.restdocs.apispec.SimpleType.NUMBER)
                                                    .description("필드 순서"),
                                                fieldWithPath("fields[].is_display").type(com.epages.restdocs.apispec.SimpleType.BOOLEAN)
                                                    .description("필드 표시 여부"),
                                                fieldWithPath("fields[].values[]").description("필드 값 목록").optional()
                                            )
                                            .responseFields(*NoteDocsSchema.noteResponseSchema)
                                            .responseSchema(Schema("NoteResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteMapper.toUpdateReqServiceDto(dto = any(), externalId = externalId, owner = requestor)
                            noteService.update(any())
                            noteMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"
                    val fieldId = UUID.randomUUID().toString()
                    val request = NoteUpdateReqControllerDto(
                        title = "수정된 노트",
                        thumbnail = null,
                        fields = listOf(
                            NoteFieldCreateReqControllerDto(
                                id = fieldId,
                                name = "필드명",
                                icon = "icon",
                                type = TestFieldType,
                                attributes = emptySet(),
                                order = 0,
                                isDisplay = true,
                                values = setOf("값1")
                            )
                        ),
                        isOpen = false
                    )

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(
                            put("$apiPrefix/note/me/{externalId}", invalidId)
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        )
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }

                describe("title 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val externalId = UUID.randomUUID()
                    val fieldId = UUID.randomUUID().toString()
                    val validField = NoteFieldCreateReqControllerDto(
                        id = fieldId,
                        name = "필드명",
                        icon = "icon",
                        type = TestFieldType,
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = true,
                        values = setOf("값1")
                    )

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("실패케이스") {
                        context("title이 빈 문자열이면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("title이 공백만 있으면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "   ",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("title이 100자를 초과하면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "a".repeat(101),
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("thumbnail 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val externalId = UUID.randomUUID()
                    val fieldId = UUID.randomUUID().toString()
                    val validField = NoteFieldCreateReqControllerDto(
                        id = fieldId,
                        name = "필드명",
                        icon = "icon",
                        type = TestFieldType,
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = true,
                        values = setOf("값1")
                    )

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("성공케이스") {
                        context("thumbnail이 null이면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(externalId = externalId, owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toUpdateReqServiceDto(
                                        dto = any(),
                                        externalId = externalId,
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.update(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                            }
                        }

                        context("thumbnail이 36자 이하면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = "a".repeat(36),
                                fields = listOf(validField),
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(externalId = externalId, owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toUpdateReqServiceDto(
                                        dto = any(),
                                        externalId = externalId,
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.update(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("thumbnail이 36자를 초과하면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = "a".repeat(37),
                                fields = listOf(validField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("fields 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val externalId = UUID.randomUUID()
                    val fieldId = UUID.randomUUID().toString()
                    val validField = NoteFieldCreateReqControllerDto(
                        id = fieldId,
                        name = "필드명",
                        icon = "icon",
                        type = TestFieldType,
                        attributes = emptySet(),
                        order = 0,
                        isDisplay = true,
                        values = setOf("값1")
                    )

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("성공케이스") {
                        context("fields가 1개면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = null,
                                fields = listOf(validField),
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(externalId = externalId, owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toUpdateReqServiceDto(
                                        dto = any(),
                                        externalId = externalId,
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.update(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                            }
                        }

                        context("fields가 50개면") {
                            val fields = (1..50).map {
                                NoteFieldCreateReqControllerDto(
                                    id = UUID.randomUUID().toString(),
                                    name = "필드$it",
                                    icon = "icon",
                                    type = TestFieldType,
                                    attributes = emptySet(),
                                    order = it,
                                    isDisplay = true,
                                    values = setOf("값$it")
                                )
                            }
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = null,
                                fields = fields,
                                isOpen = false
                            )
                            val serviceRes = mockk<NoteResServiceDto>()
                            val expected = createTestNoteResControllerDto(externalId = externalId, owner = requestor)

                            beforeTest {
                                every {
                                    noteMapper.toUpdateReqServiceDto(
                                        dto = any(),
                                        externalId = externalId,
                                        owner = requestor
                                    )
                                } returns mockk()
                                every { noteService.update(any()) } returns serviceRes
                                every { noteMapper.toResControllerDto(serviceRes) } returns expected
                            }

                            it("200 OK를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                            }
                        }
                    }

                    describe("실패케이스") {
                        context("fields가 비어있으면") {
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = null,
                                fields = emptyList(),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                        context("fields가 50개를 초과하면") {
                            val fields = (1..51).map {
                                NoteFieldCreateReqControllerDto(
                                    id = UUID.randomUUID().toString(),
                                    name = "필드$it",
                                    icon = "icon",
                                    type = TestFieldType,
                                    attributes = emptySet(),
                                    order = it,
                                    isDisplay = true,
                                    values = setOf("값$it")
                                )
                            }
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = null,
                                fields = fields,
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("field.id 검증 테스트") {
                    val requestor = UUID.randomUUID().toString()
                    val externalId = UUID.randomUUID()

                    beforeTest {
                        every { userContext.uid } returns requestor
                    }

                    describe("실패케이스") {
                        context("field.id가 UUID 형식이 아니면") {
                            val invalidField = NoteFieldCreateReqControllerDto(
                                id = "not-a-uuid",
                                name = "필드명",
                                icon = "icon",
                                type = TestFieldType,
                                attributes = emptySet(),
                                order = 0,
                                isDisplay = true,
                                values = setOf("값1")
                            )
                            val request = NoteUpdateReqControllerDto(
                                title = "수정된 노트",
                                thumbnail = null,
                                fields = listOf(invalidField),
                                isOpen = false
                            )

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/note/me/{externalId}", externalId)
                                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request))
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("NoteApiController.deleteMyNoteByExternalId") {
                context("올바른 externalId가 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val externalId = UUID.randomUUID()

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            noteService.softDeleteByExternalId(
                                externalId = externalId,
                                owner = requestor
                            )
                        } just Runs
                    }

                    it("노트를 삭제하고, 204 No Content를 응답한다") {
                        mockMvc.perform(
                            delete("$apiPrefix/note/me/{externalId}", externalId)
                        )
                            .andDo(print())
                            .andExpect(status().isNoContent)
                            .andDo(
                                document(
                                    "notes/deleteMyNoteByExternalId",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("내 노트 삭제")
                                            .pathParameters(
                                                parameterWithName("externalId").description("삭제할 노트의 외부 ID (UUID)")
                                            )
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            noteService.softDeleteByExternalId(
                                externalId = externalId,
                                owner = requestor
                            )
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(
                            delete("$apiPrefix/note/me/{externalId}", invalidId)
                        )
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

            describe("NoteApiController.getOpenNoteByExternalId") {
                context("올바른 externalId가 주어지면") {
                    val externalId = UUID.randomUUID()
                    val serviceRes = mockk<NoteResServiceDto>()
                    val expected =
                        createTestNoteResControllerDto(externalId = externalId, owner = "public-owner", isOpen = true)

                    beforeTest {
                        every {
                            noteService.getOpenByExternalId(externalId = externalId)
                        } returns serviceRes
                        every { noteMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("공개 노트를 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            get("$apiPrefix/note/open/{externalId}", externalId)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "notes/getOpenNoteByExternalId",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Note")
                                            .description("공개 노트 조회 (인증 불필요)")
                                            .pathParameters(
                                                parameterWithName("externalId").description("조회할 노트의 외부 ID (UUID)")
                                            )
                                            .responseFields(*NoteDocsSchema.noteResponseSchema)
                                            .responseSchema(Schema("NoteResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            noteService.getOpenByExternalId(externalId = externalId)
                            noteMapper.toResControllerDto(serviceRes)
                        }
                        verify(exactly = 0) {
                            userContext.uid
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(
                            get("$apiPrefix/note/open/{externalId}", invalidId)
                        )
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }
        }
    }


}