package com.gabinote.coffeenote.user.domain.withdrawRequest

enum class WithdrawPurgeStatus(val value: String) {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    RETRYING("RETRYING"),
    FAILED("FAILED"),
}