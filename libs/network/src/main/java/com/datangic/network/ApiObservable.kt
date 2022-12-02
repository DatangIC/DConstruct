package com.datangic.network

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.HttpException

abstract class ApiObservable<IN : Any, OUT>(
    private val upstream: Observable<IN>,
) : Observable<ResponseStatus<OUT>>() {
    override fun subscribeActual(
        observer: Observer<in ResponseStatus<OUT>>
    ) {
        upstream.subscribe(ApiObserver(observer))
    }

    inner class ApiObserver(
        val observer: Observer<in ResponseStatus<OUT>>
    ) : Observer<IN> {

        private var terminated = false
        override fun onSubscribe(d: Disposable) {
            observer.onSubscribe(d)
            observer.onNext(ResponseStatus.loading())
        }

        override fun onNext(t: IN) {
            observer.onNext(doNext(t))
        }

        override fun onError(e: Throwable) {
            if (e is HttpException) {
                observer.onNext(doError(e))
            } else {
                terminated = true
                observer.onError(e)
            }
        }

        override fun onComplete() {
            if (!terminated) {
                observer.onComplete()
            }
        }
    }
    abstract fun doNext(i: IN): ResponseStatus<OUT>
    abstract fun doError(e: HttpException): ResponseStatus<OUT>
}