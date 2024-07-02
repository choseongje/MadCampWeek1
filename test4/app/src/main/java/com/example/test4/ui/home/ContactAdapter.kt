package com.example.test4.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test4.R
import com.google.gson.Gson

class ContactAdapter(private val contacts: MutableList<Contact>, private val sharedPreferences: SharedPreferences, private val context: Context) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber
        holder.scoreTextView.text = "Score: ${contact.score}"

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

        holder.callButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
            context.startActivity(intent)
        }
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)
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

    fun updateContactScore(name: String, score: Int){
        val contact = contacts.find { it.name == name}
        contact?.let{
            it.score = score
            notifyDataSetChanged()
            saveContactsToSharedPreferences()
        }
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