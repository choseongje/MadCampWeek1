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
import android.widget.ImageView
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
    private var lastClickTime: Long = 0

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
            holder.catIconImageView.visibility = View.VISIBLE
        } else {
            holder.catIconImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            selectedContactName = contact.name
            onContactClick(contact)
            notifyDataSetChanged()
        }


        holder.itemView.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < 300) {
                // 더블 클릭 감지
                showResetScoreDialog(contact)
            } else {
                selectedContactName = contact.name
                onContactClick(contact)
                notifyDataSetChanged()
            }
            lastClickTime = clickTime
        }
    }

    private fun showResetScoreDialog(contact: Contact) {
        AlertDialog.Builder(context)
            .setTitle("점수 초기화")
            .setMessage("정말로 ${contact.name}의 점수를 초기화하시겠습니까?")
            .setPositiveButton("예") { dialog, which ->
                resetContactScore(contact)
            }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun resetContactScore(contact: Contact) {
        contact.score = 0
        notifyDataSetChanged()
        saveContactsToSharedPreferences()
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
        val catIconImageView: ImageView = itemView.findViewById(R.id.catIconImageView)
    }

    interface OnContactDeletedListener {
        fun onContactDeleted()
    }

    private var listener: OnContactDeletedListener? = null

    fun setOnContactDeletedListener(listener: OnContactDeletedListener) {
        this.listener = listener
    }
}
