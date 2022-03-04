package com.test.myapplication

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.brave.bravescryptoapplication.CryptoModel
import com.brave.bravescryptoapplication.CryptoRecyclerViewAdapter
import com.test.myapplication.databinding.ActivityMainBinding
import org.json.JSONException
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var cryptoRV: RecyclerView? = null
    private var searchEdt: EditText? = null
    private var cryptoModalArrayList: ArrayList<CryptoModel>? = null
    private var cryptoRecyclerViewAdapter: CryptoRecyclerViewAdapter? = null
    private var loadingProgressBar: ProgressBar? = null
    private var scheduleTaskExecutor: ScheduledExecutorService? = null

    val Any.TAG: String
        get() {
            return javaClass.simpleName
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intializeView()
    }

    private fun intializeView() {
        loadingProgressBar = binding.idPBLoading
        cryptoRV = binding.idRVcurrency
        cryptoModalArrayList = ArrayList()

        cryptoRecyclerViewAdapter = CryptoRecyclerViewAdapter(ArrayList(), this)

        cryptoRV?.layoutManager = LinearLayoutManager(this)
        cryptoRV?.adapter = cryptoRecyclerViewAdapter

        getData()
    }

    private fun filter(filter: String) {
        val filteredlist: ArrayList<CryptoModel> = ArrayList()
        // running a for loop to search the data from our array list.
        for (item in cryptoModalArrayList!!) {
            // on below line we are getting the item which are
            // filtered and adding it to filtered list.
            if (item.name?.lowercase(Locale.getDefault())
                    ?.contains(filter.lowercase(Locale.getDefault())) == true
            ) {
                filteredlist.add(item)
            }
        }
        // on below line we are checking
        // weather the list is empty or not.
        if (filteredlist.isEmpty()) {
            // if list is empty we are displaying a toast message.
            Toast.makeText(this, "No currency found..", Toast.LENGTH_SHORT).show()
        } else {
            // on below line we are calling a filter
            // list method to filter our list.
            cryptoRecyclerViewAdapter?.updateData(filteredlist)
        }
    }

    private fun getData() {
        val url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"
        // creating a variable for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(this)
        // making a json object request to fetch data from API.
        val jsonObjectRequest: JsonObjectRequest = object :
            JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    // inside on response method extracting data
                    // from response and passing it to array list
                    // on below line we are making our progress
                    // bar visibility to gone.
                    loadingProgressBar?.visibility = View.GONE
                    try {
                        // extracting data from json.
                        val dataArray = response.getJSONArray("data")

                        val btcValue =
                            dataArray.getJSONObject(0).getJSONObject("quote").getJSONObject("USD")
                                .getDouble("price")
                        for (i in 0 until dataArray.length()) {
                            val dataObj = dataArray.getJSONObject(i)
                            val name = dataObj.getString("name")
                            val quote = dataObj.getJSONObject("quote")
                            val USD = quote.getJSONObject("USD")
                            val price = USD.getDouble("price")
                            val symbol = dataObj.getString("symbol")
                            val btcPrice = price / btcValue
                            val lastUpdated = USD.getString("last_updated")
                            // adding all data to our array list.
                            val cryptoModel =
                                CryptoModel(name, symbol, price, lastUpdated, btcPrice)
                            cryptoModalArrayList?.add(cryptoModel)

                            // Example of a call to a native method
                            appendLog(
                                stringFromJNITimeStamp(cryptoModel) + ", " + stringFromJNISymbol(
                                    cryptoModel
                                ) + ", " + stringFromJNIPrice(cryptoModel).toString() + " USD"
                            )
                        }

                        cryptoModalArrayList?.let { cryptoRecyclerViewAdapter?.updateData(it) }

                        // notifying adapter on data change.
                        cryptoRecyclerViewAdapter?.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        // handling json exception.
                        Log.e("Love", "Exceeption " + e)
                        Toast.makeText(
                            this,
                            "Something went amiss. Please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener { // displaying error response when received any error.

                    Toast.makeText(
                        this,
                        "Something Error. Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {

            override fun getHeaders(): MutableMap<String, String> {
                // in this method passing headers as
                // key along with value as API keys.
                val headers: HashMap<String, String> = HashMap()
                headers["X-CMC_PRO_API_KEY"] = "d601ebb9-1f63-40dc-9bf0-3e5fd2c02f98"// actual
                // at last returning headers
                return headers
            }
        }
        // calling a method to add our
        // json object request to our queue.
        queue.add(jsonObjectRequest)
    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNITimeStamp(toString: CryptoModel): String

    external fun stringFromJNIPrice(toString: CryptoModel): Double

    external fun stringFromJNISymbol(toString: CryptoModel): String

    companion object {
        // Used to load the 'myapplication' library on application startup.
        init {
            System.loadLibrary("myapplication")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView? = searchItem?.actionView as? SearchView

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5)
        // This schedule a runnable task every 5 seconds
        scheduleTaskExecutor?.scheduleAtFixedRate(
            { getData() },
            0,
            5,
            TimeUnit.SECONDS
        )
    }

    private fun appendLog(text: String?) {
        val logFile = File("sdcard/Log.file")
        Log.e("Love", "Path " + logFile.absolutePath)
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.message?.let { Log.e(TAG, it) }
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        scheduleTaskExecutor?.shutdown()
        super.onPause()
    }
}