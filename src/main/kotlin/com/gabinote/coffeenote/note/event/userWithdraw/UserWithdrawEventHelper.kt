package com.gabinote.coffeenote.note.event.userWithdraw

object UserWithdrawEventHelper {
    const val USER_WITHDRAW_EVENT_TYPE = "ums.user.withdraw.requested"
    const val USER_WITHDRAW_EVENT_TYPE_DLQ = "ums.user.withdraw.requested.DLQ"
    const val USER_WITHDRAW_NOTE_DELETE_GROUP = "coffeenote-user-withdraw-handler-note-delete"
    const val USER_WITHDRAW_NOTE_INDEX_DELETE_GROUP = "coffeenote-user-withdraw-handler-note-index-delete"
    const val USER_WITHDRAW_NOTE_FIELD_INDEX_DELETE_GROUP = "coffeenote-user-withdraw-handler-note-field-index-delete"
}