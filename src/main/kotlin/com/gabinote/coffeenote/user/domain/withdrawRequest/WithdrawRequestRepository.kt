package com.gabinote.coffeenote.user.domain.withdrawRequest

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WithdrawRequestRepository : MongoRepository<WithdrawRequest, ObjectId> {
    fun findByUid(uid: String): WithdrawRequest?
}