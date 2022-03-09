package com.datangic.zxing

import androidx.lifecycle.LiveData
import com.google.zxing.Result

class ResultLiveData : LiveData<ResultLiveData>() {
    var mResult: Result? = null
        set(value) {
            field = value
            this.postValue(this)
        }

}