package com.gabinote.coffeenote.user.service.withdrawRequest

import com.gabinote.coffeenote.common.util.exception.service.ServerError
import com.gabinote.coffeenote.policy.domain.policy.PolicyKey
import com.gabinote.coffeenote.policy.service.policy.PolicyService
import com.gabinote.coffeenote.user.domain.withdrawRequest.WithdrawPurgeStatus
import com.gabinote.coffeenote.user.domain.withdrawRequest.WithdrawRequest
import com.gabinote.coffeenote.user.domain.withdrawRequest.WithdrawRequestRepository
import com.gabinote.coffeenote.user.service.keycloakUser.KeycloakUserService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class WithdrawRequestService(
    private val withdrawRequestRepository: WithdrawRequestRepository,
    private val keycloakUserService: KeycloakUserService,
    private val policyService: PolicyService,
) {

    fun fetchByUid(uid: UUID): WithdrawRequest {
        return withdrawRequestRepository.findByUid(uid.toString())
            ?: throw ServerError("UserWithdraw with uid $uid not found")
    }

    @Transactional
    fun create(uid: UUID): WithdrawRequest {
        val userEmail = keycloakUserService.getUserEmail(uid.toString())
        val withdrawRequest = WithdrawRequest(
            uid = uid.toString(),
            email = userEmail,
            purgeStatus = WithdrawPurgeStatus.PENDING.value
        )
        val saved = withdrawRequestRepository.save(withdrawRequest)
        return saved
    }

    private fun calCutoffCreatedDate(targetTime: LocalDateTime): LocalDateTime {
        val cutOff = policyService.getByKey(PolicyKey.USER_PURGE_CUTOFF_DAYS).toLong()
        return targetTime.minusDays(cutOff)
    }


    fun getAllPendingRequests(
        pageable: Pageable,
        targetTime: LocalDateTime,
        status: WithdrawPurgeStatus,
    ): List<WithdrawRequest> {
        val cutoffCreatedDate = calCutoffCreatedDate(targetTime)
        return withdrawRequestRepository.findAllByPurgeStatusAndCreatedDateLessThanEqual(
            status.value,
            cutoffCreatedDate,
            pageable
        )
    }

    fun getCntPendingRequests(targetTime: LocalDateTime, status: WithdrawPurgeStatus): Long {
        val cutoffCreatedDate = calCutoffCreatedDate(targetTime)
        return withdrawRequestRepository.countAllByPurgeStatusAndCreatedDateLessThanEqual(
            status.value,
            cutoffCreatedDate
        )
    }

    @Transactional
    fun updatePurgeStatus(old: WithdrawRequest, newStatus: WithdrawPurgeStatus, newRetryCnt: Long?) {
        old.updateStatus(newStatus)
        newRetryCnt?.let {
            old.updateTryCnt(newRetryCnt)
        }
        withdrawRequestRepository.save(old)
    }


}