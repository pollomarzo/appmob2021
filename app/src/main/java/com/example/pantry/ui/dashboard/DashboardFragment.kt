package com.example.pantry.ui.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pantry.R
import com.example.pantry.data.items.Item
import com.example.pantry.data.items.ItemViewModel
import com.example.pantry.data.items.ItemViewModelFactory
import com.example.pantry.data.items.ItemsApplication
import com.example.pantry.ui.browseItems.SimpleAdapter


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    private val itemViewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory((activity?.application as ItemsApplication).repository)
    }
    private lateinit var missingAdapter: SimpleAdapter
    private lateinit var percentageAdapter: SimpleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        missingAdapter = SimpleAdapter {
            it.name
        }
        // i wasted two hours because binding gave no error after finding jack shit
        // SO MUCH FOR COMPILE TIME SAFETY FUCK FUCK FUCK FUCK FUCK
        val miss = root.findViewById<RecyclerView>(R.id.missing_items)
        miss.layoutManager = LinearLayoutManager(activity)
        miss.adapter = missingAdapter

        val perc = root.findViewById<RecyclerView>(R.id.percentage_items)
        Log.d("dashboard", getMissing().toString())
        percentageAdapter = SimpleAdapter {
            "${it.amount}% of ${it.name}"
        }
        perc.layoutManager = LinearLayoutManager(activity)
        perc.adapter = percentageAdapter

        itemViewModel.allItem.observe(viewLifecycleOwner, Observer {
            missingAdapter.submitList(getMissing())
            percentageAdapter.submitList(getPercentages())
        })

        val clip = root.findViewById<Button>(R.id.export_button)
        clip.setOnClickListener {
            val clipboard = this.context?.let { it1 ->
                getSystemService(
                    it1,
                    ClipboardManager::class.java
                )
            } as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("stuff i should buy", getMissingString())
            clipboard.setPrimaryClip(clip)
        }

        return root
    }

    private fun getMissing(): List<Item> {
        return itemViewModel.allItem.value?.filter {
            it.amount == 0
        } ?: ArrayList<Item>()
    }

    private fun getMissingString(): String {
        var str = ""
        for (i in getMissing()) str += "- ${i.name}\n"
        return str
    }

    private fun getPercentages(): List<Item> {
        val typesToAmount = HashMap<String, Int>()
        var totalItems = 0
        itemViewModel.allItem.value?.map {
            totalItems += it.amount
            typesToAmount[it.type] = (typesToAmount[it.type] ?: 0) + it.amount
        }
        return typesToAmount.map {
            Item(
                "", it.key, 0, "",
                (it.value.toDouble() / totalItems * 100).toInt(), ""
            )
        }
    }
}