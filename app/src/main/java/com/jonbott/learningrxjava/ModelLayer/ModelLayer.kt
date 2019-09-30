package com.jonbott.learningrxjava.ModelLayer

import android.util.Log
import android.widget.Toast
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.LearningRxJavaApplication
import com.jonbott.learningrxjava.ModelLayer.Entities.Message
import com.jonbott.learningrxjava.ModelLayer.Entities.Person
import com.jonbott.learningrxjava.ModelLayer.NetworkLayer.NetworkLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PersistenceLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PhotoDescription
import io.reactivex.Observable
import io.reactivex.Single

class ModelLayer {

    companion object {
        val shared = ModelLayer()
    }

    val photoDescriptions = BehaviorRelay.createDefault(listOf<PhotoDescription>()) //empty list
    val messages = BehaviorRelay.createDefault(listOf<Message>()) //empty list

    private val networkLayer = NetworkLayer.instance
    private val persistenceLayer = PersistenceLayer.shared

    fun loadAllPhotoDescriptions(){ //using async
        persistenceLayer.loadAllPhotoDescriptions { photoDescriptions ->
            this.photoDescriptions.accept(photoDescriptions)
        }
    }

    fun getMessages(){
        return networkLayer.getMessages({ messages ->
            this.messages.accept(messages)
        }, { error ->
            notifyOfError(error)
        })
    }

    fun getMessagesRx(): Single<List<Message>> {
        return networkLayer.getMessagesRx()
    }

    private fun notifyOfError( error: String) {
        //notify user of error
        Toast.makeText(LearningRxJavaApplication.context, "Oops: $error", Toast.LENGTH_LONG).show()
        Log.e(ModelLayer.javaClass.simpleName, error)
    }

    fun loadInfoFor(people: List<Person>): Observable<List<String>> {
        return networkLayer.loadInfoFor(people)
    }
}