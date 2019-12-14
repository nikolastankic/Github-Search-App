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

class RepoAdapter() : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    private var repos : MutableSet<RepoInfo> = mutableSetOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repoName: TextView = view.repo_name
        val description: TextView = view.description
        val language: TextView = view.language
        val stars: TextView = view.stars
        val updatedOn: TextView = view.updated_on

        fun bind(repo : RepoInfo) {
            repoName.setText(repo.name)
            description.setText(repo.description)
            language.setText(repo.language)
            stars.setText(repo.star_count.toString())
            val dtf = DateTimeFormatter.ofPattern("dd MMM yyyy")
            updatedOn.setText("Updated on " + dtf.format(repo.updated_on))
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
        holder.bind(repos.elementAt(position))
    }

    fun addRepositories(list : ArrayList<RepoInfo>, isNewQuery: Boolean) {
        if (isNewQuery) {
            repos = mutableSetOf()
        }
        val oldItemCount = itemCount
        repos.addAll(list)
        notifyItemRangeChanged(oldItemCount, itemCount)
    }
}