package com.example.test4.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
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

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val contact = adapter.getContacts()[position]
                if (direction == ItemTouchHelper.RIGHT) {
                    callContact(contact)
                } else if (direction == ItemTouchHelper.LEFT) {
                    showDeleteContactDialog(position, contact)
                }
                adapter.notifyItemChanged(position) // 아이템 상태를 복원
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

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
        val contactListJson = sharedPreferences.getString("contacts", null)
        return if (contactListJson == null) {
            val dummyContacts = createDummyContacts()
            saveContactsToSharedPreferences(dummyContacts)
            dummyContacts
        } else {
            gson.fromJson(contactListJson, object : TypeToken<MutableList<Contact>>() {}.type)
        }
    }

    private fun createDummyContacts(): MutableList<Contact> {
        return mutableListOf(
            Contact("조성제", "010-6786-2747", score = 5362),
            Contact("엄마", "010-2345-6789", score = 42),
            Contact("아빠", "010-3456-7890", score = 235)
        )
    }

    private fun saveContactsToSharedPreferences(contacts: MutableList<Contact>) {
        val gson = Gson()
        val contactsJson = gson.toJson(contacts)
        val editor = sharedPreferences.edit()
        editor.putString("contacts", contactsJson)
        editor.apply()
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

    private fun callContact(contact: Contact) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
        startActivity(intent)
    }

    private fun showDeleteContactDialog(position: Int, contact: Contact) {
        AlertDialog.Builder(requireContext())
            .setTitle("연락처 삭제")
            .setMessage("정말로 ${contact.name} 연락처를 삭제하시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                adapter.deleteContact(position)
            }
            .setNegativeButton("아니오") { dialog, which ->
                adapter.notifyItemChanged(position) // 아이템 상태를 복원
            }
            .show()
    }
}
