package com.vipycm.commons.recycler;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mao on 13/02/2018.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public ViewDataBinding viewBinding;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(ViewDataBinding viewBinding) {
        super(viewBinding.getRoot());
        this.viewBinding = viewBinding;
    }
}
