package com.example.myboards.support

fun <T> Result<T>.toDelayed() = DelayedResult.of(this)