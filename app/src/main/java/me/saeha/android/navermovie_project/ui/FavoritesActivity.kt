package me.saeha.android.navermovie_project.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.saeha.android.navermovie_project.R
import me.saeha.android.navermovie_project.databinding.ActivityFavoritesBinding
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()
    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()

        //즐겨찾기 List에서 별 눌렀을 때 호출.
        compositeDisposable.add(
            RxBus.listen(RxEvents.EventFavoriteOfFavoriteList::class.java).subscribe {
                recyclerViewState =binding.rcyFavoriteMovieList.layoutManager?.onSaveInstanceState()!! //RecyclerView 현 스크롤 상태 저장
                if(it.movie.favorite){ //즐겨찾기 상태는 true
                    mainViewModel.deleteFavorite(it.movie)
                }
            }
        )
    }

    private fun setView(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_close_gray_24) //뒤로가기 버튼 아이콘 변경

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        //Divider-> 계속 실행되면 item 높이가 늘어남
        val dividerItemDecoration = DividerItemDecoration(this,layoutManager.orientation)
        binding.rcyFavoriteMovieList.addItemDecoration(dividerItemDecoration)

        mainViewModel.liveFavoriteList.observe(this){
            val adapter = FavoritesAdapter(this, it)
            binding.rcyFavoriteMovieList.adapter= adapter
            binding.rcyFavoriteMovieList.layoutManager= layoutManager
            // RecyclerView의 데이터 값이 바뀌어도 item 위치 그대로 하기 위해 추가
            if(recyclerViewState != null)
                binding.rcyFavoriteMovieList.layoutManager!!.onRestoreInstanceState(recyclerViewState)//저장한 RecyclerView 스크롤 상태 set


        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { //뒤로 가기 버튼
                val mIntent = Intent(this, MainActivity::class.java)
                setResult(RESULT_OK, mIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val mIntent = Intent(this, MainActivity::class.java)
        setResult(RESULT_OK, mIntent)
        finish()
    }
}