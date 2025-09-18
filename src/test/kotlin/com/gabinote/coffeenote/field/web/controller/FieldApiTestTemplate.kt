package com.gabinote.coffeenote.field.web.controller

import com.gabinote.coffeenote.common.mapping.slice.SliceMapper
import com.gabinote.coffeenote.common.util.context.UserContext
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.field.mapping.field.FieldMapper
import com.gabinote.coffeenote.field.service.field.FieldService
import com.gabinote.coffeenote.testSupport.testTemplate.WebMvcTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every

abstract class FieldApiTestTemplate : WebMvcTestTemplate() {
    @MockkBean
    lateinit var fieldService: FieldService

    @MockkBean
    lateinit var fieldMapper: FieldMapper

    @MockkBean
    lateinit var sliceMapper: SliceMapper

    @MockkBean
    lateinit var userContext: UserContext

    @SpykBean
    lateinit var fieldTypeFactory: FieldTypeFactory

    init {
        beforeTest {
            every { fieldTypeFactory.getFieldType("DROP_DOWN") } returns TestFieldType

        }
    }
}