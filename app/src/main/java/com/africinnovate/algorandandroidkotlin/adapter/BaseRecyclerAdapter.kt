package com.africinnovate.algorandandroidkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.africinnovate.algorandandroidkotlin.BR
import com.africinnovate.algorandandroidkotlin.model.Transactions


/**
*Common adapter to load the recyclerView
 */
class  BaseRecyclerAdapter : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder>() {

    //Layout ID that needs to be given to inflate the row
    @LayoutRes
    var layoutId: Int = 0

    //List of items that will be inflated in the layout
    var items: List<Transactions> = ArrayList<Transactions>()


    /**
     * Use [setData] to update the data from the adapter
     * @param edited A list of the generic type
     ***/
    fun setData(list: List<Transactions>) {
        items = list
        notifyDataSetChanged()
    }

    /**
     *   Event handler, can be used in the row layout to register any click events,
     *   these events can be handled in the parent.
     */
    var onCustomClickItemListner = fun(view: View, position: Int): Unit = Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater,
            layoutId,
            parent,
            false)
        return BaseViewHolder(binding)
    }

    /**
     *   Count of items inside the adapter
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     *   This binds the views to the viewHolder
     */
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class BaseViewHolder(val binding: ViewDataBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(ob: Any?) {
            binding.setVariable(BR.model, ob)
            binding.setVariable(BR.handler, this)
            binding.setVariable(BR.position, adapterPosition)
            binding.executePendingBindings()
        }

        /**
         *   Propagate clicks to parent via the event handler
         */
        fun onCustomClick(view: View, position: Int) {
            onCustomClickItemListner(view, position)
        }

    }
}