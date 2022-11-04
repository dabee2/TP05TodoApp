package com.dabee.tp05todoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dabee.tp05todoapp.databinding.RecyclerItemTodoBinding

class TodoRecyclerAdapter constructor(val context: Context,var todoitems:MutableList<TodoItem>): RecyclerView.Adapter<TodoRecyclerAdapter.VH>(){

    val categoryIcons:Array<Int> = arrayOf(
        R.drawable.images,
        R.drawable.ic_baseline_laptop_windows_24,
        R.drawable.ic_baseline_menu_book_24,
        R.drawable.ic_baseline_self_improvement_24,
        R.drawable.ic_baseline_sports_esports_24,
        R.drawable.ic_baseline_groups_24,
        R.drawable.ic_baseline_scatter_plot_24)


    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding:RecyclerItemTodoBinding = RecyclerItemTodoBinding.bind(itemView)
        init {
            binding.root.setOnClickListener{
                // TodoActivity 의 BottomSheet를 보여주는 기능메소드를 호출
                (context as TodoActivity).showBottomSheet(layoutPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layoutInflater:LayoutInflater = LayoutInflater.from(context)
        var itemView:View = layoutInflater.inflate(R.layout.recycler_item_todo,parent,false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.todoItemTvTitle.text= todoitems.get(position).title
        holder.binding.todoItemTvDate.text=todoitems[position].date
        holder.binding.todoItemCategoryIv.setImageResource(categoryIcons[todoitems[position].category])

//        holder.itemView.setOnClickListener()



    }

    // 코틀린언어의 함수 표기문법 중 return 값을 간략히 쓰기 위한 [함수 단순화]문법
    override fun getItemCount(): Int = todoitems.size


}