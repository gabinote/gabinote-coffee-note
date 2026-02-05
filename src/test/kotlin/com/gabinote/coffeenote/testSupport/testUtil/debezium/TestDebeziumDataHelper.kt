package com.gabinote.coffeenote.testSupport.testUtil.debezium

import com.gabinote.coffeenote.common.util.debezium.enums.DebeziumOperation
import com.gabinote.coffeenote.common.util.debezium.response.ChangeMessage
import com.gabinote.coffeenote.common.util.debezium.response.SourceInfo
import com.gabinote.coffeenote.common.util.debezium.response.TransactionInfo
import com.gabinote.coffeenote.testSupport.testUtil.time.TestTimeProvider

object TestDebeziumDataHelper {

    fun createSourceInfo(): SourceInfo = SourceInfo(
        version = "1.9.7.Final",
        connector = "mongodb",
        name = "coffeenote-connector",
        tsMs = TestTimeProvider.testEpochSecond,
        snapshot = "false",
        db = "coffeenote",
        collection = "user",
        ord = 1,
        lsid = null,
        txnNumber = null,
    )

    fun createTransactionInfo(): TransactionInfo = TransactionInfo(
        id = "transaction-id-123",
        totalOrder = 1L,
        dataCollectionOrder = 1L,
    )

    fun <T> createChangeMessage(
        before: String? = null,
        after: String? = null,
        source: SourceInfo = createSourceInfo(),
        op: DebeziumOperation,
        tsMs: Long = TestTimeProvider.testEpochSecond,
        transaction: TransactionInfo? = null,
    ) = ChangeMessage(
        before = before,
        after = after,
        source = source,
        op = op,
        tsMs = tsMs,
        transaction = transaction,
    )
}