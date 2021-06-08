package com.example.myboards.support

import androidx.lifecycle.MutableLiveData

class NeverNullMutableLiveData<T>(value: T) : MutableLiveData<T>(value) {
    var value: T
        @JvmName("getNotNullValue")
        get() = super.getValue()!!
        @JvmName("setNotNullValue")
        set(value) {
            super.setValue(value)
        }
}
