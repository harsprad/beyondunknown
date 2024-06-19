package com.example.beyondunknown

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beyondunknown.databinding.ItemBookListBinding


class BookListAdapter(
    private var bookList : List<Book>
) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

    inner class BookListViewHolder(val binding: ItemBookListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val binding = ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.binding.tvTitle.text = bookList[position].title
        holder.binding.tvTitle.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SecondaryActivity::class.java)
            intent.putExtra("EXTRA_BOOK_TITLE", bookList[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}