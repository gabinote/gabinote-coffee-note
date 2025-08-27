package com.gabinote.coffeenote.field.domain.field

import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FieldRepository : MongoRepository<Field, ObjectId> {

    fun findAllByDefault(default: Boolean = true, pageable: Pageable): Slice<Field>
    fun findAllByDefaultOrOwner(default: Boolean = true, owner: String, pageable: Pageable): Slice<Field>
    fun findAllByOwner(owner: String, pageable: Pageable): Slice<Field>
    fun findByExternalId(externalId: String): Field?
    fun deleteByExternalId(externalId: String): List<Field>
}