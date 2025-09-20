package com.gabinote.coffeenote.template.domain.template

import com.gabinote.coffeenote.common.util.auditor.extId.ExternalId
import com.gabinote.coffeenote.template.domain.templateField.TemplateField
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 템플릿 도메인 엔티티
 * 노트 템플릿의 기본 정보와 필드들을 담고 있는 클래스
 * @author 황준서
 */
@Document(collection = "templates")
data class Template(
    @Id
    var id: ObjectId? = null,

    @ExternalId
    var externalId: String? = null,

    /**
     * 템플릿 아이콘
     */
    var icon: String,

    /**
     * 템플릿 이름
     */
    var name: String,

    /**
     * 템플릿 설명
     */
    var description: String,

    /**
     * 템플릿 공개 여부
     * true: 공개, false: 비공개
     */
    @JvmField
    var isOpen: Boolean = false,

    /**
     * 템플릿 소유자
     */
    var owner: String? = null,

    /**
     * 기본 템플릿 여부
     */
    @JvmField
    var isDefault: Boolean = false,

    /**
     * 템플릿 필드 목록. Field Domain이 아님에 주의
     * @see TemplateField
     */
    var fields: List<TemplateField> = listOf()
) {

    fun changeFields(newFields: List<TemplateField>) {
        this.fields = newFields
    }

    override fun toString(): String {
        return "Template(id=$id, externalId=$externalId, name='$name', description='$description', isOpen=$isOpen, owner='$owner', fields=$fields)"
    }

}