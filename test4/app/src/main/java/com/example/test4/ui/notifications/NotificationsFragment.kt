package com.example.test4.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError
import com.example.test4.databinding.FragmentNotificationsBinding
import com.example.test4.R

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val images = arrayOf(
        R.drawable.cat1,
        R.drawable.cat2
    )
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupYouTubePlayer()
        setupImageViewClickListener()

        return root
    }

    private fun setupYouTubePlayer() {
        val youTubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "CJkrxF6DiJs" // 유효한 비디오 ID를 사용
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerError) {
                Log.e("YouTubePlayer", "Error occurred: $error")
            }
        })
    }

    private fun setupImageViewClickListener() {
        val imageView = binding.imageView
        val textView = binding.textView

        imageView.setImageResource(images[currentIndex])
        textView.text = currentIndex.toString()

        imageView.setOnClickListener {
            currentIndex = currentIndex + 1
            imageView.setImageResource(images[currentIndex % 2])
            textView.text = currentIndex.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
