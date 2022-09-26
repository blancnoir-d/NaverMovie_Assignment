package me.saeha.android.navermovie_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import me.saeha.android.navermovie_project.databinding.ActivityMainBinding
import me.saeha.android.navermovie_project.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    var text = "TEST"
    private val clientId = ""
    private val clientPass = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.activity = this

        setView()

    }

    private fun setView(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼 활성화 (화살표)
        supportActionBar?.setDisplayShowTitleEnabled(false) //액션바에 표시되는 제목의 표시유무를 설정합니다. false로 해야 custom한 툴바의 이름이 화면에 보이게 됩니다.
        binding.toolbar.title = "네이버 영화 검색"
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
               val intent = Intent(this,FavoritesActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchMovie(searchText: String){
        val call: Call<String> = RetrofitClient.service.requestMovie(clientId,clientPass,"movie.json",searchText)
        call.enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.i("Test",response.body().toString())
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })
    }
}