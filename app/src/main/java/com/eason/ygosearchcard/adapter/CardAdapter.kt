package com.eason.ygosearchcard.adapter

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.eason.ygosearchcard.R
import com.eason.ygosearchcard.bean.Card


class CardAdapter(private val context: Context?, private val datas: List<Card>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {


    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder?.cardName?.text = datas[position].desc
        if (context!=null) Glide.with(context).load(datas[position].img).into(holder?.cardImg!!)
        holder.cardName.movementMethod = ScrollingMovementMethod.getInstance()


    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder
            = CardViewHolder(LayoutInflater.from(context).inflate(R.layout.card_item_layout,parent,false))

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardName: TextView = view.findViewById(R.id.tv_cardname)
        var cardImg :ImageView = view.findViewById(R.id.iv_card)
    }

}