package com.gabinote.coffeenote.field.mapping.field

import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.dto.field.controller.*
import com.gabinote.coffeenote.field.dto.field.service.*
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.fieldType.FieldTypeMapper
import org.mapstruct.*
import java.util.*

/**
 * 필드 객체와 DTO 간의 변환을 담당하는 매퍼 인터페이스
 * @see Field 필드 도메인 엔티티
 * @see FieldResServiceDto 필드 응답 서비스 DTO
 * @see FieldResControllerDto 필드 응답 컨트롤러 DTO
 * @see FieldCreateReqControllerDto 필드 생성 요청 컨트롤러 DTO
 * @see FieldCreateReqServiceDto 필드 생성 요청 서비스 DTO
 * @see FieldUpdateReqControllerDto 필드 업데이트 요청 컨트롤러 DTO
 * @see FieldUpdateReqServiceDto 필드 업데이트 요청 서비스 DTO
 * @see FieldCreateDefaultReqControllerDto 기본 필드 생성 요청 컨트롤러 DTO
 * @see FieldCreateDefaultReqServiceDto 기본 필드 생성 요청 서비스 DTO
 * @see FieldUpdateDefaultReqControllerDto 기본 필드 업데이트 요청 컨트롤러 DTO
 * @see FieldUpdateDefaultReqServiceDto 기본 필드 업데이트 요청 서비스 DTO
 * @author 황준서
 */
@Mapper(
    componentModel = "spring",
    uses = [AttributeMapper::class, FieldTypeMapper::class]
)
interface FieldMapper {

    //response
    /**
     * 필드 엔티티를 서비스 응답 DTO로 변환
     * @param field 필드 엔티티
     * @return 필드 응답 서비스 DTO
     * @see Field
     * @see FieldResServiceDto
     */
//    @Mapping(source = "default", target = "isDefault")
    fun toResServiceDto(field: Field): FieldResServiceDto

    /**
     * 서비스 응답 DTO를 컨트롤러 응답 DTO로 변환
     * @param dto 필드 응답 서비스 DTO
     * @return 필드 응답 컨트롤러 DTO
     * @see FieldResServiceDto
     * @see FieldResControllerDto
     */
//    @Mapping(source = "default", target = "isDefault")
    fun toResControllerDto(dto: FieldResServiceDto): FieldResControllerDto


    //create req
    /**
     * 컨트롤러 생성 요청 DTO를 서비스 생성 요청 DTO로 변환
     * @param dto 필드 생성 요청 컨트롤러 DTO
     * @param owner 필드 소유자
     * @return 필드 생성 요청 서비스 DTO
     * @see FieldCreateReqControllerDto
     * @see FieldCreateReqServiceDto
     */
    fun toCreateReqServiceDto(dto: FieldCreateReqControllerDto, owner: String): FieldCreateReqServiceDto

    /**
     * 서비스 생성 요청 DTO를 필드 엔티티로 변환
     * @param dto 필드 생성 요청 서비스 DTO
     * @return 필드 엔티티
     * @see FieldCreateReqServiceDto
     * @see Field
     */
    @Mapping(target = "isDefault", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    fun toField(dto: FieldCreateReqServiceDto): Field

    //update req
    /**
     * 컨트롤러 업데이트 요청 DTO를 서비스 업데이트 요청 DTO로 변환
     * @param dto 필드 업데이트 요청 컨트롤러 DTO
     * @param externalId 필드 외부 식별자
     * @param owner 필드 소유자
     * @return 필드 업데이트 요청 서비스 DTO
     * @see FieldUpdateReqControllerDto
     * @see FieldUpdateReqServiceDto
     */
    fun toUpdateReqServiceDto(
        dto: FieldUpdateReqControllerDto,
        externalId: UUID,
        owner: String
    ): FieldUpdateReqServiceDto

    /**
     * 서비스 업데이트 요청 DTO로 필드 엔티티 업데이트
     * @param dto 필드 업데이트 요청 서비스 DTO
     * @param to 업데이트할 필드 엔티티
     * @return 업데이트된 필드 엔티티
     * @see FieldUpdateReqServiceDto
     * @see Field
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateFromDto(dto: FieldUpdateReqServiceDto, @MappingTarget to: Field): Field


    //create default req
    /**
     * 컨트롤러 기본 필드 생성 요청 DTO를 서비스 기본 필드 생성 요청 DTO로 변환
     * @param dto 기본 필드 생성 요청 컨트롤러 DTO
     * @return 기본 필드 생성 요청 서비스 DTO
     * @see FieldCreateDefaultReqControllerDto
     * @see FieldCreateDefaultReqServiceDto
     */
    fun toCreateDefaultReqServiceDto(dto: FieldCreateDefaultReqControllerDto): FieldCreateDefaultReqServiceDto

    /**
     * 서비스 기본 필드 생성 요청 DTO를 필드 엔티티로 변환
     * @param dto 기본 필드 생성 요청 서비스 DTO
     * @return 필드 엔티티
     * @see FieldCreateDefaultReqServiceDto
     * @see Field
     */
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "isDefault", constant = "true")
    @Mapping(target = "id", ignore = true)
    fun toFieldDefault(dto: FieldCreateDefaultReqServiceDto): Field

    // update default req
    /**
     * 컨트롤러 기본 필드 업데이트 요청 DTO를 서비스 기본 필드 업데이트 요청 DTO로 변환
     * @param dto 기본 필드 업데이트 요청 컨트롤러 DTO
     * @param externalId 필드 외부 식별자
     * @return 기본 필드 업데이트 요청 서비스 DTO
     * @see FieldUpdateDefaultReqControllerDto
     * @see FieldUpdateDefaultReqServiceDto
     */
    fun toUpdateDefaultReqServiceDto(
        dto: FieldUpdateDefaultReqControllerDto,
        externalId: UUID
    ): FieldUpdateDefaultReqServiceDto

    /**
     * 서비스 기본 필드 업데이트 요청 DTO로 필드 엔티티 업데이트
     * @param dto 기본 필드 업데이트 요청 서비스 DTO
     * @param to 업데이트할 필드 엔티티
     * @return 업데이트된 필드 엔티티
     * @see FieldUpdateDefaultReqServiceDto
     * @see Field
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    fun updateFromDefaultDto(dto: FieldUpdateDefaultReqServiceDto, @MappingTarget to: Field): Field

}