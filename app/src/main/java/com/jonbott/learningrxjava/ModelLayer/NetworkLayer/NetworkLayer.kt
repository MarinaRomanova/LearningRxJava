package com.jonbott.learningrxjava.ModelLayer.NetworkLayer

import com.github.kittinunf.result.Result
import com.jonbott.datalayerexample.DataLayer.NetworkLayer.EndpointInterfaces.JsonPlaceHolder
import com.jonbott.datalayerexample.DataLayer.NetworkLayer.Helpers.ServiceGenerator
import com.jonbott.learningrxjava.Common.EmptyDescriptionException
import com.jonbott.learningrxjava.Common.NullBox
import com.jonbott.learningrxjava.Common.StringLambda
import com.jonbott.learningrxjava.Common.VoidLambda
import com.jonbott.learningrxjava.ModelLayer.Entities.Message
import com.jonbott.learningrxjava.ModelLayer.Entities.Person
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.zip
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception


typealias MessageLambda = (Message?)->Unit
typealias MessagesLambda = (List<Message>?)->Unit

class NetworkLayer {
    companion object { val instance = NetworkLayer() }

    private val placeHolderApi: JsonPlaceHolder

    init {
        placeHolderApi = ServiceGenerator.createService(JsonPlaceHolder::class.java)
    }

    //region Fully Rx

    fun getMessageRx(articleId: String): Single<Message> {
        return placeHolderApi.getMessageRx(articleId)
    }

    fun getMessagesRx(): Single<List<Message>> {
        return placeHolderApi.getMessagesRx()
    }

    fun postMessageRx(message: Message): Single<Message> {
        return placeHolderApi.postMessageRx(message)
    }

    //endregion

    //region End Point - SemiRx Way

    fun getMessages(success: MessagesLambda, failure: StringLambda) {
        val call = placeHolderApi.getMessages()

        call.enqueue(object: Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>?, response: Response<List<Message>>?) {
                val article = parseRespone(response)
                success(article)
            }

            override fun onFailure(call: Call<List<Message>>?, t: Throwable?) {
                println("Failed to GET Message: ${ t?.message }")
                failure(t?.localizedMessage ?: "Unknown error occured")
            }
        })
    }

    fun getMessage(articleId: String, success: MessageLambda, failure: VoidLambda) {
        val call = placeHolderApi.getMessage(articleId)

        call.enqueue(object: Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                val article = parseRespone(response)
                success(article)
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                println("Failed to GET Message: ${ t?.message }")
                failure()
            }
        })
    }

    fun postMessage(message: Message, success: MessageLambda, failure: VoidLambda) {
        val call = placeHolderApi.postMessage(message)

        call.enqueue(object: Callback<Message>{
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                val article = parseRespone(response)
                success(article)
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                println("Failed to POST Message: ${ t?.message }")
                failure()
            }
        })
    }

    private fun <T> parseRespone(response: Response<T>?): T? {
        val article = response?.body() ?: null

        if (article == null) {
            parseResponeError(response)
        }

        return article
    }

    private fun <T> parseResponeError(response: Response<T>?) {
        if(response == null) return

        val responseBody = response.errorBody()

        if(responseBody != null) {
            try {
                val text = "responseBody = ${ responseBody.string() }"
                println("$text")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            val text = "responseBody == null"
            println("$text")
        }
    }

    //endregion

    //region Task Example

    //Make an Observable for each person in a list
    fun loadInfoFor(people: List<Person>) : Observable<List<String>>{
        // Make a network call for each person
        val networkObservables = people.map(::buildGetInfoNetworkCallFor)

        //when all server results return, zip thos observables into a single Observable
        return  networkObservables.zip{ elements ->
            elements.filter { box -> box.value !=null }
                    .map { it.value!! }

        }
    }

    //Wrap Task in Reactive Observable
    //this pattern is often used for units of work
    private fun buildGetInfoNetworkCallFor(person: Person) : Observable<NullBox<String>> {
        return Observable.create<NullBox<String>>{ observer ->
            //execute request - do actual work
            getInfoFor(person){ result ->
                result.fold( {info ->
                    observer.onNext(info)
                    observer.onComplete()
                }, {error->
                    //do smth with the err or just pass it on
                    observer.onError(error)
                })
            }
        }.onErrorReturn { NullBox(null) }
    }

    //Create a Network Task
    fun getInfoFor(person: Person, finished: (Result<NullBox<String>, Exception>)-> Unit){
        //Execute on Background thread
        //Imagine doing a real Task
        launch {
            println("start network call for: $person")
            val randomTime = person.age* 1000//in milliseconds
            delay(randomTime)
            println("the end of network call for: $person")

            //just randomly make odd people null
    //        var result = Result.of(NullBox(person.toString()))
            val isEven = person.age % 2 == 0
            var result = if (isEven) Result.of(NullBox(person.firstName))
            else Result.of(NullBox<String>(null))

            //Adding Exceptions
            if(person.age > 3) {
                Result.of { throw EmptyDescriptionException("Person's exception")}
            }
            finished(result)
        }

    }
    //endregion

}