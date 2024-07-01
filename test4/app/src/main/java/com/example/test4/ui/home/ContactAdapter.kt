package com.example.test4.ui.home

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test4.R
import com.google.gson.Gson

class ContactAdapter(private val contacts: MutableList<Contact>, private val sharedPreferences: SharedPreferences) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber

        holder.deleteButton.setOnClickListener {
            deleteContact(position)
        }
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }


    interface OnContactDeletedListener{
        fun onContactDeleted()
    }
    private var listener: OnContactDeletedListener? = null

    fun setOnContactDeletedListner(listener: OnContactDeletedListener){
        this.listener=listener
    }


    override fun getItemCount(): Int = contacts.size

    fun addContact(contact: Contact) {
        contacts.add(contact)
        notifyItemInserted(contacts.size -1)
    }

    fun deleteContact(position: Int){
        contacts.removeAt(position)
        notifyItemRemoved(position)
        saveContactsToSharedPreferences()
        listener?.onContactDeleted()
    }

    fun getContacts(): MutableList<Contact>{
        return contacts
    }



    private fun saveContactsToSharedPreferences(){
        val gson = Gson()
        val contactsJson = gson.toJson(contacts)
        val editor = sharedPreferences.edit()
        editor.putString("contacts", contactsJson)
        editor.apply()
    }

}