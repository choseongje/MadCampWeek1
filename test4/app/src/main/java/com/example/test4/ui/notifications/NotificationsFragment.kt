package com.example.test4.ui.notifications

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test4.R
import com.example.test4.databinding.FragmentNotificationsBinding
import com.example.test4.ui.home.Contact
import com.example.test4.ui.home.ContactAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var contacts: MutableList<Contact>
    private lateinit var contactAdapter: ContactAdapter

    private val images = arrayOf(
        R.drawable.cat1,
        R.drawable.cat2
    )
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val imageSwitchRunnable = Runnable {
        binding.imageView.setImageResource(images[0])
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferences = requireContext().getSharedPreferences("MyContacts", Context.MODE_PRIVATE)
        contacts = loadContactsFromSharedPreferences()
        contactAdapter = ContactAdapter(contacts, sharedPreferences, requireContext())

        setupYouTubePlayer()
        setupImageViewClickListener()

        return root
    }

    private fun loadContactsFromSharedPreferences(): MutableList<Contact> {
        val gson = Gson()
        val contactListJson = sharedPreferences.getString("contacts", "[]")
        return gson.fromJson(contactListJson, object : TypeToken<MutableList<Contact>>() {}.type)
    }

    private fun saveContactsToSharedPreferences() {
        val gson = Gson()
        val contactsJson = gson.toJson(contacts)
        val editor = sharedPreferences.edit()
        editor.putString("contacts", contactsJson)
        editor.apply()
    }

    private fun setupYouTubePlayer() {
        val youTubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "CJkrxF6DiJs" // 유효한 비디오 ID를 사용
                youTubePlayer.loadVideo(videoId, 0f)
                youTubePlayer.mute() // 유튜브 플레이어 음소거 설정
            }
        })
    }

    private fun setupImageViewClickListener() {
        val imageView = binding.imageView
        val textView = binding.textView

        imageView.setImageResource(images[currentIndex])
        textView.text = currentIndex.toString()

        imageView.setOnClickListener {
            currentIndex += 1
            imageView.setImageResource(images[1])
            textView.text = currentIndex.toString()

            // PopCat 클릭 시 점수 증가
            val contactName = "조성제" // 실제 연락처 이름으로 변경해야 합니다
            val contact = contacts.find { it.name == contactName }
            contact?.let {
                it.score += 1
                contactAdapter.updateContactScore(contactName, it.score)
                saveContactsToSharedPreferences()
            }

            handler.postDelayed({
                imageView.setImageResource(images[0])
            }, 100) // 100ms 후에 이미지 변경
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
