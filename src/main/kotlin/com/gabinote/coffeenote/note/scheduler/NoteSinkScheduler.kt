package com.gabinote.coffeenote.note.scheduler

import com.gabinote.coffeenote.common.util.time.TimeProvider
import com.gabinote.coffeenote.note.service.noteSync.NoteFieldIndexSyncService
import com.gabinote.coffeenote.note.service.noteSync.NoteIndexSyncService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled

import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
class NoteSinkScheduler(
    private val noteIndexSyncService: NoteIndexSyncService,
    private val noteFieldIndexSyncService: NoteFieldIndexSyncService,
    private val timeProvider: TimeProvider,


    @Value("\${gabinote.note.sink.note-index.minor.batch-size}")
    private val minorNoteIndexSinkBatchSize: Int,

    @Value("\${gabinote.note.sink.note-index.major.batch-size}")
    private val majorNoteIndexSinkBatchSize: Int,

    @Value("\${gabinote.note.sink.note-field-index.minor.batch-size}")
    private val minorNoteFieldIndexSinkBatchSize: Int,

    @Value("\${gabinote.note.sink.note-field-index.major.batch-size}")
    private val majorNoteFieldIndexSinkBatchSize: Int,
) {


    @Scheduled(cron = "\${gabinote.note.sink.note-index.minor.cron}")
    fun runMinorNoteIndexSink() {

        // 현재 시간 기준 정시 - 10분 부터 1시간 전까지의 노트를 싱크
        // 동기화 진행중일 수 있으니 10분 정도 여유를 둠
        val range = calMinorSyncRange()
        logger.info { "Start minor note Index Sync with Range start=${range.start} end=${range.end}" }
        noteIndexSyncService.sinkCurrentNotes(
            batchSize = minorNoteIndexSinkBatchSize,
            syncRangeStart = range.start,
            syncRangeEnd = range.end,
        )
    }

    @Scheduled(cron = "\${gabinote.note.sink.note-index.major.cron}")
    fun runMajorNoteIndexSink() {
        val start = calMajorSyncRange()
        logger.info { "Start Major note index sync with range start=$start" }
        noteIndexSyncService.sinkAllNotes(
            batchSize = majorNoteIndexSinkBatchSize,
            syncRangeStart = start,
        )
    }

    @Scheduled(cron = "\${gabinote.note.sink.note-field-index.minor.cron}")
    fun runMinorNoteFieldIndexSink() {
        val range = calMinorSyncRange()
        logger.info { "Start minor note field index Sync with Range start=${range.start} end=${range.end}" }
        noteFieldIndexSyncService.sinkCurrentNotes(
            batchSize = minorNoteFieldIndexSinkBatchSize,
            syncRangeStart = range.start,
            syncRangeEnd = range.end,
        )
    }

    @Scheduled(cron = "\${gabinote.note.sink.note-field-index.major.cron}")
    fun runMajorNoteFieldIndexSink() {
        val start = calMajorSyncRange()
        logger.info { "Start Major note field index sync with range start=$start" }
        noteFieldIndexSyncService.sinkAllNotes(
            batchSize = majorNoteFieldIndexSinkBatchSize,
            syncRangeStart = start,
        )
    }

    private fun calMinorSyncRange(): SyncRange {
        val now = timeProvider.now()
        val syncRangeEnd = now.withMinute(0).withSecond(0).withNano(0).minusMinutes(10)
        val syncRangeStart = syncRangeEnd.minusHours(1)
        return SyncRange(
            end = syncRangeEnd,
            start = syncRangeStart,
        )
    }

    private fun calMajorSyncRange(): LocalDateTime {
        val now = timeProvider.now()
        return now.withMinute(0).withSecond(0).withNano(0).minusMinutes(10).minusHours(2)

    }

    data class SyncRange(
        val end: LocalDateTime,
        val start: LocalDateTime,
    )


}