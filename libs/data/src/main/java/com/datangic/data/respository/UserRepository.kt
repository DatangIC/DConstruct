package com.datangic.data.respository

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserRepository {
        @Subscribe(threadMode = ThreadMode.BACKGROUND)
        public fun eventbusMessage( ){

        }

}