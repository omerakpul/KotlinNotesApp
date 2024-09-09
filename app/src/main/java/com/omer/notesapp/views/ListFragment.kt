package com.omer.notesapp.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.omer.notesapp.R
import com.omer.notesapp.adapter.NoteAdapter
import com.omer.notesapp.databinding.FragmentListBinding
import com.omer.notesapp.db.NoteDAO
import com.omer.notesapp.db.NoteDatabase
import com.omer.notesapp.model.Notes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : NoteDatabase
    private lateinit var noteDao : NoteDAO
    private val mDisposable = CompositeDisposable()
    private var selectedNote : Notes? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), NoteDatabase::class.java,"Notes").build()
        noteDao = db.noteDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener { addNewNote(it) }
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showList()

            val toolbar = (activity as AppCompatActivity).supportActionBar
            toolbar?.title = "Notes App" // Fragment'a göre uygun title ayarla
            toolbar?.subtitle = "Organize your notes"
            toolbar?.setDisplayHomeAsUpEnabled(false)
            setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchNotes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchNotes(it) }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun searchNotes(query: String) {
        mDisposable.add(
            noteDao.searchNotes(query) // searchNotes fonksiyonunu dao'da tanımlamanız gerekiyor
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }


    private fun showList() {
        mDisposable.add(
            noteDao.getAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(notes: List<Notes>){
        val adapter = NoteAdapter(notes)
        binding.listRecyclerView.adapter = adapter
    }

    fun addNewNote(view: View){
        val action = ListFragmentDirections.actionListFragmentToAddNoteFragment(non="new",id=0)
        Navigation.findNavController(view).navigate(action)
    }


    private fun handleResponseForInsert(){
        val action = AddNoteFragmentDirections.actionAddNoteFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}