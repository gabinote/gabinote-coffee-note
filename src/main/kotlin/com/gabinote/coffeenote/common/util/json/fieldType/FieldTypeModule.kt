package com.gabinote.coffeenote.common.util.json.fieldType

import com.fasterxml.jackson.databind.module.SimpleModule
import com.gabinote.coffeenote.field.domain.fieldType.FieldType
import org.springframework.stereotype.Component

@Component
class FieldTypeModule(

    private val fieldTypeSerializer: FieldTypeSerializer,
    private val fieldTypeDeserializer: FieldTypeDeserializer
) : SimpleModule() {

    // 모듈 초기화 시 어떤 타입을 어떤 클래스가 처리할지 등록
    init {
        addSerializer(FieldType::class.java, fieldTypeSerializer)
        addDeserializer(FieldType::class.java, fieldTypeDeserializer)
    }
}