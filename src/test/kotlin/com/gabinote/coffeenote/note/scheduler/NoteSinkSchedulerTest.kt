package com.gabinote.coffeenote.note.scheduler

import com.gabinote.coffeenote.common.util.time.TimeProvider
import com.gabinote.coffeenote.note.service.noteSync.NoteFieldIndexSyncService
import com.gabinote.coffeenote.note.service.noteSync.NoteIndexSyncService
import com.gabinote.coffeenote.testSupport.testTemplate.ServiceTestTemplate
import io.mockk.*
import io.mockk.impl.annotations.MockK
import java.time.LocalDateTime

class NoteSinkSchedulerTest : ServiceTestTemplate() {

    private lateinit var noteSinkScheduler: NoteSinkScheduler

    @MockK
    private lateinit var noteIndexSyncService: NoteIndexSyncService

    @MockK
    private lateinit var noteFieldIndexSyncService: NoteFieldIndexSyncService

    @MockK
    private lateinit var timeProvider: TimeProvider

    init {
        beforeTest {
            clearAllMocks()
            noteSinkScheduler = NoteSinkScheduler(
                noteIndexSyncService = noteIndexSyncService,
                noteFieldIndexSyncService = noteFieldIndexSyncService,
                timeProvider = timeProvider,
                minorNoteIndexSinkBatchSize = 1000,
                majorNoteIndexSinkBatchSize = 1000,
                minorNoteFieldIndexSinkBatchSize = 100,
                majorNoteFieldIndexSinkBatchSize = 100,
            )
        }

        describe("[Note] NoteSinkScheduler Test") {

            describe("runMinorNoteIndexSink") {

                context("현재 시간이 2025-01-15 03:00:00 일 때") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                    // 예상: syncRangeEnd = 02:50, syncRangeStart = 01:50
                    val expectedSyncRangeEnd = LocalDateTime.of(2025, 1, 15, 2, 50, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 15, 1, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("정시 - 10분부터 1시간 전까지의 범위로 sinkCurrentNotes가 호출된다") {
                        noteSinkScheduler.runMinorNoteIndexSink()

                        verify(exactly = 1) {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = 1000,
                                syncRangeStart = expectedSyncRangeStart,
                                syncRangeEnd = expectedSyncRangeEnd
                            )
                        }
                    }
                }

                context("현재 시간이 2025-01-15 00:00:00 일 때 (자정)") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 0, 0, 0)
                    // 예상: syncRangeEnd = 전날 23:50, syncRangeStart = 전날 22:50
                    val expectedSyncRangeEnd = LocalDateTime.of(2025, 1, 14, 23, 50, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 14, 22, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("날짜를 넘어가는 범위도 올바르게 계산된다") {
                        noteSinkScheduler.runMinorNoteIndexSink()

                        verify(exactly = 1) {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = 1000,
                                syncRangeStart = expectedSyncRangeStart,
                                syncRangeEnd = expectedSyncRangeEnd
                            )
                        }
                    }
                }

                context("현재 시간이 2025-01-15 12:30:45 일 때 (분/초가 있는 시간)") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 12, 30, 45)
                    // 정시로 변환: 12:00 -> -10분 = 11:50, -1시간 = 10:50
                    val expectedSyncRangeEnd = LocalDateTime.of(2025, 1, 15, 11, 50, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 15, 10, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("분/초를 0으로 초기화 후 범위가 계산된다") {
                        noteSinkScheduler.runMinorNoteIndexSink()

                        verify(exactly = 1) {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = 1000,
                                syncRangeStart = expectedSyncRangeStart,
                                syncRangeEnd = expectedSyncRangeEnd
                            )
                        }
                    }
                }
            }

            describe("runMajorNoteIndexSink") {

                context("현재 시간이 2025-01-15 03:00:00 일 때") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                    // 예상: syncRangeStart = 03:00 - 10분 - 2시간 = 00:50
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 15, 0, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteIndexSyncService.sinkAllNotes(
                                batchSize = any(),
                                syncRangeStart = any()
                            )
                        } just runs
                    }

                    it("정시 - 10분 - 2시간 이전의 시작 범위로 sinkAllNotes가 호출된다") {
                        noteSinkScheduler.runMajorNoteIndexSink()

                        verify(exactly = 1) {
                            noteIndexSyncService.sinkAllNotes(
                                batchSize = 1000,
                                syncRangeStart = expectedSyncRangeStart
                            )
                        }
                    }
                }

                context("현재 시간이 2025-01-15 02:00:00 일 때 (날짜 경계)") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 2, 0, 0)
                    // 예상: syncRangeStart = 02:00 - 10분 - 2시간 = 전날 23:50
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 14, 23, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteIndexSyncService.sinkAllNotes(
                                batchSize = any(),
                                syncRangeStart = any()
                            )
                        } just runs
                    }

                    it("날짜를 넘어가는 범위도 올바르게 계산된다") {
                        noteSinkScheduler.runMajorNoteIndexSink()

                        verify(exactly = 1) {
                            noteIndexSyncService.sinkAllNotes(
                                batchSize = 1000,
                                syncRangeStart = expectedSyncRangeStart
                            )
                        }
                    }
                }
            }

            describe("runMinorNoteFieldIndexSink") {

                context("현재 시간이 2025-01-15 03:00:00 일 때") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                    val expectedSyncRangeEnd = LocalDateTime.of(2025, 1, 15, 2, 50, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 15, 1, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteFieldIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("정시 - 10분부터 1시간 전까지의 범위로 sinkCurrentNotes가 호출된다") {
                        noteSinkScheduler.runMinorNoteFieldIndexSink()

                        verify(exactly = 1) {
                            noteFieldIndexSyncService.sinkCurrentNotes(
                                batchSize = 100,
                                syncRangeStart = expectedSyncRangeStart,
                                syncRangeEnd = expectedSyncRangeEnd
                            )
                        }
                    }
                }

                context("현재 시간이 2025-01-15 00:00:00 일 때 (자정)") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 0, 0, 0)
                    val expectedSyncRangeEnd = LocalDateTime.of(2025, 1, 14, 23, 50, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 14, 22, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteFieldIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("날짜를 넘어가는 범위도 올바르게 계산된다") {
                        noteSinkScheduler.runMinorNoteFieldIndexSink()

                        verify(exactly = 1) {
                            noteFieldIndexSyncService.sinkCurrentNotes(
                                batchSize = 100,
                                syncRangeStart = expectedSyncRangeStart,
                                syncRangeEnd = expectedSyncRangeEnd
                            )
                        }
                    }
                }
            }

            describe("runMajorNoteFieldIndexSink") {

                context("현재 시간이 2025-01-15 03:00:00 일 때") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 15, 0, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteFieldIndexSyncService.sinkAllNotes(
                                batchSize = any(),
                                syncRangeStart = any()
                            )
                        } just runs
                    }

                    it("정시 - 10분 - 2시간 이전의 시작 범위로 sinkAllNotes가 호출된다") {
                        noteSinkScheduler.runMajorNoteFieldIndexSink()

                        verify(exactly = 1) {
                            noteFieldIndexSyncService.sinkAllNotes(
                                batchSize = 100,
                                syncRangeStart = expectedSyncRangeStart
                            )
                        }
                    }
                }

                context("현재 시간이 2025-01-15 02:00:00 일 때 (날짜 경계)") {
                    val currentTime = LocalDateTime.of(2025, 1, 15, 2, 0, 0)
                    val expectedSyncRangeStart = LocalDateTime.of(2025, 1, 14, 23, 50, 0)

                    beforeTest {
                        every { timeProvider.now() } returns currentTime
                        every {
                            noteFieldIndexSyncService.sinkAllNotes(
                                batchSize = any(),
                                syncRangeStart = any()
                            )
                        } just runs
                    }

                    it("날짜를 넘어가는 범위도 올바르게 계산된다") {
                        noteSinkScheduler.runMajorNoteFieldIndexSink()

                        verify(exactly = 1) {
                            noteFieldIndexSyncService.sinkAllNotes(
                                batchSize = 100,
                                syncRangeStart = expectedSyncRangeStart
                            )
                        }
                    }
                }
            }

            describe("배치 사이즈 설정 검증") {

                context("Minor Note Index Sink 호출 시") {
                    beforeTest {
                        every { timeProvider.now() } returns LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                        every {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("설정된 배치 사이즈 1000으로 호출된다") {
                        noteSinkScheduler.runMinorNoteIndexSink()

                        verify {
                            noteIndexSyncService.sinkCurrentNotes(
                                batchSize = 1000,
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        }
                    }
                }

                context("Major Note Index Sink 호출 시") {
                    beforeTest {
                        every { timeProvider.now() } returns LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                        every {
                            noteIndexSyncService.sinkAllNotes(
                                batchSize = any(),
                                syncRangeStart = any()
                            )
                        } just runs
                    }

                    it("설정된 배치 사이즈 1000으로 호출된다") {
                        noteSinkScheduler.runMajorNoteIndexSink()

                        verify {
                            noteIndexSyncService.sinkAllNotes(
                                batchSize = 1000,
                                syncRangeStart = any()
                            )
                        }
                    }
                }

                context("Minor Note Field Index Sink 호출 시") {
                    beforeTest {
                        every { timeProvider.now() } returns LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                        every {
                            noteFieldIndexSyncService.sinkCurrentNotes(
                                batchSize = any(),
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        } just runs
                    }

                    it("설정된 배치 사이즈 100으로 호출된다") {
                        noteSinkScheduler.runMinorNoteFieldIndexSink()

                        verify {
                            noteFieldIndexSyncService.sinkCurrentNotes(
                                batchSize = 100,
                                syncRangeStart = any(),
                                syncRangeEnd = any()
                            )
                        }
                    }
                }

                context("Major Note Field Index Sink 호출 시") {
                    beforeTest {
                        every { timeProvider.now() } returns LocalDateTime.of(2025, 1, 15, 3, 0, 0)
                        every {
                            noteFieldIndexSyncService.sinkAllNotes(
                                batchSize = any(),
                                syncRangeStart = any()
                            )
                        } just runs
                    }

                    it("설정된 배치 사이즈 100으로 호출된다") {
                        noteSinkScheduler.runMajorNoteFieldIndexSink()

                        verify {
                            noteFieldIndexSyncService.sinkAllNotes(
                                batchSize = 100,
                                syncRangeStart = any()
                            )
                        }
                    }
                }
            }
        }
    }
}
