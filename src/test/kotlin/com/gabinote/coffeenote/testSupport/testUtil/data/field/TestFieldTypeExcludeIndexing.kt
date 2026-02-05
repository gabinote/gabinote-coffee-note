package com.gabinote.coffeenote.testSupport.testUtil.data.field

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeAttributeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeKey
import com.gabinote.coffeenote.field.domain.fieldType.FieldTypeValidationResult

object TestFieldTypeExcludeIndexing : FieldType() {
    override val key: FieldTypeKey = FieldTypeKey.SHORT_TEXT
    override val canDisplay: Boolean = true
    override val isExcludeIndexing: Boolean = true

    override val fieldTypeAttributeKeys: Set<FieldTypeAttributeKey> = setOf()

    override fun validationValues(
        values: Set<String>,
        attributes: Set<Attribute>,
    ): List<FieldTypeValidationResult> {
        return listOf(FieldTypeValidationResult(true))
    }
}
