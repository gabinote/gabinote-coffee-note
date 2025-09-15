package com.gabinote.coffeenote.template.mapping.template

import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.template.domain.template.Template
import com.gabinote.coffeenote.template.dto.template.controller.*
import com.gabinote.coffeenote.template.dto.template.service.*
import com.gabinote.coffeenote.template.mapping.templateField.TemplateFieldMapper
import org.mapstruct.*
import java.util.*

@Mapper(
    componentModel = "spring",
    uses = [AttributeMapper::class, TemplateFieldMapper::class]
)
interface TemplateMapper {

    /**
     * 템플릿 생성 매핑
     * owner는 별도로 받음
     * @param dto 템플릿 생성 요청 컨트롤러 DTO
     * @param owner 템플릿 소유자
     * @return 템플릿 생성 요청 서비스 DTO
     * @see TemplateCreateReqControllerDto
     * @see TemplateCreateReqServiceDto
     */
    fun toCreateReqServiceDto(dto: TemplateCreateReqControllerDto, owner: String): TemplateCreateReqServiceDto


    /**
     * 템플릿 생성 매핑
     * id와 externalId는 무시하고 매핑
     * isDefault는 false로 설정
     * @param dto 템플릿 생성 요청 서비스 DTO
     * @return 템플릿 엔티티
     * @see TemplateCreateReqServiceDto
     * @see Template
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "isDefault", constant = "false")
    fun toTemplate(dto: TemplateCreateReqServiceDto): Template

    /**
     * 기본 템플릿 생성 매핑
     * owner는 별도로 받음
     * @param dto 기본 템플릿 생성 요청 컨트롤러 DTO
     * @return 기본 템플릿 생성 요청 서비스 DTO
     * @see TemplateCreateDefaultReqControllerDto
     * @see TemplateCreateDefaultReqServiceDto
     */
    fun toCreateDefaultReqServiceDto(dto: TemplateCreateDefaultReqControllerDto): TemplateCreateDefaultReqServiceDto

    /**
     * 기본 템플릿 생성 매핑
     * id, externalId, owner는 무시하고 매핑
     * isDefault와 isOpen은 각각 true로 설정
     * @param dto 기본 템플릿 생성 요청 서비스 DTO
     * @return 템플릿 엔티티
     * @see TemplateCreateDefaultReqServiceDto
     * @see Template
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "isDefault", constant = "true")
    @Mapping(target = "isOpen", constant = "true")
    fun toDefaultTemplate(dto: TemplateCreateDefaultReqServiceDto): Template

    fun toUpdateDefaultReqServiceDto(
        dto: TemplateUpdateDefaultReqControllerDto,
        externalId: UUID
    ): TemplateUpdateDefaultReqServiceDto

    fun toUpdateReqServiceDto(
        dto: TemplateUpdateReqControllerDto,
        owner: String,
        externalId: UUID
    ): TemplateUpdateReqServiceDto

    fun toResServiceDto(template: Template): TemplateResServiceDto

    fun toResControllerDto(dto: TemplateResServiceDto): TemplateResControllerDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateFromDto(dto: TemplateUpdateReqServiceDto, @MappingTarget entity: Template): Template

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateDefaultFromDto(dto: TemplateUpdateDefaultReqServiceDto, @MappingTarget entity: Template): Template
}