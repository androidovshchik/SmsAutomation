package com.buggzy.smsrestroom

import android.content.Intent
import com.buggzy.smsrestroom.base.BaseService
import io.reactivex.schedulers.Schedulers

class MainService : BaseService() {

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
        startForeground(1, "Фоновая переписка VK", R.drawable.ic_vk)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (!hasConditions()) {
            if (isChiefBot()) {
                showToast("Требуются данные для работы с VK")
            }
            stopWork()
            return START_NOT_STICKY
        }
        disposable.add(RxFirestore.observeDocumentRef(FirebaseFirestore.getInstance()
            .collection("info")
            .document("settings"))
            .subscribe({

            }.subscribeOn(Schedulers.io())
                .subscribe())
            return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}