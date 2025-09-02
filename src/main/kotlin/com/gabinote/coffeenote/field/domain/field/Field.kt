package com.gabinote.coffeenote.field.domain.field

import com.gabinote.coffeenote.field.domain.attribute.Attribute
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "fields")
data class Field(
    @Id
    val id: ObjectId? = null,
    var externalId: String? = null,
    var default: Boolean = false,
    var name: String,
    var icon: String,
    var type: String = "TEXT",
    var attributes: Set<Attribute> = emptySet(),
    var owner: String?,
) {

    fun changeAttributes(newAttributes: Set<Attribute>) {
        this.attributes = newAttributes
    }

    fun isOwner(owner: String) = this.owner == owner

    override fun toString(): String {
        return "Field(id=$id, externalId='$externalId', default=$default, name='$name', icon='$icon', type='$type', attributes=$attributes, owner=$owner)"
    }
}