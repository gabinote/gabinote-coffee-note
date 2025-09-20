package com.gabinote.coffeenote.testSupport.testDocs.template

import com.epages.restdocs.apispec.SimpleType
import com.gabinote.coffeenote.testSupport.testDocs.common.SliceDocsSchema
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

object TemplateDocsSchema {

    private val templateFields: Array<FieldDescriptor> = arrayOf(
        fieldWithPath("external_id").type(SimpleType.STRING).description("템플릿 외부 ID (UUID)"),
        fieldWithPath("name").type(SimpleType.STRING).description("템플릿 이름"),
        fieldWithPath("icon").type(SimpleType.STRING).description("템플릿 아이콘"),
        fieldWithPath("description").type(SimpleType.STRING).description("템플릿 설명"),
        fieldWithPath("is_open").type(SimpleType.BOOLEAN).description("템플릿 공개 여부"),
        fieldWithPath("owner").type(SimpleType.STRING).description("템플릿 소유자").optional(),
        fieldWithPath("is_default").type(SimpleType.BOOLEAN).description("기본 템플릿 여부"),
        fieldWithPath("fields[]").description("템플릿에 포함된 필드 목록").optional(),
        fieldWithPath("fields[].id").type(SimpleType.STRING).description("필드 ID").optional(),
        fieldWithPath("fields[].name").type(SimpleType.STRING).description("필드 이름").optional(),
        fieldWithPath("fields[].icon").type(SimpleType.STRING).description("필드 아이콘").optional(),
        fieldWithPath("fields[].type").type(SimpleType.STRING).description("필드 타입").optional(),
        fieldWithPath("fields[].order").type(SimpleType.NUMBER).description("필드 순서").optional(),
        fieldWithPath("fields[].is_display").type(SimpleType.BOOLEAN).description("필드 표시 여부").optional(),
        fieldWithPath("fields[].attributes[].key").type(SimpleType.STRING).description("속성 키").optional(),
        fieldWithPath("fields[].attributes[].value").description("속성 값 집합 (문자열 배열)").optional()
    )

    val templateResponseSchema: Array<FieldDescriptor> = templateFields

    val templateSliceResponseSchema: Array<FieldDescriptor> = arrayOf(
        *templateFields.map { field ->
            fieldWithPath("content[].${field.path}")
                .type(field.type)
                .description(field.description)
                .also { if (field.isOptional) it.optional() }
        }.toTypedArray(),
        *SliceDocsSchema.sliceResponseSchema
    )
}