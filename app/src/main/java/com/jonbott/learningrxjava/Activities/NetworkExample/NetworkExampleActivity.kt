package com.jonbott.learningrxjava.Activities.NetworkExample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.jonbott.learningrxjava.Activities.NetworkExample.Recycler.MessageViewAdapter
import com.jonbott.learningrxjava.Common.disposedBy
import com.jonbott.learningrxjava.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_network_example.*
import kotlinx.coroutines.experimental.launch

class NetworkExampleActivity : AppCompatActivity() {

    private val presenter = NetworkExamplePresenter()
    private var bag = CompositeDisposable()

    lateinit var adapter: MessageViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_example)

        attachUI()
    }

    private fun attachUI() {
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)

        networkRecyclerView.layoutManager = linearLayoutManager
        networkRecyclerView.setHasFixedSize(true)
        networkRecyclerView.addItemDecoration(dividerItemDecoration)

        //Semi Rx way of doing things
        /*
        presenter.messages.observeOn(AndroidSchedulers.mainThread())
                .subscribe{ messages ->
                    adapter.messages.accept(messages)
                }.disposedBy(bag)

        */

        initializeListView()

        //Fully Rx way of doing things
        launch {
            loadData()
        }

    }

    private fun loadData() {
        presenter.getMessagesRX().observeOn(AndroidSchedulers.mainThread())
                .subscribe{ messages ->
                    adapter.messages.accept(messages)
                }.disposedBy(bag)
    }

    private fun initializeListView() {
        adapter = MessageViewAdapter { view, position -> rowTapped(position) }
        networkRecyclerView.adapter = adapter
    }

    private fun rowTapped(position: Int) {
        println("🍄")
        println(adapter.messages.value[position])
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }
}

