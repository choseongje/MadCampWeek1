package com.example.test4.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test4.R
import com.google.gson.Gson

class ContactAdapter(
    private val contacts: MutableList<Contact>,
    private val sharedPreferences: SharedPreferences,
    private val context: Context,
    private val onContactClick: (Contact) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var selectedContactName: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber
        holder.scoreTextView.text = "Score: ${contact.score}"

        if (contact.name == selectedContactName) {
            holder.nameTextView.paintFlags = holder.nameTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        } else {
            holder.nameTextView.paintFlags = holder.nameTextView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
        }

        holder.itemView.setOnClickListener {
            selectedContactName = contact.name
            onContactClick(contact)
            notifyDataSetChanged()
        }

        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle("연락처 삭제")
                .setMessage("이 연락처를 삭제하시겠습니까?")
                .setPositiveButton("예") { dialog, which ->
                    deleteContact(position)
                }
                .setNegativeButton("아니오", null)
                .show()
            true
        }


        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun addContact(contact: Contact) {
        contacts.add(contact)
        notifyItemInserted(contacts.size - 1)
        saveContactsToSharedPreferences()
    }

    fun deleteContact(position: Int) {
        contacts.removeAt(position)
        notifyItemRemoved(position)
        saveContactsToSharedPreferences()
        listener?.onContactDeleted()
    }

    fun updateContactScore(name: String, score: Int) {
        val contact = contacts.find { it.name == name }
        contact?.let {
            it.score = score
            notifyDataSetChanged()
            saveContactsToSharedPreferences()
        }
    }

    fun getContacts(): MutableList<Contact> {
        return contacts
    }

    private fun saveContactsToSharedPreferences() {
        val gson = Gson()
        val contactsJson = gson.toJson(contacts)
        val editor = sharedPreferences.edit()
        editor.putString("contacts", contactsJson)
        editor.apply()
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)
    }

    interface OnContactDeletedListener {
        fun onContactDeleted()
    }

    private var listener: OnContactDeletedListener? = null

    fun setOnContactDeletedListener(listener: OnContactDeletedListener) {
        this.listener = listener
    }
}
