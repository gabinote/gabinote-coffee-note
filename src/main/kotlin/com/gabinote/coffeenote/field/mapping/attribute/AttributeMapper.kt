package com.gabinote.coffeenote.field.mapping.attribute

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeCreateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeResControllerDto
import com.gabinote.coffeenote.field.dto.attribute.controller.AttributeUpdateReqControllerDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeResServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import org.mapstruct.Mapper

/**
 * 속성 객체와 DTO 간의 변환을 담당하는 매퍼 인터페이스
 * @see Attribute 속성 도메인 엔티티
 * @see AttributeResServiceDto 속성 응답 서비스 DTO
 * @see AttributeResControllerDto 속성 응답 컨트롤러 DTO
 * @see AttributeCreateReqControllerDto 속성 생성 요청 컨트롤러 DTO
 * @see AttributeCreateReqServiceDto 속성 생성 요청 서비스 DTO
 * @see AttributeUpdateReqControllerDto 속성 업데이트 요청 컨트롤러 DTO
 * @see AttributeUpdateReqServiceDto 속성 업데이트 요청 서비스 DTO
 * @author 황준서
 */
@Mapper(
    componentModel = "spring",
)
interface AttributeMapper {

    /**
     * 속성 엔티티를 서비스 응답 DTO로 변환
     * @param attribute 속성 엔티티
     * @return 속성 응답 서비스 DTO
     * @see Attribute
     * @see AttributeResServiceDto
     */
    fun toAttributeResServiceDto(
        attribute: Attribute
    ): AttributeResServiceDto

    /**
     * 서비스 응답 DTO를 컨트롤러 응답 DTO로 변환
     * @param dto 속성 응답 서비스 DTO
     * @return 속성 응답 컨트롤러 DTO
     * @see AttributeResServiceDto
     * @see AttributeResControllerDto
     */
    fun toAttributeResControllerDto(
        dto: AttributeResServiceDto,
    ): AttributeResControllerDto

    /**
     * 서비스 생성 요청 DTO를 속성 엔티티로 변환
     * @param dto 속성 생성 요청 서비스 DTO
     * @return 속성 엔티티
     * @see AttributeCreateReqServiceDto
     * @see Attribute
     */
    fun toAttribute(
        dto: AttributeCreateReqServiceDto,
    ): Attribute

    /**
     * 서비스 업데이트 요청 DTO를 속성 엔티티로 변환
     * @param dto 속성 업데이트 요청 서비스 DTO
     * @return 속성 엔티티
     * @see AttributeUpdateReqServiceDto
     * @see Attribute
     */
    fun toAttribute(
        dto: AttributeUpdateReqServiceDto,
    ): Attribute

    /**
     * 컨트롤러 생성 요청 DTO를 서비스 생성 요청 DTO로 변환
     * @param dto 속성 생성 요청 컨트롤러 DTO
     * @return 속성 생성 요청 서비스 DTO
     * @see AttributeCreateReqControllerDto
     * @see AttributeCreateReqServiceDto
     */
    fun toAttributeCreateReqServiceDto(
        dto: AttributeCreateReqControllerDto,
    ): AttributeCreateReqServiceDto

    /**
     * 컨트롤러 업데이트 요청 DTO를 서비스 업데이트 요청 DTO로 변환
     * @param dto 속성 업데이트 요청 컨트롤러 DTO
     * @return 속성 업데이트 요청 서비스 DTO
     * @see AttributeUpdateReqControllerDto
     * @see AttributeUpdateReqServiceDto
     */
    fun toAttributeUpdateReqServiceDto(
        dto: AttributeUpdateReqControllerDto,
    ): AttributeUpdateReqServiceDto
}