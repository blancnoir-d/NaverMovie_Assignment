package me.saeha.android.navermovie_project.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.saeha.android.navermovie_project.R
import me.saeha.android.navermovie_project.databinding.ActivityMainBinding
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var text = "TEST"

    private lateinit var mainViewModel: MainViewModel
    private val compositeDisposable = CompositeDisposable()
    private lateinit var adapter: SearchResultAdapter
    private var recyclerViewState: Parcelable? = null

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

        //즐겨찾기 화면에서 다시 돌아왔을 때
        getResultText = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                mainViewModel.updateResultListFavorite()
                binding.rcyMainMovieList.layoutManager = LinearLayoutManager(this)

                //즐겨 찾기 해체한 값 적용. recyclerview 다시 그리기
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

        //RecyclerView 바닥 인지
        binding.rcyMainMovieList.addOnScrollListener(
            object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (!binding.rcyMainMovieList.canScrollVertically(1)) {//최하단 인지하면
                        val resultSize =  mainViewModel.liveSearchResult.value?.size //현재 보이는 영화 리스트 사이즈
                        val original = mainViewModel.movieSearchResult.size //전체 영화 리스트 사이트
                        if (resultSize != null) {
                            recyclerViewState =
                                binding.rcyMainMovieList.layoutManager?.onSaveInstanceState()!! //RecyclerView 현 상태 저장
                            if(original != resultSize){
                                binding.pbMainLoadItem.visibility = View.VISIBLE
                            }else{
                                binding.pbMainLoadItem.visibility = View.INVISIBLE
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                               // 현재 보이는 결과 사이즈가 원본보다 작을 때 영화 5개씩 불러오기
                                mainViewModel.getNextPage(resultSize)
                                binding.pbMainLoadItem.visibility = View.INVISIBLE
                            }, 1000)

                        }
                    }
                }
            }
        )


        mainViewModel.liveSearchResult.observe(this) {
            //TODO:it은 즐겨찾기 데이터가 처리된 데이터로 나와야
            adapter = SearchResultAdapter(this, it)

            binding.rcyMainMovieList.adapter = adapter
            binding.rcyMainMovieList.layoutManager = layoutManager

            // RecyclerView의 데이터 값이 바뀌어도 item 위치 그대로 하기 위해 추가
            if(recyclerViewState != null)
            binding.rcyMainMovieList.layoutManager!!.onRestoreInstanceState(recyclerViewState)//저장한 RecyclerView 상태 set

            Log.d("사이즈 확인1", adapter.searchResultList.size.toString()) //10개
            Log.d("사이즈 확인2", mainViewModel.liveSearchResult.value?.size.toString()) //10개

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
}