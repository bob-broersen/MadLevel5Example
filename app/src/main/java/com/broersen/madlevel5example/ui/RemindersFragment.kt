package com.broersen.madlevel5example.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.broersen.madlevel5example.R
import com.broersen.madlevel5example.model.Reminder
import com.broersen.madlevel5example.repository.ReminderRepository
import kotlinx.android.synthetic.main.fragment_reminders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RemindersFragment : Fragment() {
    private lateinit var reminderRepository: ReminderRepository
    private val reminders = arrayListOf<Reminder>()
    private val reminderAdapter = ReminderAdapter(reminders)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reminders, container, false)
    }

    private fun initViews() {
        // Initialize the recycler view with a linear layout manager, adapter
        rvReminders.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvReminders.adapter = reminderAdapter
        rvReminders.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        createItemTouchHelper().attachToRecyclerView(rvReminders)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        ObserveAddReminderResult()

        reminderRepository = ReminderRepository(requireContext())
        getRemindersFromDatabase()
    }

    private fun getRemindersFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val reminders = withContext(Dispatchers.IO) {
                reminderRepository.getAllReminders()
            }
            this@RemindersFragment.reminders.clear()
            this@RemindersFragment.reminders.addAll(reminders)
            reminderAdapter.notifyDataSetChanged()
        }

    }

    private fun ObserveAddReminderResult() {
        setFragmentResultListener(REQ_REMINDER_KEY) { resultKey, bundle -> bundle.getString(
            BUNDLE_REMINDER_KEY
        )?.let{
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    reminderRepository.insertReminder(Reminder(it))
                }
                getRemindersFromDatabase()
            }
        }?: Log.e("ReminderFragment", "Request triggered, but empty reminder text!") }
    }

    private fun createItemTouchHelper() : ItemTouchHelper{
        val callback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder):Boolean{return false}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reminderToDelete = reminders[position]
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        reminderRepository.deleteReminder(reminderToDelete)
                    }
                    getRemindersFromDatabase()
                }
            }
        }
        return ItemTouchHelper(callback)
    }
}