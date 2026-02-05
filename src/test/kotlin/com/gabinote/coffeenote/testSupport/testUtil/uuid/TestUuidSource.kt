package com.gabinote.coffeenote.testSupport.testUtil.uuid

import com.gabinote.coffeenote.common.util.uuid.UuidSource
import org.springframework.boot.test.context.TestComponent
import java.util.*

@TestComponent
class TestUuidSource : UuidSource {

    // 해당 큐모드 활성화시 고정 uuid 값이 아닌,
    // 큐에 담긴 uuid 값들을 반환함. 이때 큐가 비어있으면 에러가 발생함.
    // 여러 uuid 값이 필요한 테스트 케이스에서 사용
    private var isEnabledQueueMode: Boolean = false

    private val uuidQueue: Queue<UUID> = LinkedList()

    private var isEnabledRNG: Boolean = false

    /**
     * 테스트용 큐모드 활성화 메소드
     * 큐모드를 활성화 하고, 미리 정의된 UUID 리스트를 큐에 담음.
     * @see isEnabledQueueMode
     */
    fun enableQueueMode() {
        isEnabledQueueMode = true
        uuidQueue.clear()
        uuidQueue.addAll(SIMPLE_UUID_LIST)
    }

    fun disableQueueMode() {
        isEnabledQueueMode = false
        uuidQueue.clear()
    }

    fun enableRNGMode() {
        isEnabledRNG = true
    }

    fun disableRNGMode() {
        isEnabledRNG = false
    }

    /**
     * 테스트용 큐모드 활성화 메소드
     * 큐모드를 활성화 하고, 전달된 UUID 리스트를 큐에 담음.
     * @see isEnabledQueueMode
     * @param uuids UUID 리스트
     */
    fun enableQueueMode(uuids: List<UUID>) {
        isEnabledQueueMode = true
        uuidQueue.clear()
        uuidQueue.addAll(uuids)
    }

    /**
     * 테스트용 UUID 메소드
     * 큐모드가 활성화 되어있으면 큐에서 UUID를 꺼내 반환하고, (이때 큐가 비어있으면 에러 발생)
     * 비활성화 되어있으면 고정된 UUID 값을 반환함.
     * @see isEnabledQueueMode
     * @return UUID
     */
    override fun generateUuid(): UUID {
        return if (isEnabledQueueMode) {
            uuidQueue.poll() ?: throw IllegalStateException("UUID queue is empty")
        } else if (isEnabledRNG) {
            UUID.randomUUID()
        } else {
            UUID_STRING
        }
    }

    companion object {
        val UUID_STRING: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val SIMPLE_UUID_LIST: List<UUID> = listOf(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            UUID.fromString("00000000-0000-0000-0000-000000000003"),
            UUID.fromString("00000000-0000-0000-0000-000000000004"),
        )
    }
}