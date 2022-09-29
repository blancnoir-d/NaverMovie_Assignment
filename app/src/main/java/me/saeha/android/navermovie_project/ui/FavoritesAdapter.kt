package me.saeha.android.navermovie_project.ui

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import com.bumptech.glide.Glide
import me.saeha.android.navermovie_project.R
import me.saeha.android.navermovie_project.databinding.ItemMovieBinding
import me.saeha.android.navermovie_project.model.Movie
import me.saeha.android.navermovie_project.model.MovieData
import me.saeha.android.navermovie_project.network.RxBus
import me.saeha.android.navermovie_project.network.RxEvents


class FavoritesAdapter(val context: Context, private val templateKeywordData: MutableList<Movie>) :
    RecyclerView.Adapter<FavoritesAdapter.ItemViewHolder>() {

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
            val movieData = MovieData(
                item.title.toString(),
                item.link.toString(), item.image.toString(), item.subtitle.toString(),
                item.pubDate.toString(), item.director.toString(), item.actor.toString(),
                item.userRating.toString(), item.favorite, item.code
            )
            intent.putExtra("movieObject", movieData)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = templateKeywordData.size

    //아이템 뷰홀더
    class ItemViewHolder(binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        private val ivbFavorite = binding.ivbFavorite
        private val ivPoster = binding.ivPoster
        private val tvTitle = binding.tvTitle
        private val tvDirector = binding.tvDirector
        private val tvActor = binding.tvActor
        private val tvUserRating = binding.tvUserRating

        fun onBind(context: Context, item: Movie) {
            Glide.with(context)
                .load(item.image)
                .override(190, 250)
                .centerCrop()
                .error(R.drawable.ic_no_pictures)
                .into(ivPoster)

            tvTitle.text = item.title
            tvDirector.text = context.getString(R.string.director, item.director)
            tvActor.text = context.getString(R.string.actor, item.actor)
            tvUserRating.text = context.getString(R.string.user_rating, item.userRating)
            if (item.favorite) {
                ivbFavorite.setBackgroundResource(R.drawable.ic_star_yellow_24)
            } else {
                ivbFavorite.setBackgroundResource(R.drawable.ic_star_gray_24)
            }

            ivbFavorite.setOnClickListener {//true인 채로 전달
                RxBus.publish(RxEvents.EventFavoriteOfFavoriteList(item))
            }
        }
    }
}