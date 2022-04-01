package com.wolfyer.myfirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AgeAdapter(private val userList : ArrayList<UserAge>) : RecyclerView.Adapter<AgeAdapter.AgeHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgeAdapter.AgeHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_cfr,parent,false)
        return AgeHolder(itemView)
    }

    override fun onBindViewHolder(holder: AgeAdapter.AgeHolder, position: Int) {
        val userage:UserAge = userList[position]
        holder.firstname.text = userage.firstname
        holder.lastname.text = userage.lastname
        holder.age.text = userage.age.toString()
    }

    override fun getItemCount(): Int {
       return userList.size
    }
    public class AgeHolder(itemView :View):RecyclerView.ViewHolder(itemView){
        val firstname : TextView = itemView.findViewById(R.id.ed_first_name)
        val lastname : TextView = itemView.findViewById(R.id.ed_last_name)
        val age : TextView = itemView.findViewById(R.id.ed_age)
    }

}