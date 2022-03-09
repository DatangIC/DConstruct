package com.datangic.smartlock.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter

class BannerImageAdapter(images: List<Any>) : BannerAdapter<Any, BannerImageAdapter.ImageHolder>(images) {


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = ImageFilterView(parent.context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return ImageHolder(imageView)
    }

    override fun onBindView(holder: ImageHolder, data: Any, position: Int, size: Int) {
        when (data) {
            is Int -> holder.imageView.setImageResource(data)
            is String ->
                Glide.with(holder.itemView)
                        .load(data)
                        .into(holder.imageView)
        }
    }


    class ImageHolder(view: ImageView) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view
    }
}

