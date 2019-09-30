package com.jonbott.learningrxjava.Activities.TasksExample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.jonbott.learningrxjava.Common.disposedBy
import com.jonbott.learningrxjava.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class TasksExampleActivity : AppCompatActivity() {

    private val presenter = TasksExamplePresenter()

    private var bag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_example)
        presenter.loadInfoFor().observeOn(AndroidSchedulers.mainThread())
                .subscribe({infoList->
                    (infoList.forEach{ println(it) })
                }, { error ->
                    Log.e(TasksExampleActivity::class.java.simpleName, error.localizedMessage)
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                }).disposedBy(bag)
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }
}

