package com.gabinote.coffeenote.note.service.noteSink

//@Service
//class NoteSinkService(
//    private val noteService: NoteService,
//    private val noteIndexService: NoteIndexService,
//    private val noteFieldIndexService: NoteFieldIndexService,
//) {
//
//    // 한번에 처리할 노트 개수
//    @Value("\${gabinote.note.sink.batch-size}")
//    private lateinit var batchSize: String
//
//
//    // 노트 수집 기준 시간 오프셋
//    // 해당 offset을 기준으로 createdDate가 이전인 노트를 수집
//    // 예: offsetHours = 2 -> 현재 시간에서 2시간 이전에 생성된 노트들 부터 수집
//    @Value("\${gabinote.note.sink.offset-hours}")
//    private lateinit var offsetHours: String
//
//    // 노트 수집 기간 시간
//    // offsetHours부터 collectDurationHours 시간 만큼의 기간 동안 생성된 노트를 수집
//    // 예: offsetHours = 2, collectDurationHours = 3 -> 현재 시간에서 2시간 이전부터 5시간 이전까지 생성된 노트들 수집
//    @Value("\${gabinote.note.sink.collect-duration-hours}")
//    private lateinit var collectDurationHours: String
//
//
//    fun sinkCurrentNotes() {
//
//    }
//
//
//}