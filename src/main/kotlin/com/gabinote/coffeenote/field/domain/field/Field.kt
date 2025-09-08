package com.gabinote.coffeenote.field.domain.field

import com.gabinote.coffeenote.common.util.auditor.extId.ExternalId
import com.gabinote.coffeenote.field.domain.attribute.Attribute
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 필드 도메인 엔티티
 * 필드의 기본 정보와 속성을 담고 있는 클래스
 * @author 황준서
 */
@Document(collection = "fields")
data class Field(
    /**
     * 필드의 고유 식별자
     */
    @Id
    val id: ObjectId? = null,

    /**
     * 외부에서 사용되는 필드 식별자
     */
    @ExternalId
    var externalId: String? = null,

    /**
     * 기본 필드 여부
     */
    var default: Boolean = false,

    /**
     * 필드 이름
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
    var type: String = "TEXT",

    /**
     * 필드 속성 집합
     */
    var attributes: Set<Attribute> = emptySet(),

    /**
     * 필드 소유자
     */
    var owner: String?,
) {

    /**
     * 필드 속성을 변경하는 메서드
     * @param newAttributes 새로운 속성 집합
     */
    fun changeAttributes(newAttributes: Set<Attribute>) {
        this.attributes = newAttributes
    }

    /**
     * 주어진 사용자가 필드의 소유자인지 확인하는 메서드
     * @param owner 확인할 소유자
     * @return 소유자 여부
     */
    fun isOwner(owner: String) = this.owner == owner

    override fun toString(): String {
        return "Field(id=$id, externalId='$externalId', default=$default, name='$name', icon='$icon', type='$type', attributes=$attributes, owner=$owner)"
    }
}