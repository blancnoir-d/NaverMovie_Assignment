package me.saeha.android.navermovie_project

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import me.saeha.android.navermovie_project.databinding.ItemMovieBinding
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.model.MoviesData

class SearchResultAdapter(
    val context: Context,
    private val searchResultList: List<MovieData>
) :
    RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //viewbinding - item 레이아웃과
        val templateDetailBinding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(templateDetailBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = searchResultList[position]
        holder.onBind(context, item)

        //아이템 클릭리스너
        holder.itemView.setOnClickListener {

            val intent = Intent(context, MovieDetailInfoActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = searchResultList.size

    //아이템 뷰홀더
    class ItemViewHolder(binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private val ivbFavorite = binding.ivbFavorite
        private val tvTitle = binding.tvTitle
        private val tvDirector = binding.tvDirector
        private val tvActor = binding.tvActor
        private val tvUserRating = binding.tvUserRating

        fun onBind(context: Context, item: MovieData) {

            tvTitle.text = item.title
            tvDirector.text = context.getString(R.string.director,item.director)
            tvActor.text = context.getString(R.string.actor,item.actor)
            tvUserRating.text = context.getString(R.string.user_rating,item.userRating)

            ivbFavorite.setOnClickListener {

            }
        }
    }
}
