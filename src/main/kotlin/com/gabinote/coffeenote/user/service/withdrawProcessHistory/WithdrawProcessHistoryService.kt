package com.gabinote.coffeenote.user.service.withdrawProcessHistory

import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.user.domain.withdrawProcessHistory.WithdrawProcessHistory
import com.gabinote.coffeenote.user.domain.withdrawProcessHistory.WithdrawProcessHistoryRepository
import com.gabinote.coffeenote.user.service.withdrawRequest.WithdrawRequestService
import org.springframework.stereotype.Service

@Service
class WithdrawProcessHistoryService(
    private val withdrawProcessHistoryRepository: WithdrawProcessHistoryRepository,
    private val withdrawRequestService: WithdrawRequestService,
) {
    fun create(uid: String, process: WithdrawProcess, isPassed: Boolean = true) {
        val withdrawRequest = withdrawRequestService.fetchByUid(uid)
        val withdrawProcessHistory = WithdrawProcessHistory(
            uid = uid,
            requestId = withdrawRequest.id!!,
            process = process.value,
            isPassed = isPassed,
        )
        withdrawProcessHistoryRepository.save(withdrawProcessHistory)
    }
}