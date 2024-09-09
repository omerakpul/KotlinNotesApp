package com.omer.notesapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.omer.notesapp.R
import com.omer.notesapp.databinding.FragmentEditNoteBinding
import com.omer.notesapp.db.NoteDAO
import com.omer.notesapp.db.NoteDatabase
import com.omer.notesapp.model.Notes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private val mDisposable = CompositeDisposable()
    private lateinit var db : NoteDatabase
    private lateinit var noteDao : NoteDAO
    private var noteId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), NoteDatabase::class.java,"Notes").build()
        noteDao = db.noteDao()


        arguments?.let {
            noteId = EditNoteFragmentArgs.fromBundle(it).id
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(noteId>0) {
            loadNoteData(noteId)
        }

        binding.editNoteButton.setOnClickListener { editNote(it) }

        val toolbar = (activity as AppCompatActivity).supportActionBar

        toolbar?.title = "EDIT YOUR NOTE"
        toolbar?.subtitle = "You can make changes about your note"
        toolbar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                Navigation.findNavController(requireView()).navigateUp()
                true
            }
            R.id.delete -> {
                deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editNote(view: View) {
        val title = binding.titleEditText.text.toString()
        val details = binding.detailsEditText.text.toString()

        if (title.isNotEmpty() && noteId > 0) {
            val updatedNote = Notes(title, details).apply { id = noteId }

            mDisposable.add(
                noteDao.updateNote(updatedNote)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        handleResponseForEdit()
                    }, { throwable ->
                        Snackbar.make(requireView(), "Error updating note", Snackbar.LENGTH_SHORT).show()
                    })
            )
        }
    }

   private fun handleResponseForEdit(){

        val action = EditNoteFragmentDirections.actionEditNoteFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
        Snackbar.make(requireView(), "Note updated succesfully", Snackbar.LENGTH_SHORT).show()

    }

    private fun loadNoteData(noteId: Int) {

        mDisposable.add(
            noteDao.findById(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForList) {
                    throwable -> Snackbar.make(requireView(),"Error loading note", Snackbar.LENGTH_SHORT).show()
                }
        )
    }

    private fun handleResponseForList(note:Notes){

        binding.titleEditText.setText(note.title)
        binding.detailsEditText.setText(note.details)
    }


    private fun deleteNote() {
        mDisposable.add(
            noteDao.deleteNoteById(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    handleResponseForDelete()
                }, {
                        throwable -> Snackbar.make(requireView(), "Error deleting note", Snackbar.LENGTH_SHORT).show()
                })
        )
    }

    private fun handleResponseForDelete() {
        val action = EditNoteFragmentDirections.actionEditNoteFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
        Snackbar.make(requireView(), "Note deleted successfully", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}