package com.jonbott.learningrxjava.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jonbott.learningrxjava.Common.disposedBy
import com.jonbott.learningrxjava.ModelLayer.Entities.Posting
import com.jonbott.learningrxjava.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_basic_example.*
import kotlinx.android.synthetic.main.item_message.userIdTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException


class BasicExampleActivity : AppCompatActivity() {

    private var bag = CompositeDisposable()

    //region Simple network layer

    interface JsonPlaceholderService {
        @GET("posts/{id}")
        fun getPostById(@Path("id")id: String): Call<Posting>
    }

    private var retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://jsonplaceholder.typicode.com/")
            .build()

    private var retrofitService = retrofit.create(JsonPlaceholderService::class.java)
    //endregion


    //region Life Cycle Events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_example)

        realSingleExample()
    }

    //endregion

    //region Rx Code
    private fun realSingleExample() {
        loadPostAsSingle().observeOn(AndroidSchedulers.mainThread())//results on the main thread
                .subscribeOn(Schedulers.io())
                .subscribe({posting ->
                    userIdTextView.text = posting.title
                    body_TextView.text = posting.body
                }, { error ->
                    Toast.makeText(this, "An error occured: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                    userIdTextView.text = ""
                    body_TextView.text = ""
                }).disposedBy(bag)
    }

    private fun loadPostAsSingle() : Single<Posting> {
        return Single.create {observer ->
            //simulate network delay
            Thread.sleep(2000)
            val postingId = 5
            retrofitService.getPostById(postingId.toString()).enqueue(object: Callback<Posting>{
                override fun onResponse(call: Call<Posting>, response: Response<Posting>) {
                    val posting = response?.body()
                    if (posting != null) {
                        observer.onSuccess(posting)
                    } else {
                        val e = IOException("An unknown network error occurred")
                        observer.onError(e)
                    }
                }
                override fun onFailure(call: Call<Posting>, t: Throwable) {
                    val e = t ?: IOException("An unknown network error occurred")
                    observer.onError(e)
                }
            })
        }
    }
    //endregion

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }
}