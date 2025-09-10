package com.gabinote.coffeenote.template.mapping.templateField

import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
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
    uses = [AttributeMapper::class]
)
interface TemplateFieldMapper {


    @Mapping(source = "display", target = "isDisplay")
    fun toResServiceDto(templateField: TemplateField): TemplateFieldResServiceDto

    @Mapping(source = "display", target = "isDisplay")
    fun toResControllerDto(dto: TemplateFieldResServiceDto): TemplateFieldResControllerDto

    @Mapping(source = "display", target = "isDisplay")
    fun toCreateReqServiceDto(dto: TemplateFieldCreateReqControllerDto): TemplateFieldCreateReqServiceDto

    @Mapping(source = "display", target = "isDisplay")
    fun toPatchReqServiceDto(dto: TemplateFieldPatchReqControllerDto): TemplateFieldPatchReqServiceDto

    @Mapping(source = "display", target = "isDisplay")
    fun toEntity(dto: TemplateFieldCreateReqServiceDto): TemplateField

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(source = "display", target = "display")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun patchFromDto(dto: TemplateFieldPatchReqServiceDto, @MappingTarget entity: TemplateField): TemplateField

}