package com.example.test4.ui.notifications


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError

import androidx.fragment.app.Fragment

import com.example.test4.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val youTubePlayerView: YouTubePlayerView = binding.youtubePlayerView

        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // 유효한 비디오 ID를 사용하여 비디오를 로드합니다.
                val videoId = "hbcGx4MGUMg" // 여기에 실제 비디오 ID를 넣으세요
                youTubePlayer.loadVideo(videoId, 0f)
            }
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerError) {
                // 오류 처리
                Log.e("YouTubePlayer", "Error occurred: $error")
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}