package com.gabinote.coffeenote.field.service.field

import com.gabinote.coffeenote.common.domain.attribute.Attribute
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeCreateReqServiceDto
import com.gabinote.coffeenote.common.dto.attribute.service.AttributeUpdateReqServiceDto
import com.gabinote.coffeenote.common.mapping.attribute.AttributeMapper
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotFound
import com.gabinote.coffeenote.common.util.exception.service.ResourceNotValid
import com.gabinote.coffeenote.field.domain.field.Field
import com.gabinote.coffeenote.field.domain.field.FieldRepository
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeRegistry
import com.gabinote.coffeenote.field.dto.field.service.*
import com.gabinote.coffeenote.field.mapping.field.FieldMapper
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.util.*

@Service
class FieldService(
    private val fieldRepository: FieldRepository,
    private val fieldMapper: FieldMapper,
    private val attributeMapper: AttributeMapper,
    private val fieldTypeRegistry: FieldTypeRegistry,
) {

    fun fetchByExternalId(externalId: UUID): Field {
        return fieldRepository.findByExternalId(externalId.toString())
            ?: throw ResourceNotFound(name = "Field", identifier = externalId.toString(), identifierType = "externalId")
    }

    fun getByExternalId(externalId: UUID): FieldResServiceDto {
        val data = fetchByExternalId(externalId)
        return fieldMapper.toResServiceDto(field = data)
    }

    fun getDefaultByExternalId(externalId: UUID): FieldResServiceDto {
        val data = fetchByExternalId(externalId)
        checkIsDefault(field = data)
        return fieldMapper.toResServiceDto(field = data)
    }

    fun getOwnedByExternalId(externalId: UUID, executor: String): FieldResServiceDto {
        val data = fetchByExternalId(externalId)
        checkOwnership(data, executor)
        return fieldMapper.toResServiceDto(field = data)
    }

    fun getAll(pageable: Pageable): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllBy(pageable)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    fun getAllDefault(pageable: Pageable): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllByDefault(pageable = pageable)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    fun getAllOwned(pageable: Pageable, executor: String): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllByOwner(pageable = pageable, owner = executor)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    fun getAllOwnedOrDefault(pageable: Pageable, executor: String): Slice<FieldResServiceDto> {
        val data = fieldRepository.findAllByDefaultOrOwner(pageable = pageable, owner = executor)
        return data.map { fieldMapper.toResServiceDto(it) }
    }

    fun deleteByExternalId(externalId: UUID) {
        if (fieldRepository.deleteByExternalId(externalId.toString()).isEmpty()) {
            throw ResourceNotFound(name = "Field", identifier = externalId.toString(), identifierType = "externalId")
        }
    }

    fun deleteOwnedByExternalId(externalId: UUID, executor: String) {
        val data = fetchByExternalId(externalId)
        checkOwnership(field = data, owner = executor)
        fieldRepository.delete(data)
    }

    fun deleteDefaultByExternalId(externalId: UUID) {
        val data = fetchByExternalId(externalId)
        checkIsDefault(field = data)
        fieldRepository.delete(data)
    }

    fun createOwnedField(dto: FieldCreateReqServiceDto): FieldResServiceDto {
        // TODO : Config 서버 통해서 유저 필드 제한 수 가져오기
        val newField = fieldMapper.toField(dto)
        return createNewField(newField = newField, type = dto.type, attributes = dto.attributes)
    }

    fun updateOwnedField(dto: FieldUpdateReqServiceDto): FieldResServiceDto {
        val existingField = fetchByExternalId(dto.externalId)

        checkOwnership(field = existingField, owner = dto.owner)

        fieldMapper.updateFromDto(dto, existingField)

        updateAttributesIfNeeded(field = existingField, updateAttributes = dto.attributes)
        val savedField = fieldRepository.save(existingField)
        return fieldMapper.toResServiceDto(field = savedField)
    }

    fun createDefaultField(dto: FieldCreateDefaultReqServiceDto): FieldResServiceDto {
        val newField = fieldMapper.toFieldDefault(dto)
        return createNewField(newField = newField, type = dto.type, attributes = dto.attributes)
    }

    fun updateDefaultField(dto: FieldUpdateDefaultReqServiceDto): FieldResServiceDto {
        val existingField = fetchByExternalId(dto.externalId)
        checkIsDefault(field = existingField)
        fieldMapper.updateFromDefaultDto(dto, existingField)

        updateAttributesIfNeeded(field = existingField, updateAttributes = dto.attributes)
        val savedField = fieldRepository.save(existingField)
        return fieldMapper.toResServiceDto(field = savedField)
    }

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

    private fun checkOwnership(field: Field, owner: String) {
        if (!field.isOwner(owner)) {
            throw ResourceNotFound(name = "Owned Field", identifier = field.externalId!!, identifierType = "externalId")
        }
    }

    private fun checkIsDefault(field: Field) {
        if (!field.default) {
            throw ResourceNotFound("Default Field", identifier = field.externalId!!, identifierType = "externalId")
        }
    }

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


    private fun checkAttributes(fieldType: FieldType, attributes: Set<Attribute>) {

        val res = fieldType.validationKey(attributes)
        if (res.any { !it.valid }) {
            val errors = res.filter { !it.valid }.map { it.message!! }
            throw ResourceNotValid(name = "Field Attribute", reasons = errors)
        }
    }


}