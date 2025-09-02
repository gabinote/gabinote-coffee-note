package com.gabinote.coffeenote.field.domain.field

import com.gabinote.coffeenote.common.domain.base.BaseSortKey

enum class FieldSortKey(
    override val key: String,
) : BaseSortKey {
    NAME("name"),
    DEFAULT("default"),
    TYPE("type"),
    OWNER("owner"),
    ID("id"),
    EXTERNAL_ID("externalId");
}