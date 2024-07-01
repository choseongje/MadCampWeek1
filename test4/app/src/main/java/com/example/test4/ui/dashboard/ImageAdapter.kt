package com.example.test4.ui.dashboard

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.test4.R

class ImageAdapter(
    private val context: Context,
    private val imagePaths: List<String>,
    private val onImageClick: (String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return imagePaths.size
    }

    override fun getItem(position: Int): Any {
        return imagePaths[position]
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

        val imagePath = imagePaths[position]
        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)
        imageView.setOnClickListener {
            onImageClick(imagePaths[position])
        }

        return imageView
    }
}
