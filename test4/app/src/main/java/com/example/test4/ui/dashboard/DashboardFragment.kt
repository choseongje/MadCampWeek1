package com.example.test4.ui.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.test4.R


class DashboardFragment : Fragment() {


    private val imageIds = listOf(
        R.drawable.image1, R.drawable.image2, R.drawable.image3,
        R.drawable.image4, R.drawable.image5, R.drawable.image6,
        R.drawable.image7, R.drawable.image8, R.drawable.image9,
        R.drawable.image1, R.drawable.image2, R.drawable.image3,
        R.drawable.image4, R.drawable.image5, R.drawable.image6,
        R.drawable.image7, R.drawable.image8, R.drawable.image9
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val gridView: GridView = root.findViewById(R.id.gridView)
        val adapter = ImageAdapter(requireContext(), imageIds) {
            imageId -> onImageClick(imageId)
        }
        gridView.adapter = adapter

        return root
    }

    private fun onImageClick(imageId: Int){
        val bundle = Bundle().apply {
            putInt("image_id", imageId)
        }
        findNavController().navigate(R.id.action_navigation_dashboard_to_imageDetailFragment, bundle)
    }
}