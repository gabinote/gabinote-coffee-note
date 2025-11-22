package com.gabinote.coffeenote.note.mapping.noteHash

import com.gabinote.coffeenote.note.domain.note.Note
import com.gabinote.coffeenote.note.domain.noteHash.NoteHash
import com.gabinote.coffeenote.testSupport.testTemplate.MockkTestTemplate
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider
import com.gabinote.coffeenote.testSupport.testUtil.uuid.TestUuidSource
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [NoteHashMapperImpl::class])
class NoteHashMapperTest : MockkTestTemplate() {
    @Autowired
    lateinit var noteHashMapper: NoteHashMapper

    init {
        describe("[Note] NoteHashMapper Test") {
            describe("NoteHashMapper.toHash") {
                context("Note 엔티티와 hash 문자열이 주어지면,") {
                    val note = Note(
                        id = ObjectId(),
                        externalId = TestUuidSource.UUID_STRING.toString(),
                        title = "Test Note",
                        thumbnail = "test-thumbnail.jpg",
                        createdDate = TestTimeProvider.testDateTime,
                        modifiedDate = TestTimeProvider.testDateTime,
                        fields = emptyList(),
                        displayFields = emptyList(),
                        isOpen = true,
                        owner = "test-owner"
                    )
                    val hash = "abc123def456hash789"
                    val expected = NoteHash(
                        id = null,
                        noteId = note.id!!,
                        hash = "abc123def456hash789",
                        createdDate = TestTimeProvider.testDateTime,
                        owner = note.owner
                    )

                    it("NoteHash 엔티티로 변환되어야 한다.") {
                        val result = noteHashMapper.toHash(note, hash)
                        result shouldBe expected
                    }
                }

            }
        }
    }
}
