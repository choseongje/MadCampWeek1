package com.example.test4.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.test4.R
import java.io.InputStreamReader

data class Contact(val name: String, val phoneNumber: String)

class HomeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ContactAdapter
    private lateinit var itemCountTextView: TextView
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireContext().getSharedPreferences("MyContacts", Context.MODE_PRIVATE)

        itemCountTextView = root.findViewById(R.id.itemCountTextView)
        nameEditText = root.findViewById(R.id.nameEditText)
        phoneEditText = root.findViewById(R.id.phoneEditText)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val contacts = loadContactsFromSharedPreferences()
        adapter = ContactAdapter(contacts)
        recyclerView.adapter = adapter

        updateItemCount()

        val addButton: Button = root.findViewById(R.id.addButton)
        addButton.setOnClickListener{
            onAddButtonClicked()
        }

        return root
    }

    private fun saveContactToSharedPreferences(contact: Contact) {
        val gson = Gson()
        val contactListJson = sharedPreferences.getString("contacts", "[]")
        val contacts: MutableList<Contact> = gson.fromJson(contactListJson, object : TypeToken<MutableList<Contact>>() {}.type)

        contacts.add(contact)

        val editor = sharedPreferences.edit()
        editor.putString("contacts", gson.toJson(contacts))
        editor.apply()
    }

    private fun loadContactsFromSharedPreferences(): MutableList<Contact> {
        val gson = Gson()
        val contactListJson = sharedPreferences.getString("contacts", "[]")
        return gson.fromJson(contactListJson, object : TypeToken<MutableList<Contact>>() {}.type)
    }

    private fun updateItemCount(){
        val cnt = adapter.itemCount
        itemCountTextView.text = "Count: $cnt"
    }

    private fun onAddButtonClicked(){
        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        if(name.isNotEmpty() && phone.isNotEmpty()){
            val contact = Contact(name,phone)
            adapter.addContact(contact)
            saveContactToSharedPreferences(contact)
            nameEditText.text.clear()
            phoneEditText.text.clear()
            updateItemCount()
        }
    }
}
