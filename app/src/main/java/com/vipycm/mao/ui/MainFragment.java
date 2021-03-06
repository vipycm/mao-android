package com.vipycm.mao.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentMainBinding;
import com.vipycm.mao.ui.MainActivity.FuncItem;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMainFragmentInteraction}
 * interface.
 */
public class MainFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentMainBinding mFragmentMainBinding;
    private OnMainFragmentInteraction mListener;

    private int mColumnCount = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.i("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        RecyclerView recyclerView = mFragmentMainBinding.list;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(mListener));

        return mFragmentMainBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteraction) {
            mListener = (OnMainFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFuncItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMainFragmentInteraction {
        void onFuncItemClicked(FuncItem item);

        List<FuncItem> getFuncItems();
    }

}
