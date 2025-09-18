package com.gabinote.coffeenote.template.domain.templateField

import com.gabinote.coffeenote.field.domain.attribute.Attribute

/**
 * 템플릿 필드 도메인 엔티티
 * 템플릿 필드의 기본 정보와 속성을 담고 있는 클래스
 * @author 황준서
 */
data class TemplateField(

    var id: String,

    /**
     * 필드 이름. 나중에 검색시에 해당 필드 이름으로 검색할 수 있음
     */
    var name: String,

    /**
     * 필드 아이콘
     */
    var icon: String,

    /**
     * 필드 타입
     * @see com.gabinote.coffeenote.field.domain.fieldType.FieldType
     */
    var type: String,

    /**
     * 필드 속성 집합
     */
    var attributes: Set<Attribute> = emptySet(),

    /**
     * 템플릿 내 필드 순서
     */
    var order: Int = 0,

    /**
     * 필드 리스트 목록에서 해당 필드를 표시할지 여부
     * 해당 옵션이 활성화 되어있으면, 리스트보기시에 커피노트에서 해당 필드가 표시됨
     * true: 표시, false: 숨김
     */
    @JvmField
    var isDisplay: Boolean = true
) {
    override fun toString(): String {
        return "TemplateField(id='$id', name='$name', icon='$icon', type='$type', attributes=$attributes, order=$order, isDisplay=$isDisplay)"
    }
}