package com.penguino.web

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.penguino.R

class NameSuggestionsRecyclerViewAdapter(
    private val names: ArrayList<String>,
    private val onNameSelectHandler: (String) -> Unit
): RecyclerView.Adapter<NameSuggestionsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(
        view: View,
        private val onNameSelectHandler: (String) -> Unit
    ): RecyclerView.ViewHolder(view) {
        val name: TextView

        init {
            name = view.findViewById(R.id.text_suggested_name)
            view.setOnClickListener {
                onNameSelectHandler(name.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_suggested_name, parent, false)
        return ViewHolder(view, onNameSelectHandler)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = names[position]
    }
}