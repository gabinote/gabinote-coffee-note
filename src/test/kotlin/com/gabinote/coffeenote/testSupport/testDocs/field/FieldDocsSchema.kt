package com.gabinote.coffeenote.testSupport.testDocs.field

import com.epages.restdocs.apispec.SimpleType
import com.gabinote.coffeenote.testSupport.testDocs.common.SliceDocsSchema
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath

object FieldDocsSchema {
    val fieldResponseSchema: Array<FieldDescriptor> = arrayOf(
        fieldWithPath("external_id").type(SimpleType.STRING).description("Field external ID"),
        fieldWithPath("is_default").type(SimpleType.BOOLEAN).description("기본 Field 여부"),
        fieldWithPath("name").type(SimpleType.STRING).description("Field 이름"),
        fieldWithPath("icon").type(SimpleType.STRING).description("Field 아이콘"),
        fieldWithPath("type").type(SimpleType.STRING).description("Field 타입"),
        fieldWithPath("attributes").description("Field Type 속성들"),
        fieldWithPath("attributes[].key").type(SimpleType.STRING).description("속성 키"),
        fieldWithPath("attributes[].value").type(SimpleType.STRING).description("속성 값"),
        fieldWithPath("owner").type(SimpleType.STRING).description("Field 소유자").optional(),
    )

    val fieldSliceResponseSchema: Array<FieldDescriptor> = arrayOf(
        fieldWithPath("content[].external_id").type(SimpleType.STRING).description("Field external ID"),
        fieldWithPath("content[].is_default").type(SimpleType.BOOLEAN).description("기본 Field 여부"),
        fieldWithPath("content[].name").type(SimpleType.STRING).description("Field 이름"),
        fieldWithPath("content[].icon").type(SimpleType.STRING).description("Field 아이콘"),
        fieldWithPath("content[].type").type(SimpleType.STRING).description("Field 타입"),
        fieldWithPath("content[].attributes").description("Field Type 속성들"),
        fieldWithPath("content[].attributes[].key").type(SimpleType.STRING).description("속성 키"),
        fieldWithPath("content[].attributes[].value").type(SimpleType.STRING).description("속성 값"),
        fieldWithPath("content[].owner").type(SimpleType.STRING).description("Field 소유자").optional(),
        *SliceDocsSchema.sliceResponseSchema
    )
}