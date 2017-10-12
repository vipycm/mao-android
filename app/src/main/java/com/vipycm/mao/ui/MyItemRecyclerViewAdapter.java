package com.vipycm.mao.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vipycm.mao.R;
import com.vipycm.mao.databinding.ItemFuncBinding;
import com.vipycm.mao.ui.MainActivity.FuncItem;
import com.vipycm.mao.ui.MainFragment.OnMainFragmentInteraction;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FuncItem} and makes a call to the
 * specified {@link OnMainFragmentInteraction}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<FuncItem> mValues;
    private final OnMainFragmentInteraction mListener;

    public MyItemRecyclerViewAdapter(OnMainFragmentInteraction listener) {
        mValues = listener.getFuncItems();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFuncBinding itemFuncBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_func, parent, false);
        return new ViewHolder(itemFuncBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Button btnFuncItem = holder.mItemFuncBinding.btnFuncItem;
        btnFuncItem.setText(mValues.get(position).name);
        btnFuncItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFuncItemClicked(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemFuncBinding mItemFuncBinding;
        public FuncItem mItem;

        public ViewHolder(ItemFuncBinding itemFuncBinding) {
            super(itemFuncBinding.getRoot());
            mItemFuncBinding = itemFuncBinding;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItemFuncBinding.btnFuncItem.getText() + "'";
        }
    }
}
