package com.datangic.smartlock.liveData

import java.util.*

class ReceivedQueue(override val size: Int = Int.MAX_VALUE) : AbstractQueue<LockBleReceivedLiveData>() {
    override fun iterator(): MutableIterator<LockBleReceivedLiveData> {
        TODO("Not yet implemented")
    }

    override fun offer(e: LockBleReceivedLiveData?): Boolean {
        TODO("Not yet implemented")
    }

    override fun poll(): LockBleReceivedLiveData? {
        TODO("Not yet implemented")
    }

    override fun peek(): LockBleReceivedLiveData? {
        TODO("Not yet implemented")
    }
}