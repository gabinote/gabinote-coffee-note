package com.gabinote.coffeenote.field.web.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldCreateDefaultReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldUpdateDefaultReqControllerDto
import com.gabinote.coffeenote.field.dto.field.controller.FieldUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.field.service.FieldCreateDefaultReqServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldResServiceDto
import com.gabinote.coffeenote.field.dto.field.service.FieldUpdateDefaultReqServiceDto
import com.gabinote.coffeenote.field.enums.userSearch.FieldAdminSearchScope
import com.gabinote.coffeenote.field.mapping.field.FieldMapper
import com.gabinote.coffeenote.field.service.field.FieldService
import com.gabinote.coffeenote.testSupport.testTemplate.WebMvcTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.FieldTestDataHelper.createTestFieldResControllerDto
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

@WebMvcTest(controllers = [FieldAdminApiController::class])
class FieldAdminApiControllerTest : WebMvcTestTemplate() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var fieldService: FieldService

    @MockkBean
    private lateinit var fieldMapper: FieldMapper

    @MockkBean
    private lateinit var sliceMapper: SliceMapper

    @MockkBean
    private lateinit var userContext: UserContext

    private val apiPrefix = "/admin/api/v1"

    init {
        describe("[Field] FieldAdminApiController") {

            describe("FieldAdminApiController.getAllFields") {

                context("쿼리파라미터 없는 올바른 요청이 주어졌을 때") {
                    // size = 20, page = 0, sort = default,desc, scope = ALL
                    val defaultPageable = createPageable(sortKey = "default")
                    val fields = mockk<FieldResServiceDto>()
                    beforeTest {
                        every {
                            fieldService.getAllByAdminScope(
                                pageable = defaultPageable,
                                scope = FieldAdminSearchScope.ALL,
                            )
                        } returns listOf(fields).toSlice(defaultPageable)
                    }

                    val data = createTestFieldResControllerDto()
                    beforeTest {
                        every { fieldMapper.toResControllerDto(fields) } returns data
                    }

                    val expected = listOf(data).toSliceResponse(defaultPageable)

                    beforeTest {
                        every {
                            sliceMapper.toSlicedResponse(
                                listOf(data).toSlice(
                                    pageable = defaultPageable
                                )
                            )
                        } returns expected
                    }

                    it("기본 pageable을 사용하여 모든 필드를 조회하고 200을 반환한다") {
                        mockMvc.perform(get("$apiPrefix/fields"))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "fields/getAllFields",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Field", "Admin")
                                            .description("모든 필드 목록 조회")
                                            .queryParameters(
                                                parameterWithName("page").description("페이지 번호 (0부터 시작, 기본값 0)")
                                                    .optional(),
                                                parameterWithName("size").description("페이지 크기 (최대 100, 기본값 20)")
                                                    .optional(),
                                                parameterWithName("sort").description("정렬 기준 및 방향 (name, type, owner, id, externalId) ex) sort=name,desc (기본값 default,desc)")
                                                    .optional(),
                                                parameterWithName("scope").description("검색 scope (ALL: 전체, DEFAULT: 기본값만) (기본값 ALL)")
                                                    .optional(),
                                            )
                                            .responseSchema(Schema("FieldSliceResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            fieldService.getAllByAdminScope(
                                pageable = defaultPageable,
                                scope = FieldAdminSearchScope.ALL,
                            )
                            fieldMapper.toResControllerDto(fields)
                            sliceMapper.toSlicedResponse(
                                listOf(data).toSlice(
                                    pageable = defaultPageable
                                )
                            )
                        }
                    }
                }

                describe("페이징 테스트") {

                    describe("sort key test") {

                        describe("올바른 SortKey 가 주어졌을 때 ") {
                            table(
                                headers("sortKey", "direction"),
                                row("name", Sort.Direction.ASC),
                                row("name", Sort.Direction.DESC),
                                row("type", Sort.Direction.ASC),
                                row("type", Sort.Direction.DESC),
                                row("owner", Sort.Direction.ASC),
                                row("owner", Sort.Direction.DESC),
                                row("id", Sort.Direction.ASC),
                                row("id", Sort.Direction.DESC),
                                row("externalId", Sort.Direction.ASC),
                                row("externalId", Sort.Direction.DESC),

                                ).forAll { sortKey, direction ->
                                context("sortKey = $sortKey, direction = $direction 가 주어졌을 때") {
                                    val pageable = createPageable(sortKey = sortKey, sortDirection = direction)
                                    val fields = mockk<FieldResServiceDto>()
                                    beforeTest {
                                        every {
                                            fieldService.getAllByAdminScope(
                                                pageable = pageable,
                                                scope = FieldAdminSearchScope.ALL,
                                            )
                                        } returns listOf(fields).toSlice(pageable)
                                    }

                                    val data = createTestFieldResControllerDto()
                                    beforeTest {
                                        every { fieldMapper.toResControllerDto(fields) } returns data
                                    }

                                    val expected = listOf(data).toSliceResponse(pageable)

                                    beforeTest {
                                        every {
                                            sliceMapper.toSlicedResponse(
                                                listOf(data).toSlice(
                                                    pageable = pageable
                                                )
                                            )
                                        } returns expected
                                    }

                                    it("해당 정렬키와 방향으로 모든 필드를 조회하고 200을 반환한다") {
                                        mockMvc.perform(
                                            get("$apiPrefix/fields")
                                                .param("sort", "${sortKey},${direction.name}")
                                        )
                                            .andDo(print())
                                            .andExpect(status().isOk)
                                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                                        verify(exactly = 1) {
                                            fieldService.getAllByAdminScope(
                                                pageable = pageable,
                                                scope = FieldAdminSearchScope.ALL,
                                            )
                                            fieldMapper.toResControllerDto(fields)
                                            sliceMapper.toSlicedResponse(
                                                listOf(data).toSlice(
                                                    pageable = pageable
                                                )
                                            )
                                        }
                                    }
                                }

                            }
                        }

                        context("올바르지 않은 SortKey 가 주어졌을 때") {
                            val invalidSortKey = "invalid"
                            it("400을 반환한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/fields")
                                        .param("sort", "$invalidSortKey,asc")
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }

                    }

                    describe("size test") {
                        context("올바른 페이징 사이즈가 주어졌을 때") {
                            val validSize = 50
                            val pageable = createPageable(sortKey = "default", size = validSize)
                            val fields = mockk<FieldResServiceDto>()
                            beforeTest {
                                every {
                                    fieldService.getAllByAdminScope(
                                        pageable = pageable,
                                        scope = FieldAdminSearchScope.ALL,
                                    )
                                } returns listOf(fields).toSlice(pageable)
                            }

                            val data = createTestFieldResControllerDto()
                            beforeTest {
                                every { fieldMapper.toResControllerDto(fields) } returns data
                            }

                            val expected = listOf(data).toSliceResponse(pageable)

                            beforeTest {
                                every {
                                    sliceMapper.toSlicedResponse(
                                        listOf(data).toSlice(
                                            pageable = pageable
                                        )
                                    )
                                } returns expected
                            }

                            it("해당 size로 모든 필드를 조회하고 200을 반환한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/fields")
                                        .param("size", validSize.toString())
                                )
                                    .andDo(print())
                                    .andExpect(status().isOk)
                                    .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                                verify(exactly = 1) {
                                    fieldService.getAllByAdminScope(
                                        pageable = pageable,
                                        scope = FieldAdminSearchScope.ALL,
                                    )
                                    fieldMapper.toResControllerDto(fields)
                                    sliceMapper.toSlicedResponse(
                                        listOf(data).toSlice(
                                            pageable = pageable
                                        )
                                    )
                                }
                            }
                        }

                        context("최대 값을 초과하는 페이징 사이즈가 주어졌을 때") {
                            val invalidPageSize = 101
                            it("400을 반환한다") {
                                mockMvc.perform(
                                    get("$apiPrefix/fields")
                                        .param("size", invalidPageSize.toString())
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }
                }

                describe("Scope 테스트") {

                    describe("올바른 scope가 주어졌을 때") {
                        table(
                            headers("scope"),
                            row(FieldAdminSearchScope.ALL),
                            row(FieldAdminSearchScope.DEFAULT),
                        ).forAll { scope ->
                            context("scope = $scope 가 주어졌을 때") {
                                val pageable = createPageable(sortKey = "default")
                                val fields = mockk<FieldResServiceDto>()
                                beforeTest {
                                    every {
                                        fieldService.getAllByAdminScope(
                                            pageable = pageable,
                                            scope = scope,
                                        )
                                    } returns listOf(fields).toSlice(pageable)
                                }

                                val data = createTestFieldResControllerDto()
                                beforeTest {
                                    every { fieldMapper.toResControllerDto(fields) } returns data
                                }

                                val expected = listOf(data).toSliceResponse(pageable)

                                beforeTest {
                                    every {
                                        sliceMapper.toSlicedResponse(
                                            listOf(data).toSlice(
                                                pageable = pageable
                                            )
                                        )
                                    } returns expected
                                }

                                it("해당 scope로 모든 필드를 조회하고 200을 반환한다") {
                                    mockMvc.perform(
                                        get("$apiPrefix/fields")
                                            .param("scope", scope.name)
                                    )
                                        .andDo(print())
                                        .andExpect(status().isOk)
                                        .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                                    verify(exactly = 1) {
                                        fieldService.getAllByAdminScope(
                                            pageable = pageable,
                                            scope = scope,
                                        )
                                        fieldMapper.toResControllerDto(fields)
                                        sliceMapper.toSlicedResponse(
                                            listOf(data).toSlice(
                                                pageable = pageable
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    context("올바르지 않은 scope 가 주어졌을 때") {
                        val invalidScope = "invalid"
                        it("400을 반환한다") {
                            mockMvc.perform(
                                get("$apiPrefix/fields")
                                    .param("scope", invalidScope)
                            )
                                .andDo(print())
                                .andExpect(status().isBadRequest)
                        }
                    }
                }
            }

            describe("FieldAdminApiController.getFieldById") {

                context("올바른 외부 id가 주어졌을 때") {
                    val validId = UUID.randomUUID()
                    val serviceRes = mockk<FieldResServiceDto>()

                    beforeTest {
                        every { fieldService.getByExternalId(externalId = validId) } returns serviceRes
                    }

                    val expected = createTestFieldResControllerDto(externalId = validId.toString())

                    beforeTest {
                        every { fieldMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("필드를 조회하고 200을 반환한다") {
                        mockMvc.perform(get("$apiPrefix/field/{extId}", validId))
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "fields/getFieldById",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Field", "Admin")
                                            .description("단일 필드 조회")
                                            .pathParameters(
                                                parameterWithName("extId").description("조회할 필드의 외부 ID (UUID)")
                                            )
                                            .responseSchema(Schema("FieldResponse"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            fieldService.getByExternalId(externalId = validId)
                            fieldMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                context("올바르지 않은 외부 id가 주어졌을 때") {
                    val invalidId = "not uuid"

                    it("400을 반환한다") {
                        mockMvc.perform(get("$apiPrefix/field/{extId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }

            }

            describe("FieldAdminApiController.createDefaultField") {

                context("올바른 요청이 주어졌을 때") {

                    val validDto = FieldCreateDefaultReqControllerDto(
                        name = "50자를 넘지 않는 이름",
                        icon = "50자를 넘지 않는 아이콘",
                        type = "SHORT_TEXT",
                        attributes = setOf(
                            AttributeCreateReqControllerDto(
                                key = "50자 넘지 않는 키",
                                value = setOf("50자 넘지 않는 값")
                            )
                        )
                    )
                    val reqDto = mockk<FieldCreateDefaultReqServiceDto>()
                    beforeTest {
                        every { fieldMapper.toCreateDefaultReqServiceDto(dto = validDto) } returns reqDto
                    }
                    val serviceRes = mockk<FieldResServiceDto>()
                    beforeTest {
                        every { fieldService.createDefaultField(dto = reqDto) } returns serviceRes
                    }

                    val data = createTestFieldResControllerDto()
                    beforeTest {
                        every { fieldMapper.toResControllerDto(serviceRes) } returns data
                    }

                    it("해당 필드를 저장하고, 저장된 필드를 리턴한다.") {
                        mockMvc.perform(
                            post("$apiPrefix/field/default")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto))
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isCreated)
                            .andExpect(content().json(objectMapper.writeValueAsString(data)))
                            .andDo(
                                document(
                                    "fields/createDefaultField",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Admin", "Field")
                                            .description("기본값 필드 생성")
                                            .requestFields(
                                                fieldWithPath("name").description("필드 이름 (1-50자)"),
                                                fieldWithPath("icon").description("필드 아이콘 (1-50자)"),
                                                fieldWithPath("type").description("필드 타입 (TEXT, NUMBER, DATE 등)"),
                                                fieldWithPath("attributes").description("필드 속성 목록 (최대 5개)"),
                                                fieldWithPath("attributes[].key").description("속성 키 (1-50자)"),
                                                fieldWithPath("attributes[].value").description("속성 값 목록 (각 값 1-50자)")
                                            )
                                            .responseSchema(Schema("FieldResponse"))
                                            .requestSchema(Schema("DefaultFieldCreateRequest"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            fieldMapper.toCreateDefaultReqServiceDto(dto = validDto)
                            fieldService.createDefaultField(dto = reqDto)
                            fieldMapper.toResControllerDto(serviceRes)
                        }
                    }
                }

                describe("올바르지 않은 요청이 주어질 때") {
                    table(
                        headers("invalidDto", "reason"),

                        // 1. name 빈 문자열
                        row(
                            jsonBuilder {
                                "name" to ""
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to "test"
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "name이 빈 문자열(비어있음)"
                        ),

                        // 2. name 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "name" to "a".repeat(51)
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to "test"
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "name 길이 50자 초과"
                        ),

                        // 3. icon 빈 문자열
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to ""
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to "test"
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "icon이 빈 문자열(비어있음)"
                        ),

                        // 4. icon 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "a".repeat(51)
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to "test"
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "icon 길이 50자 초과"
                        ),

                        // 5. type 허용되지 않은 타입
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "123"
                                "type" to "FAIL_TYPE" // RequiredFieldType 에러 유발
                                "attributes" arr {
                                    +obj {
                                        "key" to "test"
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "type이 허용된 값이 아닌"
                        ),
                        // 6. attributes 개수 초과 (max = 5) -> 6개
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj { "key" to "k1"; "value" arr { +"v" } }
                                    +obj { "key" to "k2"; "value" arr { +"v" } }
                                    +obj { "key" to "k3"; "value" arr { +"v" } }
                                    +obj { "key" to "k4"; "value" arr { +"v" } }
                                    +obj { "key" to "k5"; "value" arr { +"v" } }
                                    +obj { "key" to "k6"; "value" arr { +"v" } } // 6번째 -> 실패
                                }
                            },
                            "attributes 개수 5개 초과 (6개)"
                        ),

                        // 7. attributes 내부 key가 빈 문자열
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to ""
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "attribute.key가 빈 문자열"
                        ),

                        // 8. attributes 내부 key 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to "a".repeat(51)
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "attribute.key 길이 50자 초과"
                        ),

                        // 9. attribute.value의 각 원소 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj {
                                        "key" to "test"
                                        "value" arr {
                                            +"v".repeat(51) // 각 원소가 51자 -> Size(max=50) 위반
                                        }
                                    }
                                }
                            },
                            "attribute.value의 원소 중 하나가 50자 초과"
                        ),

                        // 10. name 필드 누락
                        row(
                            jsonBuilder {
                                // "name" 생략
                                "icon" to "123"
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj { "key" to "test"; "value" arr { +"test1" } }
                                }
                            },
                            "name 필드 누락"
                        ),

                        // 11. icon 필드 누락
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                // "icon" 생략
                                "type" to "TEXT"
                                "attributes" arr {
                                    +obj { "key" to "test"; "value" arr { +"test1" } }
                                }
                            },
                            "icon 필드 누락"
                        ),

                        // 12. type 필드 누락
                        row(
                            jsonBuilder {
                                "name" to "validName"
                                "icon" to "123"
                                //type 필드 누락
                                "attributes" arr {
                                    +obj { "key" to "test"; "value" arr { +"test1" } }
                                }
                            },
                            "icon 필드 누락"
                        )
                    )
                        .forAll { invalidDto, reason ->
                            context("$reason 인 잘못된 요청이 주어졌을 때") {
                                it("400을 반환한다") {
                                    mockMvc.perform(
                                        post("$apiPrefix/field/default")
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

            describe("FieldAdminApiController.updateDefaultField") {
                describe("올바른 요청") {

                    context("모든 값이 들어있는 올바른 요청이 주어졌을 때") {
                        val validReqDto = FieldUpdateDefaultReqControllerDto(
                            name = "test",
                            icon = "test",
                            attributes = setOf(
                                AttributeUpdateReqControllerDto(
                                    key = "test",
                                    value = setOf("test1")
                                )
                            )
                        )

                        val validExtId = UUID.randomUUID()

                        val reqDto = mockk<FieldUpdateDefaultReqServiceDto>()

                        beforeTest {
                            every {
                                fieldMapper.toUpdateDefaultReqServiceDto(
                                    dto = validReqDto,
                                    externalId = validExtId
                                )
                            } returns reqDto
                        }

                        val field = mockk<FieldResServiceDto>()
                        beforeTest {
                            every { fieldService.updateDefaultField(dto = reqDto) } returns field
                        }

                        val expected = createTestFieldResControllerDto()
                        beforeTest {
                            every { fieldMapper.toResControllerDto(field) } returns expected
                        }

                        it("해당 필드를 수정하고 200과 수정 결과를 리턴한다.") {
                            mockMvc.perform(
                                put("$apiPrefix/field/default/{extId}", validExtId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(validReqDto))
                                    .accept(MediaType.APPLICATION_JSON)
                            ).andDo(print())
                                .andExpect(status().isOk)
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                                .andDo(
                                    document(
                                        "fields/updateDefaultField",
                                        preprocessRequest(prettyPrint()),
                                        preprocessResponse(prettyPrint()),
                                        resource(
                                            ResourceSnippetParameters
                                                .builder()
                                                .tags("Admin", "Field")
                                                .description("기본 필드 수정")
                                                .pathParameters(
                                                    parameterWithName("extId").description("수정할 필드의 외부 ID (UUID)")
                                                )
                                                .requestFields(
                                                    fieldWithPath("name").description("필드 이름 (1-50자)").optional(),
                                                    fieldWithPath("icon").description("필드 아이콘 (1-50자)").optional(),
                                                    fieldWithPath("attributes").description("필드 속성 목록 (최대 5개)")
                                                        .optional(),
                                                    fieldWithPath("attributes[].key").description("속성 키 (1-50자)")
                                                        .optional(),
                                                    fieldWithPath("attributes[].value").description("속성 값 목록 (각 값 1-50자)")
                                                        .optional()
                                                )
                                                .responseSchema(Schema("FieldResponse"))
                                                .requestSchema(Schema("DefaultFieldUpdateRequest"))
                                                .build()
                                        )
                                    )
                                )

                            verify(exactly = 1) {
                                fieldMapper.toUpdateDefaultReqServiceDto(
                                    dto = validReqDto,
                                    externalId = validExtId
                                )
                                fieldService.updateDefaultField(dto = reqDto)
                                fieldMapper.toResControllerDto(field)
                            }
                        }
                    }

                    describe("일부 값만 들어있는 요청") {
                        table(
                            headers("validReq", "validReqDto", "reason"),
                            row(
                                jsonBuilder {
                                    "name" to "test"
                                },
                                FieldUpdateDefaultReqControllerDto(name = "test"),
                                "name만 있는 요청"
                            ),
                            row(
                                jsonBuilder { "icon" to "test" },
                                FieldUpdateDefaultReqControllerDto(icon = "test"),
                                "icon만 있는 요청"
                            ),
                            row(
                                jsonBuilder {
                                    "attributes" arr {
                                        +obj {
                                            "key" to "test"
                                            "value" arr { +"test1" }
                                        }
                                    }
                                },
                                FieldUpdateDefaultReqControllerDto(
                                    attributes = setOf(
                                        AttributeUpdateReqControllerDto(
                                            key = "test",
                                            value = setOf("test1")
                                        )
                                    )
                                ),
                                "attributes만 있는 요청"
                            )
                        ).forAll { validReq, validReqDto, reason ->
                            context("$reason 인 올바른 요청이 주어지면") {

                                val validExtId = UUID.randomUUID()

                                val reqDto = mockk<FieldUpdateDefaultReqServiceDto>()

                                beforeTest {
                                    every {
                                        fieldMapper.toUpdateDefaultReqServiceDto(
                                            dto = validReqDto,
                                            externalId = validExtId
                                        )
                                    } returns reqDto
                                }

                                val field = mockk<FieldResServiceDto>()
                                beforeTest {
                                    every { fieldService.updateDefaultField(dto = reqDto) } returns field
                                }

                                val expected = createTestFieldResControllerDto()
                                beforeTest {
                                    every { fieldMapper.toResControllerDto(field) } returns expected
                                }

                                it("해당 필드를 수정하고 200과 수정 결과를 리턴한다.") {
                                    mockMvc.perform(
                                        put("$apiPrefix/field/default/{extId}", validExtId)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(validReq)
                                            .accept(MediaType.APPLICATION_JSON)
                                    ).andDo(print())
                                        .andExpect(status().isOk)
                                        .andExpect(content().json(objectMapper.writeValueAsString(expected)))

                                    verify(exactly = 1) {
                                        fieldMapper.toUpdateDefaultReqServiceDto(
                                            dto = validReqDto,
                                            externalId = validExtId
                                        )
                                        fieldService.updateDefaultField(dto = reqDto)
                                        fieldMapper.toResControllerDto(field)
                                    }
                                }
                            }
                        }

                        context("올바르지 않은 외부 id가 주어졌을 때") {
                            val invalidId = "not uuid"
                            val validDto = FieldUpdateReqControllerDto(
                                name = "test",
                                icon = "test",
                                attributes = setOf(
                                    AttributeUpdateReqControllerDto(
                                        key = "test",
                                        value = setOf("test1")
                                    )
                                )
                            )
                            it("400 을 리턴한다.") {
                                mockMvc.perform(
                                    put("$apiPrefix/field/default/{extId}", invalidId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(validDto))
                                        .accept(MediaType.APPLICATION_JSON)
                                )
                                    .andDo(print())
                                    .andExpect(status().isBadRequest)
                            }
                        }
                    }

                }

                describe("올바르지 않은 요청") {
                    table(
                        headers("invalidDto", "reason"),

                        // 1. name 빈 문자열
                        row(
                            jsonBuilder {
                                "name" to ""
                            },
                            "name이 빈 문자열(비어있음)"
                        ),

                        // 2. name 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "name" to "a".repeat(51)
                            },
                            "name 길이 50자 초과"
                        ),

                        // 3. icon 빈 문자열
                        row(
                            jsonBuilder {
                                "icon" to ""
                            },
                            "icon이 빈 문자열(비어있음)"
                        ),

                        // 4. icon 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "icon" to "a".repeat(51)
                            },
                            "icon 길이 50자 초과"
                        ),

                        // 5. attributes 개수 초과 (max = 5) -> 6개
                        row(
                            jsonBuilder {
                                "attributes" arr {
                                    +obj { "key" to "k1"; "value" arr { +"v" } }
                                    +obj { "key" to "k2"; "value" arr { +"v" } }
                                    +obj { "key" to "k3"; "value" arr { +"v" } }
                                    +obj { "key" to "k4"; "value" arr { +"v" } }
                                    +obj { "key" to "k5"; "value" arr { +"v" } }
                                    +obj { "key" to "k6"; "value" arr { +"v" } } // 6번째 -> 실패
                                }
                            },
                            "attributes 개수 5개 초과 (6개)"
                        ),

                        // 6. attributes 내부 key가 빈 문자열
                        row(
                            jsonBuilder {
                                "attributes" arr {
                                    +obj {
                                        "key" to ""
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "attribute.key가 빈 문자열"
                        ),

                        // 7. attributes 내부 key 길이 초과 (>50)
                        row(
                            jsonBuilder {
                                "attributes" arr {
                                    +obj {
                                        "key" to "a".repeat(51)
                                        "value" arr { +"test1" }
                                    }
                                }
                            },
                            "attributes 내부 key 길이 초과"
                        ),
                    ).forAll { invalidDto, reason ->
                        context("$reason 인 올바르지 않은 요청이 주어졌을 때") {
                            it("400 을 리턴한다.") {
                                mockMvc.perform(
                                    put("$apiPrefix/field/default/{extId}", UUID.randomUUID())
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

            describe("FieldAdminApiController.deleteDefaultField") {

                context("올바른 요청이 주어졌을 때") {
                    val validExtId = UUID.randomUUID()

                    beforeTest {
                        every { fieldService.deleteDefaultByExternalId(externalId = validExtId) } just Runs
                    }

                    it("해당 필드를 삭제하고 204를 반환한다") {
                        mockMvc.perform(
                            delete("$apiPrefix/field/default/{extId}", validExtId)
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isNoContent)
                            .andDo(
                                document(
                                    "fields/deleteDefaultField",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Field", "Admin")
                                            .description("기본 필드 삭제")
                                            .pathParameters(
                                                parameterWithName("extId").description("삭제할 필드의 외부 ID (UUID)")
                                            )
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            fieldService.deleteDefaultByExternalId(externalId = validExtId)
                        }
                    }
                }

                context("올바르지 않은 external id가 주어졌을 때") {
                    val invalidId = "not uuid"
                    it("400을 리턴한다.") {
                        mockMvc.perform(
                            delete("$apiPrefix/field/default/{extId}", invalidId)
                                .accept(MediaType.APPLICATION_JSON)
                        ).andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

        }
    }
}
