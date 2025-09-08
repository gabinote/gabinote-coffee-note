package com.gabinote.coffeenote.field.service.field

import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.domain.field.FieldRepository
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeRegistry
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.field.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.field.dto.field.service.*
import com.gabinote.coffeenote.field.enums.userSearch.FieldAdminSearchScope
import com.gabinote.coffeenote.field.enums.userSearch.FieldUserSearchScope
import com.gabinote.coffeenote.field.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.field.mapping.field.FieldMapper
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.util.*

/**
 * 필드 서비스
 * 필드의 생성, 조회, 수정, 삭제 기능을 제공하는 서비스
 * @author 황준서
 */
@Service
class FieldService(
    private val fieldRepository: FieldRepository,
    private val fieldMapper: FieldMapper,
    private val attributeMapper: AttributeMapper,
    private val fieldTypeRegistry: FieldTypeRegistry,
) {

    /**
     * 외부 식별자로 필드를 조회하는 내부 메서드
     * @param externalId 필드 외부 식별자
     * @return 조회된 필드
     * @throws ResourceNotFound 해당 식별자의 필드가 없는 경우
     */
    fun fetchByExternalId(externalId: UUID): Field {
        return fieldRepository.findByExternalId(externalId.toString())
            ?: throw ResourceNotFound(name = "Field", identifier = externalId.toString(), identifierType = "externalId")
    }

    /**
     * 외부 식별자로 필드를 조회
     * @param externalId 필드 외부 식별자
     * @return 필드 응답 DTO
     */
    fun getByExternalId(externalId: UUID): FieldResServiceDto {
        val data = fetchByExternalId(externalId)
        return fieldMapper.toResServiceDto(field = data)
    }

    /**
     * 외부 식별자로 기본 필드를 조회
     * @param externalId 필드 외부 식별자
     * @return 필드 응답 DTO
     * @throws ResourceNotFound 해당 식별자의 기본 필드가 없는 경우
     */
    fun getDefaultByExternalId(externalId: UUID): FieldResServiceDto {
        val data = fetchByExternalId(externalId)
        checkIsDefault(field = data)
        return fieldMapper.toResServiceDto(field = data)
    }

    /**
     * 외부 식별자로 소유 필드를 조회
     * @param externalId 필드 외부 식별자
     * @param executor 조회 요청자(소유자)
     * @return 필드 응답 DTO
     * @throws ResourceNotFound 해당 식별자의 소유 필드가 없는 경우
     */
    fun getOwnedByExternalId(externalId: UUID, executor: String): FieldResServiceDto {
        val data = fetchByExternalId(externalId)
        checkOwnership(data, executor)
        return fieldMapper.toResServiceDto(field = data)
    }

    /**
     * 모든 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @return 필드 응답 DTO 슬라이스
     */
    fun getAll(pageable: Pageable): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllBy(pageable)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    /**
     * 사용자 검색 범위에 따라 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @param scope 필드 검색 범위
     * @param executor 조회 요청자
     * @return 필드 응답 DTO 슬라이스
     */
    fun getAllByUserScope(
        pageable: Pageable,
        scope: FieldUserSearchScope,
        executor: String
    ): Slice<FieldResServiceDto> {
        return when (scope) {
            FieldUserSearchScope.ALL -> getAllOwnedOrDefault(pageable = pageable, executor = executor)
            FieldUserSearchScope.OWNED -> getAllOwned(pageable = pageable, executor = executor)
            FieldUserSearchScope.DEFAULT -> getAllDefault(pageable = pageable)
        }
    }

    /**
     * 관리자 검색 범위에 따라 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @param scope 필드 검색 범위
     * @return 필드 응답 DTO 슬라이스
     */
    fun getAllByAdminScope(
        pageable: Pageable,
        scope: FieldAdminSearchScope
    ): Slice<FieldResServiceDto> {
        return when (scope) {
            FieldAdminSearchScope.ALL -> getAll(pageable = pageable)
            FieldAdminSearchScope.DEFAULT -> getAllDefault(pageable = pageable)
        }
    }

    /**
     * 모든 기본 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @return 필드 응답 DTO 슬라이스
     */
    fun getAllDefault(pageable: Pageable): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllByDefault(pageable = pageable)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    /**
     * 특정 사용자가 소유한 모든 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @param executor 조회 요청자(소유자)
     * @return 필드 응답 DTO 슬라이스
     */
    fun getAllOwned(pageable: Pageable, executor: String): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllByOwner(pageable = pageable, owner = executor)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    /**
     * 특정 사용자가 소유하거나 기본 필드인 모든 필드를 페이징하여 조회
     * @param pageable 페이지 정보
     * @param executor 조회 요청자(소유자)
     * @return 필드 응답 DTO 슬라이스
     */
    fun getAllOwnedOrDefault(pageable: Pageable, executor: String): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllByDefaultOrOwner(pageable = pageable, owner = executor)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    /**
     * 외부 식별자로 필드 삭제
     * @param externalId 필드 외부 식별자
     * @throws ResourceNotFound 해당 식별자의 필드가 없는 경우
     */
    fun deleteByExternalId(externalId: UUID) {
        if (fieldRepository.deleteByExternalId(externalId.toString()).isEmpty()) {
            throw ResourceNotFound(name = "Field", identifier = externalId.toString(), identifierType = "externalId")
        }
    }

    /**
     * 특정 사용자가 소유한 필드 삭제
     * @param externalId 필드 외부 식별자
     * @param executor 삭제 요청자(소유자)
     * @throws ResourceNotFound 해당 식별자의 소유 필드가 없는 경우
     */
    fun deleteOwnedByExternalId(externalId: UUID, executor: String) {
        val data = fetchByExternalId(externalId)
        checkOwnership(field = data, owner = executor)
        fieldRepository.delete(data)
    }

    /**
     * 기본 필드 삭제
     * @param externalId 필드 외부 식별자
     * @throws ResourceNotFound 해당 식별자의 기본 필드가 없는 경우
     */
    fun deleteDefaultByExternalId(externalId: UUID) {
        val data = fetchByExternalId(externalId)
        checkIsDefault(field = data)
        fieldRepository.delete(data)
    }

    /**
     * 사용자 소유 필드 생성
     * @param dto 필드 생성 요청 DTO
     * @return 생성된 필드 응답 DTO
     */
    fun createOwnedField(dto: FieldCreateReqServiceDto): FieldResServiceDto {
        // TODO : Config 서버 통해서 유저 필드 제한 수 가져오기
        val newField = fieldMapper.toField(dto)
        return createNewField(newField = newField, type = dto.type, attributes = dto.attributes)
    }

    /**
     * 사용자 소유 필드 업데이트
     * @param dto 필드 업데이트 요청 DTO
     * @return 업데이트된 필드 응답 DTO
     * @throws ResourceNotFound 해당 식별자의 소유 필드가 없는 경우
     */
    fun updateOwnedField(dto: FieldUpdateReqServiceDto): FieldResServiceDto {
        val existingField = fetchByExternalId(dto.externalId)

        checkOwnership(field = existingField, owner = dto.owner)

        fieldMapper.updateFromDto(dto, existingField)

        updateAttributesIfNeeded(field = existingField, updateAttributes = dto.attributes)
        val savedField = fieldRepository.save(existingField)
        return fieldMapper.toResServiceDto(field = savedField)
    }

    /**
     * 기본 필드 생성
     * @param dto 기본 필드 생성 요청 DTO
     * @return 생성된 필드 응답 DTO
     */
    fun createDefaultField(dto: FieldCreateDefaultReqServiceDto): FieldResServiceDto {
        val newField = fieldMapper.toFieldDefault(dto)
        return createNewField(newField = newField, type = dto.type, attributes = dto.attributes)
    }

    /**
     * 기본 필드 업데이트
     * @param dto 기본 필드 업데이트 요청 DTO
     * @return 업데이트된 필드 응답 DTO
     * @throws ResourceNotFound 해당 식별자의 기본 필드가 없는 경우
     */
    fun updateDefaultField(dto: FieldUpdateDefaultReqServiceDto): FieldResServiceDto {
        val existingField = fetchByExternalId(dto.externalId)
        checkIsDefault(field = existingField)
        fieldMapper.updateFromDefaultDto(dto, existingField)

        updateAttributesIfNeeded(field = existingField, updateAttributes = dto.attributes)
        val savedField = fieldRepository.save(existingField)
        return fieldMapper.toResServiceDto(field = savedField)
    }

    /**
     * 새 필드 생성 공통 로직
     * @param newField 생성할 필드 엔티티
     * @param type 필드 타입
     * @param attributes 속성 목록
     * @return 생성된 필드 응답 DTO
     */
    private fun createNewField(
        newField: Field,
        type: String,
        attributes: Set<AttributeCreateReqServiceDto>
    ): FieldResServiceDto {
        val fieldType = fieldTypeRegistry.fromString(type)

        //attribute 검증
        val attributes = attributes.map { attributeMapper.toAttribute(it) }.toSet()
        checkAttributes(fieldType = fieldType, attributes = attributes)


        newField.changeAttributes(attributes)

        val savedField = fieldRepository.save(newField)
        return fieldMapper.toResServiceDto(field = savedField)
    }

    /**
     * 필드 소유권 확인
     * @param field 확인할 필드
     * @param owner 소유자
     * @throws ResourceNotFound 소유자가 일치하지 않는 경우
     */
    private fun checkOwnership(field: Field, owner: String) {
        if (!field.isOwner(owner)) {
            throw ResourceNotFound(name = "Owned Field", identifier = field.externalId!!, identifierType = "externalId")
        }
    }

    /**
     * 필드가 기본 필드인지 확인
     * @param field 확인할 필드
     * @throws ResourceNotFound 기본 필드가 아닌 경우
     */
    private fun checkIsDefault(field: Field) {
        if (!field.default) {
            throw ResourceNotFound("Default Field", identifier = field.externalId!!, identifierType = "externalId")
        }
    }

    /**
     * 필요한 경우 필드 속성 업데이트
     * @param field 업데이트할 필드
     * @param updateAttributes 업데이트할 속성 목록
     * @throws ResourceNotValid 속성 유효성 검사 실패 시
     */
    private fun updateAttributesIfNeeded(field: Field, updateAttributes: Set<AttributeUpdateReqServiceDto>) {
        val newAttributes = updateAttributes.map { attributeMapper.toAttribute(it) }.toSet()
        if (newAttributes.isEmpty() || field.attributes == newAttributes) {
            return
        }

        val errors = updateFromNewAttribute(existsAttribute = field.attributes, newAttributes = newAttributes)
        if (errors.isNotEmpty()) {
            throw ResourceNotValid(name = "Field Attribute", reasons = errors)
        }
        val fieldType = fieldTypeRegistry.fromString(field.type)
        checkAttributes(fieldType = fieldType, attributes = field.attributes)
    }

    /**
     * 새 속성으로 기존 속성 업데이트
     * @param existsAttribute 기존 속성 집합
     * @param newAttributes 새 속성 집합
     * @return 오류 메시지 목록
     */
    private fun updateFromNewAttribute(existsAttribute: Set<Attribute>, newAttributes: Set<Attribute>): List<String> {
        val errors = mutableListOf<String>()
        var raisedError = false
        val existsMap = existsAttribute.associateBy { it.key }


        for (newAttribute in newAttributes) {

            val target = existsMap[newAttribute.key]

            if (target == null) {
                errors.add("Unknown attribute key: ${newAttribute.key}")
                raisedError = true
                continue
            }

            if (raisedError) {
                continue
            }

            target.changeValue(newAttribute.value)
        }
        return errors
    }

    /**
     * 필드 속성 유효성 검사
     * @param fieldType 필드 타입
     * @param attributes 검사할 속성 집합
     * @throws ResourceNotValid 유효성 검사 실패 시
     */
    private fun checkAttributes(fieldType: FieldType, attributes: Set<Attribute>) {
        val res = fieldType.validationAttributes(attributes)
        if (res.any { !it.valid }) {
            val errors = res.filter { !it.valid }.map { it.message!! }
            throw ResourceNotValid(name = "Field Attribute", reasons = errors)
        }
    }

}