package me.saeha.android.navermovie_project.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.saeha.android.navermovie_project.R
import me.saeha.android.navermovie_project.databinding.ActivityMainBinding
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var text = "TEST"

    private lateinit var mainViewModel: MainViewModel
    private val compositeDisposable = CompositeDisposable()
    private lateinit var adapter: SearchResultAdapter

    //onActivity
    private lateinit var getResultText: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.activity = this
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setView()

        //main List에서 별 표시를 눌렀을 때 호출 됨
        compositeDisposable.add(
            RxBus.listen(RxEvents.EventFavoriteOfMainList::class.java).subscribe {
                Log.d("메인 즐찾 rx 1", it.movie.favorite.toString())
                if (it.movie.favorite) {//true
                    mainViewModel.addFavorite(it.movie)
                } else {//false
                    mainViewModel.deleteFavorite(it.movie)
                }
            }
        )

        getResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                //val deleteFavoriteList: = it.data?.getSerializableExtra("delete") as ArrayList<MovieData>
                val list: ArrayList<MovieData>? =
                    intent.getSerializableExtra("delete") as ArrayList<MovieData>?
                mainViewModel.updateResultListFavorite()
                binding.rcyMainMovieList.layoutManager = LinearLayoutManager(this)

                mainViewModel.liveSearchResult.observe(this) { movieList ->

                    adapter = SearchResultAdapter(this, movieList)
                    binding.rcyMainMovieList.adapter = adapter

                }
            }
        }
    }

    private fun setView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.tvMainToolbarTitle.text = "네이버 영화 검색"

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //Divider-> 계속 실행되면 item 높이가 늘어남
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rcyMainMovieList.addItemDecoration(dividerItemDecoration)

        mainViewModel.liveSearchResult.observe(this) {
            //TODO:it은 즐겨찾기 데이터가 처리된 데이터로 나와야
            adapter = SearchResultAdapter(this, it)
            binding.rcyMainMovieList.adapter = adapter
            binding.rcyMainMovieList.layoutManager = layoutManager
            Log.d("사이즈 확인2", adapter.searchResultList.size.toString()) //10개
            Log.d("사이즈 확인23", mainViewModel.liveSearchResult.value?.size.toString()) //10개
            adapter.searchResultList = mainViewModel.liveSearchResult.value!!


        }

        //testWatcher를 생성해 입력이 끝날 때 검색이 되도록 함
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                mainViewModel.getSearchData(binding.etMainSearch.text.toString())
            }
        }
        binding.etMainSearch.addTextChangedListener(textWatcher)


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
            R.id.menu_favorites -> {//즐겨찾기
                val intent = Intent(this, FavoritesActivity::class.java)
                getResultText.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }
}