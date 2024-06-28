package com.example.test4.ui.dashboard

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.test4.R

class ImageAdapter(
    private val context: Context,
    private val imageIds: List<Int>,
    private val onImageClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return imageIds.size
    }

    override fun getItem(position: Int): Any {
        return imageIds[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val itemWidth = screenWidth / 3
            imageView.layoutParams = ViewGroup.LayoutParams(itemWidth, itemWidth)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(4, 4, 4, 4)
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(imageIds[position])
        imageView.setOnClickListener {
            onImageClick(imageIds[position])
        }

        return imageView
    }
}
