package com.gabinote.coffeenote.field.domain.fieldType

import org.springframework.stereotype.Component
import java.util.Locale.getDefault

/**
 * 필드 타입 인스턴스들을 관리하는 레지스트리 클래스
 * 모든 필드 타입을 등록하고 조회하는 기능 제공
 * @author 황준서 (hzser123@gmail.com)
 * @since 2025-09-08
 */
@Component
class FieldTypeRegistry(
    /**
     * 등록된 필드 타입 목록
     */
    private val fieldTypes: List<FieldType> = listOf(
        DateField,
        DropDownField,
        LongTextField,
        MultiSelectField,
        NumberField,
        ScoreField,
        ShortTextField,
        TimeField,
        ToggleField,
        ImageField,
    )
) {
    /**
     * 필드 타입을 키로 조회할 수 있는 맵
     */
    private val typeMap: Map<String, FieldType> = fieldTypes.associateBy { it.key }

    /**
     * 문자열 키로 필드 타입을 조회
     * @param key 필드 타입 키
     * @return 해당하는 필드 타입
     * @throws IllegalArgumentException 존재하지 않는 필드 타입 키인 경우
     */
    fun fromString(key: String): FieldType =
        typeMap[key] ?: throw IllegalArgumentException("Unknown field type key: $key")

    /**
     * 모든 등록된 필드 타입 목록 반환
     * @return 필드 타입 컬렉션
     */
    fun allTypes(): Collection<FieldType> = typeMap.values

    /**
     * 주어진 키가 유효한 필드 타입인지 확인
     * @param key 확인할 필드 타입 키
     * @return 유효한 필드 타입 여부
     */
    fun isFieldType(key: String): Boolean = typeMap.containsKey(key.uppercase(getDefault()))
}