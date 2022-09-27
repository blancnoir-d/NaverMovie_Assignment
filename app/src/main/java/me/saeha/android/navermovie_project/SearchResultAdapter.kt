package me.saeha.android.navermovie_project

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import me.saeha.android.navermovie_project.databinding.ItemMovieBinding
import me.saeha.android.navermovie_project.model.MoviesData

class SearchResultAdapter(
    val context: Context,
    private val templateKeywordData: List<MoviesData.Movie>
) :
    RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //viewbinding - item 레이아웃과
        val templateDetailBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(templateDetailBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = templateKeywordData[position]
        holder.onBind(context, item)

        //아이템 클릭리스너
        holder.itemView.setOnClickListener {

            val intent = Intent(context, MovieDetailInfoActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = templateKeywordData.size

    //아이템 뷰홀더
    class ItemViewHolder(binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(context: Context, item: MoviesData.Movie) {


        }
    }
}
