package com.jonbott.learningrxjava.Activities.ReactiveUi.Simple

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.ModelLayer.Entities.Friend
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.lang.Exception

class SimpleUIPresenter {

    val friends = BehaviorRelay.createDefault(listOf<Friend>()) //empty list
    val title = BehaviorSubject.createDefault("Default Title")

    init {
        loadFriends()
    }

    private fun loadFriends() {
        title.onNext("Loading Friends")
        launch {
            delay(3000)
            title.onNext("Friends loaded")
            var newFriends = listOf(Friend("Debi", "Darlington"),
                    Friend("Arlie", "Abalos"),
                    Friend("Jessica", "Jetton"),
                    Friend("Tonia", "Threlkeld"),
                    Friend("Donte", "Derosa"),
                    Friend("Nohemi", "Notter"),
                    Friend("Rod", "Rye"),
                    Friend("Simonne", "Sala"),
                    Friend("Kathaleen", "Kyles"),
                    Friend("Loan", "Lawrie"),
                    Friend("Elden", "Ellen"),
                    Friend("Felecia", "Fortin"),
                    Friend("Fiona", "Fiorini"),
                    Friend("Joette", "July"),
                    Friend("Beverley", "Bob"),
                    Friend("Artie", "Aquino"),
                    Friend("Yan", "Ybarbo"),
                    Friend("Armando", "Araiza"),
                    Friend("Dolly", "Delapaz"),
                    Friend("Juliane", "Jobin"))
            launch(UI) {
                friends.accept(newFriends)
            }
        }

        launch {
            delay(6000)
            title.onError(Exception("Faked Error"))
        }

        launch {
            delay(7000)
            println(SimpleUIPresenter::class.java.simpleName + " Pushing new Value")
            title.onNext("New value")
        }
    }
}