package com.example.test4.ui.home

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

    private lateinit var adapter: ContactAdapter
    private lateinit var itemCountTextView: TextView
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        itemCountTextView = root.findViewById(R.id.itemCountTextView)

        nameEditText = root.findViewById(R.id.nameEditText)
        phoneEditText = root.findViewById(R.id.phoneEditText)

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val contacts = loadContactsFromJson()
        adapter = ContactAdapter(contacts)
        recyclerView.adapter = adapter

        updateItemCount()

        val addButton: Button = root.findViewById(R.id.addButton)
        addButton.setOnClickListener{
            onAddButtonClicked()
        }

        return root
    }

    private fun loadContactsFromJson(): MutableList<Contact> {
        val inputStream = resources.openRawResource(R.raw.contacts)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Contact>>() {}.type
        return Gson().fromJson(reader, type)
    }

    private fun updateItemCount(){
        val cnt = adapter.itemCount
        itemCountTextView.text = "Count: $cnt"
    }

    private fun onAddButtonClicked(){
        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        if(name.isNotEmpty() && phone.isNotEmpty()){
            addContact(Contact(name,phone))
            nameEditText.text.clear()
            phoneEditText.text.clear()
            updateItemCount()
        }
    }

    private fun addContact(contact: Contact) {
        adapter.addContact(contact)
        updateItemCount()
    }
}
