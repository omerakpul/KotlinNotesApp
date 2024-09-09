package com.omer.notesapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import com.omer.notesapp.R
import com.omer.notesapp.databinding.FragmentAddNoteBinding
import com.omer.notesapp.databinding.FragmentListBinding
import com.omer.notesapp.db.NoteDAO
import com.omer.notesapp.db.NoteDatabase
import com.omer.notesapp.model.Notes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : NoteDatabase
    private lateinit var noteDao : NoteDAO
    private val mDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), NoteDatabase::class.java,"Notes").build()
        noteDao = db.noteDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveNoteButton.setOnClickListener { save(it) }

    }


    private fun save(view: View?) {

        val title = binding.titleEditText.text.toString()
        val details = binding.detailsEditText.text.toString()
        val notes = Notes(title,details)

        if (title!=null){
            mDisposable.add(
                noteDao.insertNote(notes)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsert)
            )
        }

    }


    private fun handleResponseForInsert(){
        val action = AddNoteFragmentDirections.actionAddNoteFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}