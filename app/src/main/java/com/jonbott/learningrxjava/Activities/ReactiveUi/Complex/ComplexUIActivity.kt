package com.jonbott.learningrxjava.Activities.ReactiveUi.Complex

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jonbott.learningrxjava.Common.disposedBy
import com.jonbott.learningrxjava.R
import com.jonbott.learningrxjava.databinding.ActivityComplexUiBinding
import com.jonbott.learningrxjava.databinding.ItemReactiveUiBinding
import com.minimize.android.rxrecycleradapter.RxDataSource
import io.reactivex.disposables.CompositeDisposable


//https://github.com/ahmedrizwan/RxRecyclerAdapter

private enum class CellType {
    ITEM,
    ITEM2
}

class ReactiveUIActivity : AppCompatActivity() {

    private var bag = CompositeDisposable()
    private val dataSet = (0..100).toList().map { it.toString() }
    lateinit var boundActivity: ActivityComplexUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complex_ui)

        commonInit()
        showSimpleBIndingExample()
    }

    private fun commonInit() {
        boundActivity = DataBindingUtil.setContentView(this, R.layout.activity_complex_ui)
        boundActivity.reactiveUIRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showSimpleBIndingExample() {
        val rxDataSource = RxDataSource<ItemReactiveUiBinding, String>(R.layout.item_reactive_ui, dataSet)
        rxDataSource.bindRecyclerView(boundActivity.reactiveUIRecyclerView)
        rxDataSource.map { it.toUpperCase()}
                .asObservable()
                .subscribe{
                    val ui = it.viewDataBinding ?: return@subscribe
                    val data = it.item

                    ui.textViewItem.text = data
                }.disposedBy(bag)
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }
}