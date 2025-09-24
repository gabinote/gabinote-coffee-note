package com.gabinote.coffeenote.template.web.controller

import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeFactory
import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.service.template.TemplateService
import com.gabinote.coffeenote.testSupport.testTemplate.WebMvcTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every


abstract class TemplateControllerTest : WebMvcTestTemplate() {
    @MockkBean
    lateinit var templateService: TemplateService

    @MockkBean
    lateinit var templateMapper: TemplateMapper

    @SpykBean
    lateinit var fieldTypeFactory: FieldTypeFactory

    init {
        beforeTest {
            every { fieldTypeFactory.getFieldType("DROP_DOWN") } returns TestFieldType

        }
    }
}