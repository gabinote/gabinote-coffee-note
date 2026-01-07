package com.gabinote.coffeenote.user.domain.withdrawProcessHistory

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WithdrawProcessHistoryRepository : MongoRepository<WithdrawProcessHistory, ObjectId>