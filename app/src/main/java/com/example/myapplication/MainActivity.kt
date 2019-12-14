package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.example.myapplication.model.RepoInfo
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val api_url = "https://api.github.com/search/repositories?q="
    private var searchQuery = ""
    private var reposCount : Int = 0
    private var pageCount : Int = 0
    private val maxPageCount : Int = 34
    private var loadedPageCount : Int = 0

    private lateinit var requestQueue : RequestQueue
    private lateinit var repoAdapter : RepoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        recycler.layoutManager = LinearLayoutManager(this)
        repoAdapter = RepoAdapter()
        recycler.adapter = repoAdapter
        recycler.addOnScrollListener(object : PaginationScrollListener(recycler.layoutManager as LinearLayoutManager) {
            override fun addNextPage() {
                if (loadedPageCount < pageCount) {
                    apiCall(false, loadedPageCount + 1)
                }
                isLoading = false
            }

        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchQuery = p0
                    recycler.scrollToPosition(0)
                    apiCall(true, 1)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

    }

    private fun apiCall(isNewQuery : Boolean, pageNumber : Int) {
        var url = api_url + searchQuery + "&page=" + pageNumber
        var request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> {response ->
                reposCount = response
                    .getInt("total_count")

                pageCount = min(ceil(reposCount / 30.0).toInt(), maxPageCount)

                handleResponse(response
                    .getJSONArray("items"), isNewQuery);

                loadedPageCount = pageNumber
                Log.d("loaded page ", loadedPageCount.toString())
                val itemCount = repoAdapter.itemCount
                textView.text = "$reposCount results found - showing $itemCount"

                requestQueue.cancelAll(object : RequestQueue.RequestFilter {
                    override fun apply(request: Request<*>?): Boolean {
                        return true
                    }

                })
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG)
            })
        requestQueue.add(request)
    }

    private fun handleResponse(repositories : JSONArray, isNewQuery: Boolean) {
        var list : ArrayList<RepoInfo> = ArrayList()
        for (i in 0 until repositories.length()) {
            var repo: JSONObject = repositories.getJSONObject(i)
            val dtf = DateTimeFormatter.ofPattern(("yyyy-MM-dd'T'HH:mm:ss'Z'"))
            list.add(
                RepoInfo(
                    repo.getString("full_name"),
                    repo.getString("description"),
                    repo.getString("language"),
                    repo.getInt("stargazers_count"),
                    LocalDateTime.from(dtf.parse(repo.getString("updated_at"))),
                    repo.getString("html_url")
                )
            )
        }
        repoAdapter.addRepositories(list, isNewQuery)
    }

}
