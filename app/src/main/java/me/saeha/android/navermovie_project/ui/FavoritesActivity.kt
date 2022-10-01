package me.saeha.android.navermovie_project.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.saeha.android.navermovie_project.R
import me.saeha.android.navermovie_project.databinding.ActivityFavoritesBinding
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()
    var itemCode = 0
    var list = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()

        //즐겨찾기 List에서 별 표시를 눌렀을 때 호출
        compositeDisposable.add(
            RxBus.listen(RxEvents.EventFavoriteOfFavoriteList::class.java).subscribe {
                if(it.movie.favorite){ //즐겨찾기 상태는 true
                    mainViewModel.deleteFavorite(it.movie)
                    list.add(it.movie.code)
                }
            }
        )
    }

    private fun setView(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        //Divider-> 계속 실행되면 item 높이가 늘어남
        val dividerItemDecoration = DividerItemDecoration(this,layoutManager.orientation)
        binding.rcyFavoriteMovieList.addItemDecoration(dividerItemDecoration)

        mainViewModel.liveFavoriteList.observe(this){
            val adapter = FavoritesAdapter(this, it)
            binding.rcyFavoriteMovieList.adapter= adapter
            binding.rcyFavoriteMovieList.layoutManager= layoutManager


        }
    }

    override fun onBackPressed() {
        val mIntent = Intent(this, MainActivity::class.java)
        setResult(RESULT_OK, mIntent)
        finish()
    }
}