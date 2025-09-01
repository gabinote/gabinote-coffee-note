package com.gabinote.coffeenote.field.mapping.field

import com.gabinote.coffeenote.common.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.dto.field.controller.*
import com.gabinote.coffeenote.field.dto.field.service.*
import org.mapstruct.*
import java.util.*

@Mapper(
    componentModel = "spring",
    uses = [AttributeMapper::class]
)
interface FieldMapper {

    //response
    fun toResServiceDto(field: Field): FieldResServiceDto
    fun toResControllerDto(dto: FieldResServiceDto): FieldResControllerDto


    //create req
    fun toCreateReqServiceDto(dto: FieldCreateReqControllerDto, owner: String): FieldCreateReqServiceDto

    @Mapping(target = "default", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    fun toField(dto: FieldCreateReqServiceDto): Field

    //update req
    fun toUpdateReqServiceDto(
        dto: FieldUpdateReqControllerDto,
        externalId: UUID,
        owner: String
    ): FieldUpdateReqServiceDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "default", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateFromDto(dto: FieldUpdateReqServiceDto, @MappingTarget to: Field): Field


    //create default req
    fun toCreateDefaultReqServiceDto(dto: FieldCreateDefaultReqControllerDto): FieldCreateDefaultReqServiceDto

    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "default", constant = "true")
    @Mapping(target = "id", ignore = true)
    fun toFieldDefault(dto: FieldCreateDefaultReqServiceDto): Field

    // update default req
    fun toUpdateDefaultReqServiceDto(
        dto: FieldUpdateDefaultReqControllerDto,
        externalId: UUID
    ): FieldUpdateDefaultReqServiceDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "default", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateFromDefaultDto(dto: FieldUpdateDefaultReqServiceDto, @MappingTarget to: Field): Field


}