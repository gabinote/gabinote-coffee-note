package com.gabinote.coffeenote.note.event.noteCreated

object NoteCreateEventHelper {
    const val NOTE_CHANGE_TOPIC = "coffeenote.note.change"
    const val NOTE_CREATED_TOPIC = "coffeenote.note.created"
    const val NOTE_DELETED_TOPIC = "coffeenote.note.deleted"
    const val NOTE_CREATED_TOPIC_DLQ = "$NOTE_CREATED_TOPIC.dlq"
    const val NOTE_DELETED_TOPIC_DLQ = "$NOTE_DELETED_TOPIC.dlq"
    const val NOTE_INDEX_GROUP_ID = "coffeenote-index-sink-consumer-group"
    const val NOTE_FIELD_INDEX_GROUP_ID = "coffeenote-field-index-sink-consumer-group"
}