package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.RepoInfo
import kotlinx.android.synthetic.main.repository_layout.view.*
import java.time.format.DateTimeFormatter

class RepoAdapter : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    private var repos : ArrayList<RepoInfo> = ArrayList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repoName: TextView = view.repo_name
        val description: TextView = view.description
        val language: TextView = view.language
        val stars: TextView = view.stars
        val updatedOn: TextView = view.updated_on

        fun bind(repo : RepoInfo) {
            repoName.text = repo.name
            if (!repo.description.equals("null")) {
                description.text = repo.description
            }
            else {
                description.text = ""
            }
            if (!repo.language.equals("null")) {
                language.text = repo.language
            }
            else {
                language.text = "N/A"
            }
            stars.text = repo.star_count.toString()
            val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy")
            updatedOn.text = "Updated on " + dtf.format(repo.updated_on)
            itemView.setOnClickListener {
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repo.link)))
            }
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

    fun addRepositories(list : ArrayList<RepoInfo>, isNewQuery: Boolean) {
        if (isNewQuery) {
            repos.clear()
        }
        val oldItemCount = itemCount
        repos.addAll(list)
        notifyItemRangeChanged(oldItemCount, itemCount)
    }
}