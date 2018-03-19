package com.vipycm.commons.recycler;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by mao on 13/02/2018.
 */

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public BaseViewHolder createHolder(ViewGroup parent, int layoutId) {
        return new BaseViewHolder(inflateBinding(parent, layoutId));
    }

    protected <VDB extends ViewDataBinding> VDB inflateBinding(@NonNull ViewGroup parent, @LayoutRes int resource) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), resource, parent, false);
    }
}
