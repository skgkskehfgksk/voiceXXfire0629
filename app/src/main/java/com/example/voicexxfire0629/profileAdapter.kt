package com.example.voicexxfire0629

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfileAdapter(private val context: Context) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    var datas = mutableListOf<ProfileData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_ex,parent,false)
        return ViewHolder(view)



    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.tv_rv_name)

        private val imgProfile: ImageView = itemView.findViewById(R.id.img_rv_photo)

        fun bind(item: ProfileData) {
            txtName.text = item.name

            Glide.with(itemView).load(item.img).into(imgProfile)

            itemView.setOnClickListener {
                Intent(context, ProfileDetailActivity::class.java).apply {
                    putExtra("data", item)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(this)
                }
            }
        }
    }
}
