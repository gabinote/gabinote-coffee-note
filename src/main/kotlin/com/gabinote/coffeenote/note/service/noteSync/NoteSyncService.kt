package com.gabinote.coffeenote.note.service.noteSync

import com.gabinote.coffeenote.note.service.note.NoteService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

abstract class NoteSyncService<T, S : Enum<*>>(
    protected val noteService: NoteService,
) {


    fun sinkCurrentNotes(
        batchSize: Int = 1000,
        syncRangeStart: LocalDateTime,
        syncRangeEnd: LocalDateTime,
    ) {
        val targetCnt = noteService.getCountWithBetweenModifiedDate(
            startDate = syncRangeStart,
            endDate = syncRangeEnd,
        )

        logger.debug { "found $targetCnt items" }

        val batchPageable = setUpBatchPageable(batchSize = batchSize)
        processCurrBatchNotes(
            batchPageable = batchPageable,
            syncRangeStart = syncRangeStart,
            syncRangeEnd = syncRangeEnd,
            totalCnt = targetCnt,
        )
    }

    fun sinkAllNotes(
        batchSize: Int = 1000,
        syncRangeStart: LocalDateTime,
    ) {
        val targetCnt = noteService.getCountBeforeModifiedDate(
            beforeDate = syncRangeStart,
        )

        logger.debug { "found $targetCnt items" }

        val batchPageable = setUpBatchPageable(batchSize = batchSize)
        processAllBatchNotes(
            batchPageable = batchPageable,
            syncRangeStart = syncRangeStart,
            totalCnt = targetCnt,
        )
    }

    private fun processCurrBatchNotes(
        batchPageable: Pageable,
        syncRangeStart: LocalDateTime,
        syncRangeEnd: LocalDateTime,
        totalCnt: Long,
    ) {
        var processedCnt = 0L

        while (processedCnt < totalCnt) {
            logger.debug { "processing batch with offset $processedCnt / $totalCnt" }

            processCurrNotePerBatch(
                pageable = batchPageable.withPage((processedCnt / batchPageable.pageSize).toInt()),
                syncRangeStart = syncRangeStart,
                syncRangeEnd = syncRangeEnd,
            )

            processedCnt += batchPageable.pageSize
        }
    }

    private fun processAllBatchNotes(
        batchPageable: Pageable,
        syncRangeStart: LocalDateTime,
        totalCnt: Long,
    ) {
        var processedCnt = 0L

        while (processedCnt < totalCnt) {
            logger.debug { "processing batch with offset $processedCnt / $totalCnt" }

            processAllNotePerBatch(
                pageable = batchPageable.withPage((processedCnt / batchPageable.pageSize).toInt()),
                syncRangeStart = syncRangeStart,
            )

            processedCnt += batchPageable.pageSize
        }
    }

    private fun processCurrNotePerBatch(
        pageable: Pageable,
        syncRangeStart: LocalDateTime,
        syncRangeEnd: LocalDateTime,
    ) {
        val notes = fetchCurrNote(
            startDate = syncRangeStart,
            endDate = syncRangeEnd,
            pageable = pageable,
        )

        logger.debug { "fetched ${notes.size} items" }

        val incorrectIndexMap = compareWithNoteIndex(
            notes = notes,
        )

        logger.debug { "found incorrect ${incorrectIndexMap.values.sumOf { it.size }} items" }

        recoverIncorrectIndexes(
            incorrectIndexMap = incorrectIndexMap,
        )
    }

    private fun processAllNotePerBatch(
        pageable: Pageable,
        syncRangeStart: LocalDateTime,
    ) {
        val notes = fetchAllNote(beforeDate = syncRangeStart, pageable = pageable)
        logger.debug { "fetched ${notes.size} items" }

        val incorrectIndexMap = compareWithNoteIndex(
            notes = notes,
        )

        logger.debug { "found incorrect ${incorrectIndexMap.values.sumOf { it.size }} items" }

        recoverIncorrectIndexes(
            incorrectIndexMap = incorrectIndexMap,
        )
    }

    abstract fun compareWithNoteIndex(
        notes: List<T>,
    ): Map<S, List<String>>

    abstract fun recoverIncorrectIndexes(
        incorrectIndexMap: Map<S, List<String>>,
    )

    abstract fun fetchCurrNote(startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): List<T>

    abstract fun fetchAllNote(beforeDate: LocalDateTime, pageable: Pageable): List<T>

    private fun setUpBatchPageable(
        batchSize: Int,
    ) = PageRequest.of(
        0,
        batchSize,
        Sort.by(
            Sort.Direction.ASC,
            "id"
        )
    )


}