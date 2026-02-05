package com.gabinote.coffeenote.user.service.withdrawRequest

import com.gabinote.coffeenote.common.util.exception.service.ServerError
import com.gabinote.coffeenote.user.domain.withdrawRequest.WithdrawRequest
import com.gabinote.coffeenote.user.domain.withdrawRequest.WithdrawRequestRepository
import org.springframework.stereotype.Service

@Service
class WithdrawRequestService(
    private val withdrawRequestRepository: WithdrawRequestRepository,
) {

    fun fetchByUid(uid: String): WithdrawRequest {
        return withdrawRequestRepository.findByUid(uid)
            ?: throw ServerError("UserWithdraw with uid $uid not found")
    }

}