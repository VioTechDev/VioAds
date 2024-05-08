package com.example.andmoduleads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter // Pass in the contact array into the constructor
    (private val mContacts: List<Contact>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var nameTextView: TextView
        var messageButton: Button

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        init {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            nameTextView = itemView.findViewById(R.id.contact_name)
            messageButton = itemView.findViewById(R.id.message_button)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.item_contact, parent, false)

        // Return a new holder instance
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contact = mContacts[position]
        val viewHolder: ViewHolder = holder as ViewHolder
        viewHolder.nameTextView.text = contact.name
        val button = viewHolder.messageButton
        button.text = if (contact.isOnline) "Message" else "Offline"
        button.isEnabled = contact.isOnline
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }
}
