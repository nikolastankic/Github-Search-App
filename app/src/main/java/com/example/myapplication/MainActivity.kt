package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
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
import kotlinx.android.synthetic.main.repository_layout.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val url = "https://api.github.com/search/repositories?q="
    private var searchQuery = ""

    private lateinit var requestQueue : RequestQueue

    private lateinit var repoAdapter : RepoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = RepoAdapter(ArrayList())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchQuery = p0
                    apiCall()
                    return true
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })

        apiCall()
    }

    private fun apiCall() {
        requestQueue.add(
            JsonObjectRequest(Request.Method.GET, url + searchQuery, null,
                Response.Listener<JSONObject> {response ->
                    val reposCount = response
                        .getInt("total_count")
                    textView.text = "$reposCount results"
                    handleResponse(response
                        .getJSONArray("items"));
                },
                Response.ErrorListener {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                })
        )
    }

    private fun handleResponse(repositories : JSONArray) {
        var list : ArrayList<RepoInfo> = ArrayList()
        for (i in 0 until repositories.length()) {
            var repo: JSONObject = repositories.getJSONObject(i)
            list.add(
                RepoInfo(
                    repo.getString("full_name"),
                    repo.getString("description"),
                    repo.getString("language"),
                    repo.getInt("stargazers_count"),
                    LocalDateTime.now()
                )
            )
        }
        Log.d("list size ", list.size.toString())
        recycler.adapter = RepoAdapter(list)
    }

    class RepoAdapter(val repos : ArrayList<RepoInfo>) : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

        class ViewHolder(holder: View) : RecyclerView.ViewHolder(holder) {
            val repoName: TextView = holder.repo_name
            val description: TextView = holder.description
            val language: TextView = holder.language
            val stars: TextView = holder.stars
            val updatedOn: TextView = holder.updated_on

            fun bind(repo : RepoInfo) {
                repoName.setText(repo.name)
                description.setText(repo.description)
                language.setText(repo.language)
                stars.setText(repo.star_count.toString())
                updatedOn.setText("Updated on " + repo.updated_on.toString())
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
           return ViewHolder(
               LayoutInflater
                   .from(parent.context)
                   .inflate(R.layout.repository_layout, parent, false)
           )
        }

        override fun getItemCount(): Int {
            return repos.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(repos[position])
        }
    }

}
