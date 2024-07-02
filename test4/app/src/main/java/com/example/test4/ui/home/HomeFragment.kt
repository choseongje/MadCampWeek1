package com.example.test4.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.test4.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

data class Contact(
    val name: String,
    val phoneNumber: String,
    var score: Int = 0 // 점수 필드 추가
)

class HomeFragment : Fragment(), ContactAdapter.OnContactDeletedListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireContext().getSharedPreferences("MyContacts", Context.MODE_PRIVATE)
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val contacts = loadContactsFromSharedPreferences()
        adapter = ContactAdapter(contacts, sharedPreferences, requireContext()) { contact ->
            onContactSelected(contact)
        }
        recyclerView.adapter = adapter

        adapter.setOnContactDeletedListener(this)

        val addButton: FloatingActionButton = root.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddContactDialog()
        }

        return root
    }

    override fun onContactDeleted() {
        // 추가적인 동작 필요시 여기에 작성
    }

    private fun onContactSelected(contact: Contact) {
        val editor = sharedPreferences.edit()
        editor.putString("selected_contact_name", contact.name)
        editor.apply()
    }

    private fun saveContactToSharedPreferences(contact: Contact) {
        val gson = Gson()
        val contacts = adapter.getContacts()
        val editor = sharedPreferences.edit()
        editor.putString("contacts", gson.toJson(contacts))
        editor.apply()
    }

    private fun loadContactsFromSharedPreferences(): MutableList<Contact> {
        val gson = Gson()
        val contactListJson = sharedPreferences.getString("contacts", "[]")
        return gson.fromJson(contactListJson, object : TypeToken<MutableList<Contact>>() {}.type)
    }

    private fun showAddContactDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("연락처 추가")

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null)
        val nameEditText: EditText = view.findViewById(R.id.nameEditText)
        val phoneEditText: EditText = view.findViewById(R.id.phoneEditText)

        builder.setView(view)

        builder.setPositiveButton("추가") { dialog, which ->
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            if (name.isNotEmpty() && phone.isNotEmpty()) {
                val contact = Contact(name, phone)
                adapter.addContact(contact)
                saveContactToSharedPreferences(contact)
            }
        }

        builder.setNegativeButton("취소", null)

        val dialog = builder.create()
        dialog.show()
    }
}
