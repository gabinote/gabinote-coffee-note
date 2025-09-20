package com.gabinote.coffeenote.template.web.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.template.dto.template.controller.TemplateUpdateReqControllerDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateCreateReqServiceDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateResServiceDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateUpdateReqServiceDto
import com.gabinote.coffeenote.template.service.template.strategy.GetTemplateByExternalIdStrategyType
import com.gabinote.coffeenote.testSupport.testDocs.template.TemplateDocsSchema
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateTestDataHelper.createTemplateCreateReqControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateTestDataHelper.createTestTemplateResControllerDto
import com.gabinote.coffeenote.testSupport.testUtil.page.TestPageableUtil.createPageable
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSlice
import com.gabinote.coffeenote.testSupport.testUtil.page.TestSliceUtil.toSliceResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.mockk.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*


@WebMvcTest(controllers = [TemplateApiController::class])
class TemplateApiControllerTest : TemplateControllerTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var userContext: UserContext

    @MockkBean
    private lateinit var sliceMapper: SliceMapper


    private val apiPrefix = "/api/v1"


    init {
        describe("[Template] TemplateApiController Test") {
            describe("TemplateApiController.getDefaultTemplate") {
                context("올바른 externalId가 주어지면") {
                    val validExternalId = UUID.randomUUID()
                    val requestor = UUID.randomUUID().toString()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto(externalId = validExternalId, isDefault = true)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            templateService.getByExternalId(
                                externalId = validExternalId,
                                requestor = requestor,
                                strategyType = GetTemplateByExternalIdStrategyType.DEFAULT
                            )
                        } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/template/default/{externalId}", validExternalId))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/getDefaultTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("기본 템플릿 조회")
                                            .pathParameters(
                                                parameterWithName("externalId").description("조회할 템플릿의 외부 ID (UUID)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateService.getByExternalId(
                                externalId = validExternalId,
                                requestor = requestor,
                                strategyType = GetTemplateByExternalIdStrategyType.DEFAULT
                            )
                            templateMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/template/default/{externalId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

            describe("TemplateApiController.getMyTemplate") {
                context("올바른 externalId가 주어지면") {
                    val validExternalId = UUID.randomUUID()
                    val requestor = UUID.randomUUID().toString()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto(externalId = validExternalId, owner = requestor)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            templateService.getByExternalId(
                                externalId = validExternalId,
                                requestor = requestor,
                                strategyType = GetTemplateByExternalIdStrategyType.OWNED
                            )
                        } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/template/me/{externalId}", validExternalId))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/getMyTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("내 템플릿 조회")
                                            .pathParameters(
                                                parameterWithName("externalId").description("조회할 템플릿의 외부 ID (UUID)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateService.getByExternalId(
                                externalId = validExternalId,
                                requestor = requestor,
                                strategyType = GetTemplateByExternalIdStrategyType.OWNED
                            )
                            templateMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/template/me/{externalId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

            describe("TemplateApiController.getOpenTemplate") {
                context("올바른 externalId가 주어지면") {
                    val validExternalId = UUID.randomUUID()
                    val requestor = UUID.randomUUID().toString()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto(externalId = validExternalId, isOpen = true)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            templateService.getByExternalId(
                                externalId = validExternalId,
                                requestor = requestor,
                                strategyType = GetTemplateByExternalIdStrategyType.OPENED
                            )
                        } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/template/open/{externalId}", validExternalId))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/getOpenTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("공개 템플릿 조회")
                                            .pathParameters(
                                                parameterWithName("externalId").description("조회할 템플릿의 외부 ID (UUID)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateService.getByExternalId(
                                externalId = validExternalId,
                                requestor = requestor,
                                strategyType = GetTemplateByExternalIdStrategyType.OPENED
                            )
                            templateMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/template/open/{externalId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

            describe("TemplateApiController.getAllDefaultTemplates") {
                context("올바른 요청이 주어지면") {
                    // 기본 pageable 값: size = 20, page = 0, sort = name,desc
                    val defaultPageable = createPageable(sortKey = "name", sortDirection = Sort.Direction.DESC)
                    val templates = mockk<TemplateResServiceDto>()
                    val data = createTestTemplateResControllerDto(isDefault = true)
                    val expected = listOf(data).toSliceResponse(defaultPageable)

                    beforeTest {
                        every { templateService.getAllDefault(pageable = defaultPageable) } returns listOf(templates).toSlice(
                            defaultPageable
                        )
                        every { templateMapper.toResControllerDto(templates) } returns data
                        every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                    }

                    it("템플릿들을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/templates/default"))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/getAllDefaultTemplates",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("모든 기본 템플릿 목록 조회")
                                            .queryParameters(
                                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)")
                                                    .optional(),
                                                parameterWithName("size").description("페이지 크기 (최대 100, 기본값 20)")
                                                    .optional(),
                                                parameterWithName("sort").description("정렬 기준 및 방향 (기본값 name,desc)")
                                                    .optional()
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateSliceResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateSliceResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            templateService.getAllDefault(pageable = defaultPageable)
                            templateMapper.toResControllerDto(templates)
                            sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable))
                        }
                    }
                }

                describe("페이징 사이즈 테스트") {
                    context("올바른 사이즈가 주어지면") {
                        val validSize = 50
                        val pageable =
                            createPageable(sortKey = "name", sortDirection = Sort.Direction.DESC, size = validSize)
                        val templates = mockk<TemplateResServiceDto>()
                        val data = createTestTemplateResControllerDto(isDefault = true)
                        val expected = listOf(data).toSliceResponse(pageable)

                        beforeTest {
                            every { templateService.getAllDefault(pageable = pageable) } returns listOf(templates).toSlice(
                                pageable
                            )
                            every { templateMapper.toResControllerDto(templates) } returns data
                            every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                        }

                        it("템플릿들을 조회하고, 200 OK를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/templates/default")
                                    .param("size", validSize.toString())
                            )
                                .andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                            verify(exactly = 1) {
                                templateService.getAllDefault(pageable = pageable)
                                templateMapper.toResControllerDto(templates)
                                sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable))
                            }
                        }
                    }

                    context("사이즈가 100보다 크면") {
                        val invalidSize = 101

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/templates/default")
                                    .param("size", invalidSize.toString())
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }

                describe("페이징 SortKey 테스트") {
                    describe("올바른 SortKey가 주어지면 성공한다.") {
                        table(
                            headers("sortKey", "direction"),
                            row("externalId", Sort.Direction.ASC),
                            row("externalId", Sort.Direction.DESC),
                            row("icon", Sort.Direction.ASC),
                            row("icon", Sort.Direction.DESC),
                            row("name", Sort.Direction.ASC),
                            row("name", Sort.Direction.DESC),
                            row("isOpen", Sort.Direction.ASC),
                            row("isOpen", Sort.Direction.DESC),
                            row("owner", Sort.Direction.ASC),
                            row("owner", Sort.Direction.DESC),
                            row("isDefault", Sort.Direction.ASC),
                            row("isDefault", Sort.Direction.DESC)
                        ).forAll { sortKey, direction ->
                            context("sortKey = $sortKey, direction = $direction 가 주어졌을 때") {
                                val pageable = createPageable(sortKey = sortKey, sortDirection = direction)
                                val templates = mockk<TemplateResServiceDto>()
                                val data = createTestTemplateResControllerDto(isDefault = true)
                                val expected = listOf(data).toSliceResponse(pageable)

                                beforeTest {
                                    every { templateService.getAllDefault(pageable = pageable) } returns listOf(
                                        templates
                                    ).toSlice(pageable)
                                    every { templateMapper.toResControllerDto(templates) } returns data
                                    every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                                }

                                it("해당 키와 방향으로 정렬된 결과를 반환한다") {
                                    mockMvc.perform(
                                        get("$apiPrefix/templates/default")
                                            .param("sort", "${sortKey},${direction.name}")
                                    )
                                        .andDo(print())
                                        .andExpect(status().isOk)
                                        .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                                    verify(exactly = 1) {
                                        templateService.getAllDefault(pageable = pageable)
                                        templateMapper.toResControllerDto(templates)
                                        sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable))
                                    }
                                }
                            }
                        }
                    }

                    context("올바르지 않은 SortKey가 주어지면") {
                        val invalidSortKey = "invalid"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/templates/default")
                                    .param("sort", "$invalidSortKey,asc")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }
            }

            describe("TemplateApiController.getAllMyTemplates") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    // 기본 pageable 값: size = 20, page = 0, sort = name,desc
                    val defaultPageable = createPageable(sortKey = "name", sortDirection = Sort.Direction.DESC)
                    val templates = mockk<TemplateResServiceDto>()
                    val data = createTestTemplateResControllerDto(owner = requestor)
                    val expected = listOf(data).toSliceResponse(defaultPageable)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            templateService.getAllOwned(
                                pageable = defaultPageable,
                                owner = requestor
                            )
                        } returns listOf(templates).toSlice(defaultPageable)
                        every { templateMapper.toResControllerDto(templates) } returns data
                        every { sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable)) } returns expected
                    }

                    it("템플릿들을 조회하고, 200 OK를 응답한다") {
                        mockMvc.perform(get("$apiPrefix/templates/me"))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/getAllMyTemplates",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("내 템플릿 목록 조회")
                                            .queryParameters(
                                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)")
                                                    .optional(),
                                                parameterWithName("size").description("페이지 크기 (최대 100, 기본값 20)")
                                                    .optional(),
                                                parameterWithName("sort").description("정렬 기준 및 방향 (기본값 name,desc)")
                                                    .optional()
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateSliceResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateSliceResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateService.getAllOwned(pageable = defaultPageable, owner = requestor)
                            templateMapper.toResControllerDto(templates)
                            sliceMapper.toSlicedResponse(listOf(data).toSlice(defaultPageable))
                        }
                    }
                }

                describe("페이징 사이즈 테스트") {
                    context("올바른 사이즈가 주어지면") {
                        val requestor = UUID.randomUUID().toString()
                        val validSize = 50
                        val pageable =
                            createPageable(sortKey = "name", sortDirection = Sort.Direction.DESC, size = validSize)
                        val templates = mockk<TemplateResServiceDto>()
                        val data = createTestTemplateResControllerDto(owner = requestor)
                        val expected = listOf(data).toSliceResponse(pageable)

                        beforeTest {
                            every { userContext.uid } returns requestor
                            every {
                                templateService.getAllOwned(
                                    pageable = pageable,
                                    owner = requestor
                                )
                            } returns listOf(templates).toSlice(pageable)
                            every { templateMapper.toResControllerDto(templates) } returns data
                            every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                        }

                        it("템플릿들을 조회하고, 200 OK를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/templates/me")
                                    .param("size", validSize.toString())
                            )
                                .andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                            verify(exactly = 1) {
                                userContext.uid
                                templateService.getAllOwned(pageable = pageable, owner = requestor)
                                templateMapper.toResControllerDto(templates)
                                sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable))
                            }
                        }
                    }

                    context("사이즈가 100보다 크면") {
                        val invalidSize = 101

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/templates/me")
                                    .param("size", invalidSize.toString())
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }

                describe("페이징 SortKey 테스트") {
                    describe("올바른 SortKey가 주어지면 성공한다.") {
                        table(
                            headers("sortKey", "direction"),
                            row("externalId", Sort.Direction.ASC),
                            row("externalId", Sort.Direction.DESC),
                            row("icon", Sort.Direction.ASC),
                            row("icon", Sort.Direction.DESC),
                            row("name", Sort.Direction.ASC),
                            row("name", Sort.Direction.DESC),
                            row("isOpen", Sort.Direction.ASC),
                            row("isOpen", Sort.Direction.DESC),
                            row("owner", Sort.Direction.ASC),
                            row("owner", Sort.Direction.DESC),
                            row("isDefault", Sort.Direction.ASC),
                            row("isDefault", Sort.Direction.DESC)
                        ).forAll { sortKey, direction ->
                            context("sortKey = $sortKey, direction = $direction 가 주어졌을 때") {
                                val requestor = UUID.randomUUID().toString()
                                val pageable = createPageable(sortKey = sortKey, sortDirection = direction)
                                val templates = mockk<TemplateResServiceDto>()
                                val data = createTestTemplateResControllerDto(owner = requestor)
                                val expected = listOf(data).toSliceResponse(pageable)

                                beforeTest {
                                    every { userContext.uid } returns requestor
                                    every {
                                        templateService.getAllOwned(
                                            pageable = pageable,
                                            owner = requestor
                                        )
                                    } returns listOf(templates).toSlice(pageable)
                                    every { templateMapper.toResControllerDto(templates) } returns data
                                    every { sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable)) } returns expected
                                }

                                it("해당 키와 방향으로 정렬된 결과를 반환한다") {
                                    mockMvc.perform(
                                        get("$apiPrefix/templates/me")
                                            .param("sort", "${sortKey},${direction.name}")
                                    )
                                        .andDo(print())
                                        .andExpect(status().isOk)
                                        .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                                    verify(exactly = 1) {
                                        userContext.uid
                                        templateService.getAllOwned(pageable = pageable, owner = requestor)
                                        templateMapper.toResControllerDto(templates)
                                        sliceMapper.toSlicedResponse(listOf(data).toSlice(pageable))
                                    }
                                }
                            }
                        }
                    }

                    context("올바르지 않은 SortKey가 주어지면") {
                        val invalidSortKey = "invalid"

                        it("400 Bad Request를 응답한다") {
                            mockMvc.perform(
                                get("$apiPrefix/templates/me")
                                    .param("sort", "$invalidSortKey,asc")
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }
            }

            describe("TemplateApiController.createMyTemplate") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val validDto = createTemplateCreateReqControllerDto(
                        name = "Valid Template",
                        icon = "valid-icon",
                        description = "Valid description",
                        isOpen = false,
                        fields = emptyList()
                    )
                    val reqDto = mockk<TemplateCreateReqServiceDto>()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto(owner = requestor)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every { templateMapper.toCreateReqServiceDto(dto = validDto, owner = requestor) } returns reqDto
                        every { templateService.createOwned(reqDto) } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 생성하고, 201 Created를 응답한다") {
                        mockMvc.perform(
                            post("$apiPrefix/template/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto))
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isCreated)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/createMyTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("내 템플릿 생성")
                                            .requestFields(
                                                fieldWithPath("name").description("템플릿 이름 (1-50자)"),
                                                fieldWithPath("icon").description("템플릿 아이콘 (1-50자)"),
                                                fieldWithPath("description").description("템플릿 설명 (1-255자)"),
                                                fieldWithPath("is_open").description("공개 여부"),
                                                fieldWithPath("fields").description("템플릿 필드 목록 (최대 50개)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .requestSchema(Schema("TemplateCreateRequest"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateMapper.toCreateReqServiceDto(dto = validDto, owner = requestor)
                            templateService.createOwned(reqDto)
                            templateMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                describe("잘못된 요청이 주어지면 실패한다.") {
                    table(
                        headers("invalidDto", "reason"),

                        // name 검증
                        row(
                            jsonBuilder {
                                "name" to ""
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "name이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "a".repeat(51)
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "name 길이 50자 초과"
                        ),

                        // icon 검증
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to ""
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "icon이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "a".repeat(51)
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "icon 길이 50자 초과"
                        ),

                        // description 검증
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to ""
                                "is_open" to false
                                "fields" arr { }
                            },
                            "description이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "a".repeat(256)
                                "is_open" to false
                                "fields" arr { }
                            },
                            "description 길이 255자 초과"
                        ),

                        // fields 개수 검증
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    // 51개의 필드 추가 (최대 50개)
                                    repeat(51) { index ->
                                        +obj {
                                            "id" to UUID.randomUUID().toString()
                                            "name" to "field$index"
                                            "icon" to "icon$index"
                                            "type" to "TEXT"
                                            "order" to index
                                            "is_display" to true
                                            "attributes" arr { }
                                        }
                                    }
                                }
                            },
                            "fields 개수 50개 초과"
                        ),

                        // TemplateField 내부 검증 - id 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to ""  // 빈 문자열
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field id가 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to "invalid-uuid"  // UUID 형식이 아님
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field id가 UUID 형식이 아님"
                        ),

                        // TemplateField 내부 검증 - name 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to ""  // 빈 문자열
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field name이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "a".repeat(51)  // 50자 초과
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field name 길이 50자 초과"
                        ),

                        // TemplateField 내부 검증 - icon 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to ""  // 빈 문자열
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field icon이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "a".repeat(51)  // 50자 초과
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field icon 길이 50자 초과"
                        ),

                        // TemplateField 내부 검증 - type 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "INVALID_TYPE"  // 유효하지 않은 타입
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field type이 유효하지 않음"
                        ),

                        // TemplateField 내부 검증 - attributes 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            // 6개의 속성 (최대 5개)
                                            +obj { "key" to "attr1"; "value" arr { +"val1" } }
                                            +obj { "key" to "attr2"; "value" arr { +"val2" } }
                                            +obj { "key" to "attr3"; "value" arr { +"val3" } }
                                            +obj { "key" to "attr4"; "value" arr { +"val4" } }
                                            +obj { "key" to "attr5"; "value" arr { +"val5" } }
                                            +obj { "key" to "attr6"; "value" arr { +"val6" } }
                                        }
                                    }
                                }
                            },
                            "field attributes 개수 5개 초과"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to ""  // 빈 키
                                                "value" arr { +"valid-value" }
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute key가 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to "a".repeat(51)  // 50자 초과
                                                "value" arr { +"valid-value" }
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute key 길이 50자 초과"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to "valid-key"
                                                "value" arr {
                                                    +"a".repeat(51)  // 50자 초과
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute value 원소 길이 50자 초과"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            // 101개의 값 (최대 100개)
                                            repeat(101) { index ->
                                                +"value$index"
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute value 개수 100개 초과"
                        ),

                        // TemplateField 필수 필드 누락
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        // "id" 누락
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field id 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        // "name" 누락
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field name 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        // "icon" 누락
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field icon 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        // "type" 누락
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field type 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        // "order" 누락
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field order 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        // "is_display" 누락
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field is_display 필드 누락"
                        ),
                    ).forAll { invalidDto, reason ->
                        context("$reason 인 잘못된 요청이 주어졌을 때") {
                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/template/me")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(invalidDto)
                                        .accept(MediaType.APPLICATION_JSON)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("TemplateApiController.updateMyTemplate") {
                context("올바른 요청이 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val validExternalId = UUID.randomUUID()
                    val validDto = TemplateUpdateReqControllerDto(
                        name = "Updated Template",
                        icon = "updated-icon",
                        description = "Updated description",
                        isOpen = true,
                        fields = emptyList()
                    )
                    val reqDto = mockk<TemplateUpdateReqServiceDto>()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto(externalId = validExternalId, owner = requestor)

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every {
                            templateMapper.toUpdateReqServiceDto(
                                dto = validDto,
                                owner = requestor,
                                externalId = validExternalId
                            )
                        } returns reqDto
                        every { templateService.updateOwned(dto = reqDto) } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 수정하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            put("$apiPrefix/template/me/{externalId}", validExternalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto))
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/updateMyTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("내 템플릿 수정")
                                            .pathParameters(
                                                parameterWithName("externalId").description("수정할 템플릿의 외부 ID (UUID)")
                                            )
                                            .requestFields(
                                                fieldWithPath("name").description("템플릿 이름 (1-50자)"),
                                                fieldWithPath("icon").description("템플릿 아이콘 (1-50자)"),
                                                fieldWithPath("description").description("템플릿 설명 (1-255자)"),
                                                fieldWithPath("is_open").description("공개 여부"),
                                                fieldWithPath("fields").description("템플릿 필드 목록 (최대 50개)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .requestSchema(Schema("TemplateUpdateRequest"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateMapper.toUpdateReqServiceDto(
                                dto = validDto,
                                owner = requestor,
                                externalId = validExternalId
                            )
                            templateService.updateOwned(dto = reqDto)
                            templateMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                describe("잘못된 요청이 주어지면 실패한다.") {
                    table(
                        headers("invalidDto", "reason"),

                        // name 검증
                        row(
                            jsonBuilder {
                                "name" to ""
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "name이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "a".repeat(51)
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "name 길이 50자 초과"
                        ),

                        // icon 검증
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to ""
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "icon이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "a".repeat(51)
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr { }
                            },
                            "icon 길이 50자 초과"
                        ),

                        // description 검증
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to ""
                                "is_open" to false
                                "fields" arr { }
                            },
                            "description이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "a".repeat(256)
                                "is_open" to false
                                "fields" arr { }
                            },
                            "description 길이 255자 초과"
                        ),

                        // fields 개수 검증
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    // 51개의 필드 추가 (최대 50개)
                                    repeat(51) { index ->
                                        +obj {
                                            "id" to UUID.randomUUID().toString()
                                            "name" to "field$index"
                                            "icon" to "icon$index"
                                            "type" to "TEXT"
                                            "order" to index
                                            "is_display" to true
                                            "attributes" arr { }
                                        }
                                    }
                                }
                            },
                            "fields 개수 50개 초과"
                        ),

                        // TemplateField 내부 검증 - id 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to ""  // 빈 문자열
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field id가 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to "invalid-uuid"  // UUID 형식이 아님
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field id가 UUID 형식이 아님"
                        ),

                        // TemplateField 내부 검증 - name 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to ""  // 빈 문자열
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field name이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "a".repeat(51)  // 50자 초과
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field name 길이 50자 초과"
                        ),

                        // TemplateField 내부 검증 - icon 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to ""  // 빈 문자열
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field icon이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "a".repeat(51)  // 50자 초과
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field icon 길이 50자 초과"
                        ),

                        // TemplateField 내부 검증 - type 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "INVALID_TYPE"  // 유효하지 않은 타입
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field type이 유효하지 않음"
                        ),

                        // TemplateField 내부 검증 - attributes 관련
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            // 6개의 속성 (최대 5개)
                                            +obj { "key" to "attr1"; "value" arr { +"val1" } }
                                            +obj { "key" to "attr2"; "value" arr { +"val2" } }
                                            +obj { "key" to "attr3"; "value" arr { +"val3" } }
                                            +obj { "key" to "attr4"; "value" arr { +"val4" } }
                                            +obj { "key" to "attr5"; "value" arr { +"val5" } }
                                            +obj { "key" to "attr6"; "value" arr { +"val6" } }
                                        }
                                    }
                                }
                            },
                            "field attributes 개수 5개 초과"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to ""  // 빈 키
                                                "value" arr { +"valid-value" }
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute key가 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to "a".repeat(51)  // 50자 초과
                                                "value" arr { +"valid-value" }
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute key 길이 50자 초과"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            +obj {
                                                "key" to "valid-key"
                                                "value" arr {
                                                    +"a".repeat(51)  // 50자 초과
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute value 원소 길이 50자 초과"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr {
                                            // 101개의 값 (최대 100개)
                                            repeat(101) { index ->
                                                +"value$index"
                                            }
                                        }
                                    }
                                }
                            },
                            "field attribute value 개수 100개 초과"
                        ),

                        // TemplateField 필수 필드 누락
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        // "id" 누락
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field id 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        // "name" 누락
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field name 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        // "type" 누락
                                        "order" to 1
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field type 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        // "order" 누락
                                        "is_display" to true
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field order 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "is_open" to false
                                "fields" arr {
                                    +obj {
                                        "id" to UUID.randomUUID().toString()
                                        "name" to "valid-field-name"
                                        "icon" to "valid-field-icon"
                                        "type" to "TEXT"
                                        "order" to 1
                                        // "is_display" 누락
                                        "attributes" arr { }
                                    }
                                }
                            },
                            "field is_display 필드 누락"
                        ),
                    ).forAll { invalidDto, reason ->
                        context("$reason 인 잘못된 요청이 주어졌을 때") {
                            val validExternalId = UUID.randomUUID()

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/template/me/{externalId}", validExternalId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(invalidDto)
                                        .accept(MediaType.APPLICATION_JSON)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }
            }

            describe("TemplateApiController.deleteMyTemplate") {
                context("올바른 externalId가 주어지면") {
                    val requestor = UUID.randomUUID().toString()
                    val validExternalId = UUID.randomUUID()

                    beforeTest {
                        every { userContext.uid } returns requestor
                        every { templateService.deleteOwned(externalId = validExternalId, owner = requestor) } just Runs
                    }

                    it("템플릿을 삭제하고, 204 No Content를 응답한다") {
                        mockMvc.perform(delete("$apiPrefix/template/me/{externalId}", validExternalId))
                            .andDo(print())
                            .andExpect(status().isNoContent)
                            .andDo(
                                document(
                                    "templates/deleteMyTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template")
                                            .description("내 템플릿 삭제")
                                            .pathParameters(
                                                parameterWithName("externalId").description("삭제할 템플릿의 외부 ID (UUID)")
                                            )
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            userContext.uid
                            templateService.deleteOwned(externalId = validExternalId, owner = requestor)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(delete("$apiPrefix/template/me/{externalId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }
        }
    }
}
