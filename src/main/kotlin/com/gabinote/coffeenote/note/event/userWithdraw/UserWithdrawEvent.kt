package com.gabinote.coffeenote.note.event.userWithdraw

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserWithdrawEvent(
    val uid: String,
)