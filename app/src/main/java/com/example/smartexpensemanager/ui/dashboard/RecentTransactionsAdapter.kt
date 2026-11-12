package com.example.smartexpensemanager.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.data.remote.dto.TransactionDto
import com.example.smartexpensemanager.databinding.ItemRecentTransactionBinding
import com.example.smartexpensemanager.util.toCurrency

class RecentTransactionsAdapter(
    private val maxItems : Int = 5,
    private val onItemClick: (TransactionDto) -> Unit = {}
) : ListAdapter<TransactionDto, RecentTransactionsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = ItemRecentTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return minOf(super.getItemCount(), maxItems)
    }
    inner class ViewHolder(
        private val binding: ItemRecentTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionDto) {
            val context = binding.root.context

            //gan thong tin
            binding.itemTitle.text = item.note?.ifBlank { item.type } ?: item.type
            binding.itemSubtitle.text = item.transactionDate
            //Kiem tra thu nhap hay chi tieu
            val isIncome = item.type.equals("income", ignoreCase = true)
            val formatterAmount = item.amount.toCurrency()

            binding.itemAmount.text = if (isIncome) "+$formatterAmount" else "-$formatterAmount"
            binding.itemAmount.setTextColor(
                ContextCompat.getColor(
                    context, if (isIncome) R.color.finora_emerald else R.color.finora_expense))
            binding.itemSource.text = if (isIncome) "Thu Nhap" else "Chi Tieu"
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TransactionDto>() {
            override fun areItemsTheSame(
                oldItem: TransactionDto, newItem: TransactionDto
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TransactionDto, newItem: TransactionDto
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}