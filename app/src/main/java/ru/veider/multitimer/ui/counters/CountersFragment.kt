package ru.veider.multitimer.ui.counters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.veider.multitimer.R
import ru.veider.multitimer.const.*
import ru.veider.multitimer.data.Counter
import ru.veider.multitimer.data.Counters
import ru.veider.multitimer.databinding.FragmentCountersBinding
import ru.veider.multitimer.ui.counters.CountersAdapter.CounterHolder
import ru.veider.multitimer.viewmodel.CountersViewModel
import ru.veider.multitimer.viewmodel.CountersViewModelFactory


class CountersFragment : Fragment(), CountersAdapter.CountersAdapterEvents {

    private var _binder: FragmentCountersBinding? = null

    private val binder get() = _binder!!
    private lateinit var viewModel: CountersViewModel

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this, CountersViewModelFactory.getInstance())[CountersViewModel::class.java].apply {
            counters().observe(this@CountersFragment.viewLifecycleOwner) { counters -> isCountersChanged(counters) }
            counter().observe(this@CountersFragment.viewLifecycleOwner) { counter -> isCounterChanged(counter) }
        }

        _binder = FragmentCountersBinding.inflate(inflater, container, false)
        binder.listView.apply {
            layoutManager = LinearLayoutManager(this@CountersFragment.requireContext())
            layoutManager?.let {
                addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL))
            }
        }

        val decorator = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL).apply {
            ContextCompat.getDrawable(requireContext(), R.drawable.divider_drawable)?.also {
                this.setDrawable(it)
            }
        }
        binder.listView.addItemDecoration(decorator)

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val movementsFlag = ItemTouchHelper.START or ItemTouchHelper.END
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, movementsFlag)
            }

            override fun onMove(recyclerView: RecyclerView, fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder): Boolean {
                val fromPosition: Int = fromViewHolder.adapterPosition
                val toPosition: Int = toViewHolder.adapterPosition
                (binder.listView.adapter as CountersAdapter).swapItems(fromPosition, toPosition)
                viewModel.saveCounters()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val counterID: Int = (viewHolder as CounterHolder).counter.id
                val dialog = MaterialAlertDialogBuilder(this@CountersFragment.requireContext())
                dialog.setMessage("Вы подтверждаете удаление таймера?")
                dialog.setPositiveButton("Да") { _, _ ->
                    viewModel.deleteCounter(counterID)
                    (binder.listView.adapter as CountersAdapter).notifyItemRemoved(position)
                }
                dialog.setNegativeButton("Нет"
                ) { _, _ ->
                    (binder.listView.adapter as CountersAdapter).notifyItemChanged(position)
                }
                dialog.show()
            }
        }).attachToRecyclerView(binder.listView)

        arguments?.let {
            val counterId = it.getInt(COUNTER_ID, -1)
            if (counterId >= 0) onClickStopButton(counterId)
        }

        setHasOptionsMenu(true)
        return binder.root
    }

    private fun isCounterChanged(counter: Counter?) {
        counter?.let {
            binder.listView.adapter?.apply {
                notifyItemChanged(viewModel.getCounters.getIndexByID(counter.id), 1)
            }
        }
    }

    private fun isCountersChanged(counters: Counters?) {
        counters?.let {
            binder.listView.adapter = CountersAdapter(this, counters)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.multi_timer, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_counter -> viewModel.addCounter()
        }
        return true
    }

    override fun onDestroyView() {
        viewModel.saveCounters()
        super.onDestroyView()
        _binder = null
    }

    override fun onClickStartButton(id: Int) {
        viewModel.startCounter(id)
    }

    override fun onClickPauseButton(id: Int) {
        viewModel.pauseCounter(id)
    }

    override fun onClickStopButton(id: Int) {
        viewModel.stopCounter(id)
    }

    override fun onTimerTitleChange(id: Int, title: String) {
        viewModel.updateTitle(id, title)
    }

    override fun onTimerSetValue(id: Int, seconds: Int) {
        viewModel.updateMaxProgress(id, seconds)
    }
}