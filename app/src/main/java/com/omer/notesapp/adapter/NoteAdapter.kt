package com.omer.notesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.omer.notesapp.databinding.RecyclerRowBinding
import com.omer.notesapp.model.Notes
import com.omer.notesapp.views.ListFragmentDirections

class NoteAdapter(val noteList: List<Notes>) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    class NoteHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NoteHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = noteList[position].title
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToEditNoteFragment(noteList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }

}