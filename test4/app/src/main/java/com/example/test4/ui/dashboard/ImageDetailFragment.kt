package com.example.test4.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.test4.R

class ImageDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_detail, container, false)
        val imageView: ImageView = view.findViewById(R.id.imageViewDetail)
        val imageId = arguments?.getInt("image_id") ?: throw IllegalArgumentException("Image ID is required")
        imageView.setImageResource(imageId)
        return view
    }

}