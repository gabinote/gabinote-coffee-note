package com.gabinote.coffeenote.template.domain.template

import com.gabinote.coffeenote.common.domain.base.BaseSortKey

/**
 * 템플릿 정렬 키를 정의하는 열거형
 * @property key 정렬에 사용되는 속성 키
 * @author 황준서
 */
enum class TemplateSortKey(
    override val key: String
) : BaseSortKey {
    EXTERNAL_ID("externalId"),
    ICON("icon"),
    NAME("name"),
    IS_OPEN("isOpen"),
    OWNER("owner"),
    IS_DEFAULT("isDefault")
}