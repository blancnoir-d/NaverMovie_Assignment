package me.saeha.android.navermovie_project.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import me.saeha.android.navermovie_project.R
import me.saeha.android.navermovie_project.databinding.ActivityMovieDetailInfoBinding
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents


class MovieDetailInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailInfoBinding
    private lateinit var movie: MovieData
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        movie = intent.getSerializableExtra("movieObject") as MovieData // 직렬화된 객체를 받음

        setView()

    }

    private fun setView() {
        //toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.

        binding.tvDetailToolbarTitle.text = movie.title
        binding.tvDetailTitle.text = movie.title
        binding.tvDetailDirector.text = getString(R.string.director,movie.director)
        binding.tvDetailActor.text = getString(R.string.actor,movie.actor)
        binding.tvDetailUserRating.text = getString(R.string.user_rating,movie.userRating)

        if(movie.favorite){
            binding.ivbDetailFavorite.setBackgroundResource(R.drawable.ic_star_yellow_24)
        }else{
            binding.ivbDetailFavorite.setBackgroundResource(R.drawable.ic_star_gray_24)
        }

        //즐겨찾기
        binding.ivbDetailFavorite.setOnClickListener{
            if(movie.favorite){
                binding.ivbDetailFavorite.setBackgroundResource(R.drawable.ic_star_gray_24)
                movie.favorite = false
                mainViewModel.deleteFavorite(movie)
            }else{
                binding.ivbDetailFavorite.setBackgroundResource(R.drawable.ic_star_yellow_24)
                movie.favorite = true
                mainViewModel.addFavorite(movie)
            }

        }

        Glide.with(this)
            .load(movie.image)
            .override(190, 250)
            .centerCrop()
            .error(R.drawable.ic_no_pictures)
            .into(binding.ivDetailPoster)

        //웹뷰
        binding.wvDetailWeb.webViewClient = MovieWebView()
        val webSet = binding.wvDetailWeb.settings
        webSet.builtInZoomControls = true //확대 축소
        webSet.javaScriptEnabled = true
        binding.wvDetailWeb.loadUrl(movie.link)
    }



    //Toolbar 메뉴 클릭 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { //뒤로 가기 버튼
                finish()

            }
//            R.id.toolbar_info -> {// 툴팁
//                val view = findViewById<View>(R.id.toolbar_info) //툴팁을 띄우기 위해서는 view가 필요함
//                balloon.showAlignBottom(view)
//            }
        }
        return super.onOptionsItemSelected(item)
    }


    //WebViewClient
    internal class MovieWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.d("확인", request?.url.toString())
            return super.shouldOverrideUrlLoading(view, request)
        }

    }
}