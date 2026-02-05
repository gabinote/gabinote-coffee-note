package com.gabinote.coffeenote.testSupport.testDocs.note

import com.epages.restdocs.apispec.SimpleType
import com.gabinote.coffeenote.testSupport.testDocs.common.SliceDocsSchema
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

object NoteDocsSchema {

    private val noteListFields: Array<FieldDescriptor> = arrayOf(
        fieldWithPath("external_id").type(SimpleType.STRING).description("노트 외부 ID (UUID)"),
        fieldWithPath("title").type(SimpleType.STRING).description("노트 제목"),
        fieldWithPath("thumbnail").type(SimpleType.STRING).description("노트 썸네일 URL").optional(),
        fieldWithPath("created_date").type(SimpleType.STRING).description("노트 생성 일시"),
        fieldWithPath("modified_date").type(SimpleType.STRING).description("노트 수정 일시"),
        fieldWithPath("is_open").type(SimpleType.BOOLEAN).description("노트 공개 여부"),
        fieldWithPath("owner").type(SimpleType.STRING).description("노트 소유자 ID"),
        fieldWithPath("display_fields[]").description("노트 표시 필드 목록").optional(),
        fieldWithPath("display_fields[].name").type(SimpleType.STRING).description("표시 필드 이름").optional(),
        fieldWithPath("display_fields[].icon").type(SimpleType.STRING).description("표시 필드 아이콘").optional(),
        fieldWithPath("display_fields[].values[]").description("표시 필드 값 목록").optional(),
        fieldWithPath("display_fields[].order").type(SimpleType.NUMBER).description("표시 필드 순서").optional()
    )

    private val noteFields: Array<FieldDescriptor> = arrayOf(
        fieldWithPath("external_id").type(SimpleType.STRING).description("노트 외부 ID (UUID)"),
        fieldWithPath("title").type(SimpleType.STRING).description("노트 제목"),
        fieldWithPath("thumbnail").type(SimpleType.STRING).description("노트 썸네일 URL").optional(),
        fieldWithPath("created_date").type(SimpleType.STRING).description("노트 생성 일시"),
        fieldWithPath("modified_date").type(SimpleType.STRING).description("노트 수정 일시"),
        fieldWithPath("is_open").type(SimpleType.BOOLEAN).description("노트 공개 여부"),
        fieldWithPath("owner").type(SimpleType.STRING).description("노트 소유자 ID"),
        fieldWithPath("fields[]").description("노트 필드 목록").optional(),
        fieldWithPath("fields[].id").type(SimpleType.STRING).description("필드 ID").optional(),
        fieldWithPath("fields[].name").type(SimpleType.STRING).description("필드 이름").optional(),
        fieldWithPath("fields[].icon").type(SimpleType.STRING).description("필드 아이콘").optional(),
        fieldWithPath("fields[].type").type(SimpleType.STRING).description("필드 타입").optional(),
        fieldWithPath("fields[].order").type(SimpleType.NUMBER).description("필드 순서").optional(),
        fieldWithPath("fields[].is_display").type(SimpleType.BOOLEAN).description("필드 표시 여부").optional(),
        fieldWithPath("fields[].values[]").description("필드 값 목록").optional(),
        fieldWithPath("fields[].attributes[]").description("필드 속성 목록").optional(),
        fieldWithPath("fields[].attributes[].key").type(SimpleType.STRING).description("필드 속성 키").optional(),
        fieldWithPath("fields[].attributes[].value[]").description("필드 속성 값 집합 (문자열 배열)").optional(),
        fieldWithPath("display_fields[]").description("노트 표시 필드 목록").optional(),
        fieldWithPath("display_fields[].name").type(SimpleType.STRING).description("표시 필드 이름").optional(),
        fieldWithPath("display_fields[].icon").type(SimpleType.STRING).description("표시 필드 아이콘").optional(),
        fieldWithPath("display_fields[].values[]").description("표시 필드 값 목록").optional(),
        fieldWithPath("display_fields[].order").type(SimpleType.NUMBER).description("표시 필드 순서").optional()
    )

    val noteResponseSchema: Array<FieldDescriptor> = noteFields

    private val noteIndexFields: Array<FieldDescriptor> = arrayOf(
        fieldWithPath("id").type(SimpleType.STRING).description("노트 외부 ID"),
        fieldWithPath("title").type(SimpleType.STRING).description("노트 제목"),
        fieldWithPath("owner").type(SimpleType.STRING).description("노트 소유자 ID"),
        fieldWithPath("created_date").type(SimpleType.STRING).description("노트 생성 일시"),
        fieldWithPath("modified_date").type(SimpleType.STRING).description("노트 수정 일시"),
        fieldWithPath("display_fields[]").description("노트 표시 필드 목록").optional(),
        fieldWithPath("display_fields[].name").type(SimpleType.STRING).description("표시 필드 이름").optional(),
        fieldWithPath("display_fields[].tag").type(SimpleType.STRING).description("표시 필드 태그").optional(),
        fieldWithPath("display_fields[].value[]").description("표시 필드 값 목록").optional(),
        fieldWithPath("display_fields[].order").type(SimpleType.NUMBER).description("표시 필드 순서").optional(),
        fieldWithPath("filters").description("필터 맵").optional()
    )

    val noteIndexSliceResponseSchema: Array<FieldDescriptor> = arrayOf(
        *noteIndexFields.map { field ->
            fieldWithPath("content[].${field.path}")
                .type(field.type)
                .description(field.description)
                .also { if (field.isOptional) it.optional() }
        }.toTypedArray(),
        *SliceDocsSchema.sliceResponseSchema
    )

    val noteListSliceResponseSchema: Array<FieldDescriptor> = arrayOf(
        *noteListFields.map { field ->
            fieldWithPath("content[].${field.path}")
                .type(field.type)
                .description(field.description)
                .also { if (field.isOptional) it.optional() }
        }.toTypedArray(),
        *SliceDocsSchema.sliceResponseSchema
    )
}

