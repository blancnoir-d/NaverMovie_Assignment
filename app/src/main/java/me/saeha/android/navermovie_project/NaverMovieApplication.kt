package me.saeha.android.navermovie_project

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class NaverMovieApplication : Application(){
    override fun onCreate(){
        super.onCreate()
        //Realm 초기화
        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .name("appdb.realm") // 생성할 realm 파일 이름 지정
            .allowWritesOnUiThread(true) // UI Thread에서도 realm에 접근할 수 있도록 한다.
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)

    }
}