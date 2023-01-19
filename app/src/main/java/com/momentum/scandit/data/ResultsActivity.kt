package com.momentum.scandit.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.momentum.scandit.R
import com.scandit.datacapture.barcode.data.SymbologyDescription

class ResultsActivity : AppCompatActivity() {

    val RESULT_CODE_CLEAN = 1
    private val ARG_SCAN_RESULTS = "scan-results"

    fun getIntent(context: Context?, scanResults: HashSet<ScanResult>?): Intent {
        return Intent(context, ResultsActivity::class.java)
            .putExtra(ARG_SCAN_RESULTS, scanResults)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)


        // Setup recycler view.

        // Setup recycler view.
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        )

        // Receive results from previous screen and set recycler view items.

        // Receive results from previous screen and set recycler view items.
        val scanResults: ArrayList<ScanResult?> = ArrayList(
            (intent.getSerializableExtra(ARG_SCAN_RESULTS) as java.util.HashSet<ScanResult?>?)!!
        )
        recyclerView.adapter = ScanResultsAdapter(this, scanResults)

        val doneButton = findViewById<Button>(R.id.done_button)
        doneButton.setOnClickListener {
            setResult(RESULT_CODE_CLEAN)
            finish()
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }


    private class ScanResultsAdapter(
        private val context: Context,
        private val items: ArrayList<ScanResult?>
    ) :
        RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.scan_result_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.update(items[position]!!)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    private class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val dataTextView: TextView
        private val typeTextView: TextView
        fun update(scanResult: ScanResult) {
            dataTextView.text = scanResult.data
            typeTextView.text = SymbologyDescription.create(scanResult.symbology!!).readableName
        }

        init {
            dataTextView = itemView.findViewById(R.id.data_text)
            typeTextView = itemView.findViewById(R.id.type_text)
        }
    }

}