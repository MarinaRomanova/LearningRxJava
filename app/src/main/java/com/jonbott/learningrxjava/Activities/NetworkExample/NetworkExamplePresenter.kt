package com.jonbott.learningrxjava.Activities.NetworkExample

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.ModelLayer.Entities.Message
import com.jonbott.learningrxjava.ModelLayer.ModelLayer
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class NetworkExamplePresenter {

    private val modelLayer = ModelLayer.shared //normally injected

    //Semi Rx way of doing things
    val messages : BehaviorRelay<List<Message>>
        get() = modelLayer.messages
    private var bag = CompositeDisposable()

    init {
        modelLayer.getMessages()
    }

    //Fully RX way of doing things
    fun getMessagesRX(): Single<List<Message>> {
        return modelLayer.getMessagesRx()
    }
}