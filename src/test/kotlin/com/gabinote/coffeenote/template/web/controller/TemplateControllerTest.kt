package com.gabinote.coffeenote.template.web.controller

import com.gabinote.coffeenote.template.mapping.template.TemplateMapper
import com.gabinote.coffeenote.template.service.template.TemplateService
import com.gabinote.coffeenote.testSupport.testTemplate.WebMvcTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.data.field.TestFieldType
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every


abstract class TemplateControllerTest : WebMvcTestTemplate() {
    @MockkBean
    lateinit var templateService: TemplateService

    @MockkBean
    lateinit var templateMapper: TemplateMapper


    init {
        beforeTest {
            every { fieldTypeFactory.getFieldType("DROP_DOWN") } returns TestFieldType

        }
    }
}