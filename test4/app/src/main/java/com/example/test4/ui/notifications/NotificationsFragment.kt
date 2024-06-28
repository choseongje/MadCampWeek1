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
import com.example.test4.R
import android.widget.ImageView
import android.widget.TextView

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

        val youTubePlayerView: YouTubePlayerView = binding.youtubePlayerView

        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // 유효한 비디오 ID를 사용하여 비디오를 로드합니다.
                val videoId = "CJkrxF6DiJs" // 여기에 실제 비디오 ID를 넣으세요
                youTubePlayer.loadVideo(videoId, 0f)
            }
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerError) {
                // 오류 처리
                Log.e("YouTubePlayer", "Error occurred: $error")
            }
        })

        // 이미지 뷰와 텍스트 뷰 설정
        val imageView: ImageView = binding.imageView
        val textView: TextView = binding.textView

        imageView.setImageResource(images[currentIndex])
        textView.text = "  $currentIndex" // 초기화 시 인덱스 설정
        imageView.setOnClickListener {
            // 다음 이미지로 전환
            currentIndex = (currentIndex + 1)
            imageView.setImageResource(images[currentIndex % images.size])
            // 텍스트 뷰 업데이트
            textView.text = "   $currentIndex"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
