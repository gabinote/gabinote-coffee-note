package com.gabinote.coffeenote.user.service.withdrawProcessHistory

import com.gabinote.coffeenote.note.event.userWithdraw.WithdrawProcess
import com.gabinote.coffeenote.user.domain.withdrawProcessHistory.WithdrawProcessHistory
import com.gabinote.coffeenote.user.domain.withdrawProcessHistory.WithdrawProcessHistoryRepository
import com.gabinote.coffeenote.user.domain.withdrawRequest.WithdrawRequest
import com.gabinote.coffeenote.user.service.withdrawRequest.WithdrawRequestService
import org.springframework.stereotype.Service
import java.util.*


@Service
class WithdrawProcessHistoryService(
    private val withdrawProcessHistoryRepository: WithdrawProcessHistoryRepository,
    private val withdrawRequestService: WithdrawRequestService,
) {

    fun create(uid: UUID, process: WithdrawProcess, isPassed: Boolean = true) {
        val withdrawRequest = withdrawRequestService.fetchByUid(uid)
        val withdrawProcessHistory = WithdrawProcessHistory(
            uid = uid.toString(),
            requestId = withdrawRequest.id!!,
            process = process.value,
            isPassed = isPassed,
        )
        withdrawProcessHistoryRepository.save(withdrawProcessHistory)
    }

    fun create(request: WithdrawRequest, isPassed: Boolean = true, process: WithdrawProcess) {
        val withdrawProcessHistory = WithdrawProcessHistory(
            uid = request.uid,
            requestId = request.id!!,
            process = process.value,
            isPassed = isPassed,
        )
        withdrawProcessHistoryRepository.save(withdrawProcessHistory)
    }


}