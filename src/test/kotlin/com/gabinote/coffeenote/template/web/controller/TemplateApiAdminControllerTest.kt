package com.gabinote.coffeenote.template.web.controller

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.gabinote.api.testSupport.testUtil.json.jsonBuilder
import com.gabinote.coffeenote.template.dto.template.controller.TemplateCreateDefaultReqControllerDto
import com.gabinote.coffeenote.template.dto.template.controller.TemplateUpdateDefaultReqControllerDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateCreateDefaultReqServiceDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateResServiceDto
import com.gabinote.coffeenote.template.dto.template.service.TemplateUpdateDefaultReqServiceDto
import com.gabinote.coffeenote.testSupport.testDocs.template.TemplateDocsSchema
import com.gabinote.coffeenote.testSupport.testUtil.data.template.TemplateTestDataHelper.createTestTemplateResControllerDto
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.mockk.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(controllers = [TemplateAdminApiController::class])
class TemplateApiAdminControllerTest : TemplateControllerTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    private val apiPrefix = "/api/v1/admin"


    init {
        describe("[Template] TemplateAdminApiController Test") {
            describe("TemplateAdminApiController.createDefaultTemplate") {
                context("올바른 요청이 주어지면") {
                    val validDto = TemplateCreateDefaultReqControllerDto(
                        name = "Valid Default Template",
                        icon = "valid-icon",
                        description = "Valid description",
                        fields = emptyList()
                    )
                    val reqDto = mockk<TemplateCreateDefaultReqServiceDto>()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto()

                    beforeTest {
                        every { templateMapper.toCreateDefaultReqServiceDto(dto = validDto) } returns reqDto
                        every { templateService.createDefault(dto = reqDto) } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 생성하고, 201 Created를 응답한다") {
                        mockMvc.perform(
                            post("$apiPrefix/template/default")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto))
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isCreated)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/createDefaultTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template", "Admin")
                                            .description("기본 템플릿 생성")
                                            .requestFields(
                                                fieldWithPath("name").description("템플릿 이름 (1-50자)"),
                                                fieldWithPath("icon").description("템플릿 아이콘 (1-50자)"),
                                                fieldWithPath("description").description("템플릿 설명 (1-255자)"),
                                                fieldWithPath("fields").description("템플릿 필드 목록 (최대 50개)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .requestSchema(Schema("TemplateCreateDefaultRequest"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            templateMapper.toCreateDefaultReqServiceDto(dto = validDto)
                            templateService.createDefault(dto = reqDto)
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
                                "fields" arr { }
                            },
                            "name이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "a".repeat(51)
                                "icon" to "valid-icon"
                                "description" to "valid-description"
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
                                "fields" arr { }
                            },
                            "icon이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "a".repeat(51)
                                "description" to "valid-description"
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
                                "fields" arr { }
                            },
                            "description이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "a".repeat(256)
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
                                "fields" arr {
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
                                                    repeat(101) { index ->
                                                        +"value$index"
                                                    }
                                                }
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

                        // 필수 필드 누락
                        row(
                            jsonBuilder {
                                // "name" 누락
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "fields" arr { }
                            },
                            "name 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                // "icon" 누락
                                "description" to "valid-description"
                                "fields" arr { }
                            },
                            "icon 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                // "description" 누락
                                "fields" arr { }
                            },
                            "description 필드 누락"
                        )
                    ).forAll { invalidDto, reason ->
                        context("$reason 인 잘못된 요청이 주어졌을 때") {
                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    post("$apiPrefix/template/default")
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

            describe("TemplateAdminApiController.updateDefaultTemplate") {
                context("올바른 요청이 주어지면") {
                    val validExternalId = UUID.randomUUID()
                    val validDto = TemplateUpdateDefaultReqControllerDto(
                        name = "Updated Default Template",
                        icon = "updated-icon",
                        description = "Updated description",
                        fields = emptyList()
                    )
                    val reqDto = mockk<TemplateUpdateDefaultReqServiceDto>()
                    val serviceRes = mockk<TemplateResServiceDto>()
                    val expected = createTestTemplateResControllerDto(externalId = validExternalId)

                    beforeTest {
                        every {
                            templateMapper.toUpdateDefaultReqServiceDto(
                                dto = validDto,
                                externalId = validExternalId
                            )
                        } returns reqDto
                        every { templateService.updateDefault(dto = reqDto) } returns serviceRes
                        every { templateMapper.toResControllerDto(serviceRes) } returns expected
                    }

                    it("템플릿을 수정하고, 200 OK를 응답한다") {
                        mockMvc.perform(
                            put("$apiPrefix/template/default/{externalId}", validExternalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto))
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isOk)
                            .andExpect(content().json(objectMapper.writeValueAsString(expected)))
                            .andDo(
                                document(
                                    "templates/updateDefaultTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template", "Admin")
                                            .description("기본 템플릿 수정")
                                            .pathParameters(
                                                parameterWithName("externalId").description("수정할 템플릿의 외부 ID (UUID)")
                                            )
                                            .requestFields(
                                                fieldWithPath("name").description("템플릿 이름 (1-50자)"),
                                                fieldWithPath("icon").description("템플릿 아이콘 (1-50자)"),
                                                fieldWithPath("description").description("템플릿 설명 (1-255자)"),
                                                fieldWithPath("fields").description("템플릿 필드 목록 (최대 50개)")
                                            )
                                            .responseFields(
                                                *TemplateDocsSchema.templateResponseSchema
                                            )
                                            .responseSchema(Schema("TemplateResponse"))
                                            .requestSchema(Schema("TemplateUpdateDefaultRequest"))
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            templateMapper.toUpdateDefaultReqServiceDto(dto = validDto, externalId = validExternalId)
                            templateService.updateDefault(dto = reqDto)
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
                                "fields" arr { }
                            },
                            "name이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "a".repeat(51)
                                "icon" to "valid-icon"
                                "description" to "valid-description"
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
                                "fields" arr { }
                            },
                            "icon이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "a".repeat(51)
                                "description" to "valid-description"
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
                                "fields" arr { }
                            },
                            "description이 빈 문자열"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                "description" to "a".repeat(256)
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
                                "fields" arr {
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
                                                    repeat(101) { index ->
                                                        +"value$index"
                                                    }
                                                }
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

                        // 필수 필드 누락
                        row(
                            jsonBuilder {
                                // "name" 누락
                                "icon" to "valid-icon"
                                "description" to "valid-description"
                                "fields" arr { }
                            },
                            "name 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                // "icon" 누락
                                "description" to "valid-description"
                                "fields" arr { }
                            },
                            "icon 필드 누락"
                        ),
                        row(
                            jsonBuilder {
                                "name" to "valid-name"
                                "icon" to "valid-icon"
                                // "description" 누락
                                "fields" arr { }
                            },
                            "description 필드 누락"
                        )
                    ).forAll { invalidDto, reason ->
                        context("$reason 인 잘못된 요청이 주어졌을 때") {
                            val validExternalId = UUID.randomUUID()

                            it("400 Bad Request를 응답한다") {
                                mockMvc.perform(
                                    put("$apiPrefix/template/default/{externalId}", validExternalId)
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

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"
                    val validDto = TemplateUpdateDefaultReqControllerDto(
                        name = "Valid Template",
                        icon = "valid-icon",
                        description = "Valid description",
                        fields = emptyList()
                    )

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(
                            put("$apiPrefix/template/default/{externalId}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validDto))
                                .accept(MediaType.APPLICATION_JSON)
                        )
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }

            describe("TemplateAdminApiController.deleteDefaultTemplate") {
                context("올바른 externalId가 주어지면") {
                    val validExternalId = UUID.randomUUID()

                    beforeTest {
                        every { templateService.deleteDefault(externalId = validExternalId) } just Runs
                    }

                    it("템플릿을 삭제하고, 204 No Content를 응답한다") {
                        mockMvc.perform(delete("$apiPrefix/template/default/{externalId}", validExternalId))
                            .andDo(print())
                            .andExpect(status().isNoContent)
                            .andDo(
                                document(
                                    "templates/admin/deleteDefaultTemplate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                        ResourceSnippetParameters
                                            .builder()
                                            .tags("Template", "Admin")
                                            .description("기본 템플릿 삭제")
                                            .pathParameters(
                                                parameterWithName("externalId").description("삭제할 템플릿의 외부 ID (UUID)")
                                            )
                                            .build()
                                    )
                                )
                            )

                        verify(exactly = 1) {
                            templateService.deleteDefault(externalId = validExternalId)
                        }
                    }
                }

                context("UUID 형식이 아닌 externalId가 주어지면") {
                    val invalidId = "not-uuid"

                    it("400 Bad Request를 응답한다") {
                        mockMvc.perform(delete("$apiPrefix/template/default/{externalId}", invalidId))
                            .andDo(print())
                            .andExpect(status().isBadRequest)
                    }
                }
            }
        }
    }
}