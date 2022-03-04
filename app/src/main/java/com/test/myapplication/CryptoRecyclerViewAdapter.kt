package com.brave.bravescryptoapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.myapplication.R
import java.text.DecimalFormat

/**
 * author lgarg on 3/3/22.
 */
class CryptoRecyclerViewAdapter(var cryptoModals: ArrayList<CryptoModel>, val context: Context) :
    RecyclerView.Adapter<CryptoRecyclerViewAdapter.CryptoViewHolder>() {

    fun updateData(updatedList: ArrayList<CryptoModel>) {
        cryptoModals = updatedList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CryptoViewHolder {
        return CryptoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.crypto_element, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: CryptoViewHolder,
        position: Int
    ) {
        val cryptoModel = cryptoModals[position]
        holder.name.text = cryptoModel.name
        holder.rate.text = "$ " + CryptoRecyclerViewAdapter.df2.format(cryptoModel.price)
        holder.symbol.text = cryptoModel.btcPrice.toString() + " BTC"
    }

    override fun getItemCount(): Int {
        return cryptoModals.size
    }

    class CryptoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        internal val symbol: TextView = itemView.findViewById(R.id.idBTC)
        internal val rate: TextView = itemView.findViewById(R.id.idRate)
        internal val name: TextView = itemView.findViewById(R.id.idName)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            //Future reference
        }
    }

    companion object {
        private val df2 = DecimalFormat("#.##")
    }
}