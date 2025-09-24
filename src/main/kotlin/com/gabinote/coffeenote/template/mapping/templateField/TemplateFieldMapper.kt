package com.gabinote.coffeenote.template.mapping.templateField

import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapper
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldCreateReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldPatchReqControllerDto
import com.gabinote.coffeenote.template.dto.templateField.controller.TemplateFieldResControllerDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldCreateReqServiceDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldPatchReqServiceDto
import com.gabinote.coffeenote.template.dto.templateField.service.TemplateFieldResServiceDto
import org.mapstruct.*

@Mapper(
    componentModel = "spring",
    uses = [AttributeMapper::class, FieldTypeMapper::class]
)
interface TemplateFieldMapper {


    fun toResServiceDto(templateField: TemplateField): TemplateFieldResServiceDto

    fun toResControllerDto(dto: TemplateFieldResServiceDto): TemplateFieldResControllerDto

    fun toCreateReqServiceDto(dto: TemplateFieldCreateReqControllerDto): TemplateFieldCreateReqServiceDto

    fun toPatchReqServiceDto(dto: TemplateFieldPatchReqControllerDto): TemplateFieldPatchReqServiceDto

    @Mapping(target = "attributes", expression = "java(java.util.Collections.emptySet())")
    fun toTemplateField(dto: TemplateFieldCreateReqServiceDto): TemplateField

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun patchFromDto(dto: TemplateFieldPatchReqServiceDto, @MappingTarget entity: TemplateField): TemplateField

}