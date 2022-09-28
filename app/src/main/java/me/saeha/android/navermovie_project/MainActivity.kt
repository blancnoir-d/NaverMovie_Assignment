package me.saeha.android.navermovie_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import me.saeha.android.navermovie_project.databinding.ActivityMainBinding
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.model.MoviesData
import me.saeha.android.navermovie_project.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    var text = "TEST"

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.activity= this
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setView()

    }

    private fun setView(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.tvMainToolbarTitle.text= "네이버 영화 검색"

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        //Divider-> 계속 실행되면 item 높이가 늘어남
        val dividerItemDecoration = DividerItemDecoration(this,layoutManager.orientation)
        binding.rcyMainMovieList.addItemDecoration(dividerItemDecoration)

        mainViewModel.liveSearchResult.observe(this){
            //TODO:it은 즐겨찾기 데이터가 처리된 데이터로 나와야
            val adapter = SearchResultAdapter(this,it)
            binding.rcyMainMovieList.adapter= adapter
            binding.rcyMainMovieList.layoutManager= layoutManager


        }

    }

    // 툴바 메뉴 버튼을 설정- menu에 있는 item을 연결하는 부분
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.toolbar_menu,
            menu
        )       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    //Toolbar 메뉴 클릭 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorites-> {//즐겨찾기
                mainViewModel.getSearchData("1991")
//               val intent = Intent(this,FavoritesActivity::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun searchMovie(searchText: String){
//        //
//        val call: Call<MoviesData> = RetrofitClient.service.requestMovieJson(clientId,clientPass,"movie.json",searchText)
//        call.enqueue(object: Callback<MoviesData>{
//            override fun onResponse(call: Call<MoviesData>, response: Response<MoviesData>) {
//                if(response.isSuccessful){ //response.code ==200
//                    val data: MoviesData? = response.body()
//                    if(data != null){
//                        Log.d("영화 데이터", data.items.size.toString())
//                    }
//
//                }else{
//                    Log.e("Network",response.code().toString())
//                }//response.code == 400
//            }
//
//            override fun onFailure(call: Call<MoviesData>, t: Throwable) { //response.code == 500
//
//            }
//        })
//    }
//
//
//    private fun searchMovieRxJava(searchText: String){
//        //
//        val disposable = RetrofitClient.service.requestMovieRxJava(clientId,clientPass,"movie.json",searchText)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe { movies ->
//                movies.items.iterator().forEach {
//                    val movieItem = MovieData(it.title, it.link, it.image, it.subtitle, it.pubDate, it.director,it.actor, it.userRating, )
//                    resultList.add(movieItem)
//
//                    val adapter = SearchResultAdapter(this, resultList)
//                }
//
//            }
//
//    }
//
//    override fun onDestroy() { //RxJava
//        super.onDestroy()
//        disposable?.let{ disposable!!.dispose() }
//
//    }


}