//package com.gabinote.coffeenote.note.service.noteIndex
//
//import com.gabinote.coffeenote.common.util.time.TimeProvider
//import com.gabinote.coffeenote.note.domain.noteIndex.NoteIndexRepository
//import com.gabinote.coffeenote.note.domain.noteIndex.vo.DateRangeFilter
//import com.gabinote.coffeenote.note.dto.note.domain.NoteFilterCondition
//import com.gabinote.coffeenote.note.dto.note.domain.NoteSearchCondition
//import com.gabinote.coffeenote.note.dto.noteIndex.service.NoteIndexResServiceDto
//import org.springframework.data.domain.Slice
//import org.springframework.stereotype.Service
//
//@Service
//class NoteIndexService(
//    private val noteIndexRepository: NoteIndexRepository,
//    private val noteIndexMapper: NoteIndexMapper,
//    private val timeProvider: TimeProvider,
//) {
//    fun searchByCondition(
//        searchCondition: NoteSearchCondition,
//    ): Slice<NoteIndexResServiceDto> {
//        val indexes = noteIndexRepository.searchNotes(
//            owner = searchCondition.owner,
//            query = searchCondition.query,
//            pageable = searchCondition.pageable,
//            highlightTag = searchCondition.highlightTag
//
//        )
//        return indexes.map {
//            noteIndexMapper.toNoteListResServiceDto(it)
//        }
//    }
//
//    fun filterByCondition(
//        condition: NoteFilterCondition,
//    ): Slice<NoteIndexResServiceDto> {
//
//        val indexes = noteIndexRepository.searchNotesWithFilter(
//            owner = condition.owner,
//            filters = condition.fieldOptions,
//            pageable = condition.pageable,
//            highlightTag = condition.highlightTag,
//            createdDateFilter = DateRangeFilter(
//                startDate = condition.createdDateStart?.toEpochSecond(timeProvider.zoneOffset()),
//                endDate = condition.createdDateEnd?.toEpochSecond(timeProvider.zoneOffset()),
//            ),
//            modifiedDateFilter = DateRangeFilter(
//                startDate = condition.modifiedDateStart?.toEpochSecond(timeProvider.zoneOffset()),
//                endDate = condition.modifiedDateEnd?.toEpochSecond(timeProvider.zoneOffset()),
//            ),
//        )
//
//        return indexes.map {
//            noteIndexMapper.toNoteListResServiceDto(it)
//        }
//
//    }
//}