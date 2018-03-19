package com.vipycm.mao.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.commons.recycler.BaseRecyclerViewAdapter;
import com.vipycm.commons.recycler.BaseViewHolder;
import com.vipycm.mao.databinding.FragmentScrollBinding;
import com.vipycm.mao.databinding.ItemSampleBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * ScrollFragment
 * Created by mao on 2018/2/13.
 */
public class ScrollFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentScrollBinding mDataBinding;
    private RecyclerView mRightRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scroll, container, false);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("ScrollFragment " + i);
        }
        mRightRecyclerView = new RecyclerView(getContext());
        mRightRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1080, ViewGroup.LayoutParams.MATCH_PARENT);
        mRightRecyclerView.setLayoutParams(params);
        mRightRecyclerView.setAdapter(new RightRecyclerViewAdapter(list));
        mRightRecyclerView.addOnScrollListener(mOnScrollListener);

        mDataBinding.leftRecyclerView.setAdapter(new LeftRecyclerViewAdapter(list));
        mDataBinding.leftRecyclerView.addOnScrollListener(mOnScrollListener);
        mDataBinding.horizontalRecyclerView.setAdapter(new HorizontalRecyclerViewAdapter());
        return mDataBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();
    }

    int y = 0;

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy==0){
                if (recyclerView == mRightRecyclerView) {
                    log.e("right scroll y:" + y + " dx:" + dx + " dy:" + dy);
                } else {
                    log.e("left scroll y:" + y + " dx:" + dx + " dy:" + dy);
                }
                return;
            }

            y += dy;
            LinearLayoutManager linearLayoutManager;
            //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
            if (recyclerView == mRightRecyclerView) {
                log.i("right scroll y:" + y + " dx:" + dx + " dy:" + dy);
                linearLayoutManager = (LinearLayoutManager) mDataBinding.leftRecyclerView.getLayoutManager();
            } else {
                log.i("left scroll y:" + y + " dx:" + dx + " dy:" + dy);
                linearLayoutManager = (LinearLayoutManager) mRightRecyclerView.getLayoutManager();
            }
            linearLayoutManager.scrollToPositionWithOffset(0, -y);
        }
    };

    private class LeftRecyclerViewAdapter extends BaseRecyclerViewAdapter {

        List<String> mList;

        public LeftRecyclerViewAdapter(List<String> list) {
            mList = list;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return createHolder(parent, R.layout.item_sample);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            ItemSampleBinding viewBinding = (ItemSampleBinding) holder.viewBinding;
            viewBinding.textView.setText(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    private class HorizontalRecyclerViewAdapter extends BaseRecyclerViewAdapter {

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BaseViewHolder(mRightRecyclerView);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private class RightRecyclerViewAdapter extends BaseRecyclerViewAdapter {

        List<String> mList;

        public RightRecyclerViewAdapter(List<String> list) {
            mList = list;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return createHolder(parent, R.layout.item_sample);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            ItemSampleBinding viewBinding = (ItemSampleBinding) holder.viewBinding;
            viewBinding.textView.setText(mList.get(position) + mList.get(position) + mList.get(position) + mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
