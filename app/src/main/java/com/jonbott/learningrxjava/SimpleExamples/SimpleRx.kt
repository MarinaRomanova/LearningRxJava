package com.jonbott.learningrxjava.SimpleExamples

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.Common.disposedBy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

object SimpleRx {

    var bag = CompositeDisposable()

    fun simpleValues(){
        println("~~~~simpleValues~~~~")
        val someInfo = BehaviorRelay.createDefault("1")
        println("someinfo.value ${someInfo.value}")

        val plainString = someInfo.value
        println("plainString: ${plainString}")

        someInfo.accept("2")
        println("someinfo.value ${someInfo.value}")

        someInfo.subscribe() { newValue ->
            println("value has changed: ${newValue}")
            // relays never receives onError and onComplete events
        }
        someInfo.accept("3")
    }

    fun subjects(){
        val behaviorSubject = BehaviorSubject.createDefault(24)

        val disposable = behaviorSubject.subscribe({ newValue -> //onNext
            println("behaviourSubject.value ${newValue}")
        }, { error-> //onError
            println("behaviourSubject error ${error.localizedMessage}")
        }, {//onCompleted
            println("behaviourSubject completed")
        }, { disposable -> //onSubscribe
            println("behaviourSubject subscribed")
        })

        behaviorSubject.onNext(32)
        behaviorSubject.onNext(45)
        behaviorSubject.onNext(45) //duplicates show as new event

        //1 onError
        val someException =  IllegalArgumentException("Ooops! Some fake error")
        behaviorSubject.onError(someException)
        behaviorSubject.onNext(100)//this will never show

        //2 onComplete (not triggered if onError occurs)
        behaviorSubject.onComplete()
        behaviorSubject.onNext(10000)//will never show if onComplete is triggered
    }

    fun basicObservable(){
        //The Observable

        val observable = Observable.create<String>{observer->
            //the lambda is called for every subscriber - by default
            //do work on a background thread
            launch {
                delay(1000)//artificial delay
                observer.onNext("some value 23")
                observer.onComplete()
            }
        }

        observable.subscribe{ someString->
            println("observable: ${someString}")
        }.disposedBy(bag)

        val observer = observable.subscribe{ someString ->
            println("another observer: ${someString}")
        }

        observer.disposedBy(bag)
    }

    fun creatingObservables(){
        val observable = Observable.just(1)
        val observable2 = Observable.interval(300, TimeUnit.MILLISECONDS).timeInterval(AndroidSchedulers.mainThread()) //runs on a main thread
        val observable3 = Observable.fromArray(1,2,3,4)
        val userIds = arrayOf(1,2,3,4,5)
        val observable4 = Observable.fromArray(*userIds)
        val observable5 =userIds.toObservable()
    }
}