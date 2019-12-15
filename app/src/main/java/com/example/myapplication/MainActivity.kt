package com.example.myapplication

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.model.RepoInfo
import kotlinx.android.synthetic.main.activity_main.*
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
                    progressBar.show()
                    apiCall(false, loadedPageCount + 1)
                }
            }
        })
        recycler.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 8
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchQuery = p0
                    recycler.scrollToPosition(0)
                    requestQueue.cancelAll { true }
                    apiCall(true, 1)
                    searchView.clearFocus()
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
        val url = "$api_url$searchQuery&page=$pageNumber"
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> {response ->
                handleResponse(response, isNewQuery);
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG)
            })
        requestQueue.add(request)
    }

    private fun handleResponse(response : JSONObject, isNewQuery: Boolean) {
        reposCount = response.getInt("total_count")
        pageCount = min(ceil(reposCount / 30.0).toInt(), maxPageCount)
        val repositories = response.getJSONArray("items")

        val list : ArrayList<RepoInfo> = ArrayList()
        for (i in 0 until repositories.length()) {
            var repo: JSONObject = repositories.getJSONObject(i)
            val dtf = DateTimeFormatter.ofPattern(("yyyy-MM-dd'T'HH:mm:ss'Z'"))
            list.add(
                RepoInfo(
                    repo.getString("full_name"),
                    repo.getString("description"),
                    repo.getString("language"),
                    repo.getInt("stargazers_count"),
                    LocalDateTime.from(dtf.parse(repo.getString("pushed_at"))),
                    repo.getString("html_url")
                )
            )
        }
        repoAdapter.addRepositories(list, isNewQuery)

        progressBar.hide()
        PaginationScrollListener.isLoading = false
        ++loadedPageCount
        val itemCount = repoAdapter.itemCount
        textView.text = "showing $itemCount out of $reposCount results"
    }

}
