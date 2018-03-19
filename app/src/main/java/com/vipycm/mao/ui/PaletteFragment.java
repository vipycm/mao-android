package com.vipycm.mao.ui;

import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.commons.recycler.BaseRecyclerViewAdapter;
import com.vipycm.commons.recycler.BaseViewHolder;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentPaletteBinding;
import com.vipycm.mao.databinding.ItemPaletteBinding;

/**
 * Created by mao on 19/03/2018.
 */

public class PaletteFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentPaletteBinding mDataBinding;

    private int[] mData = {
            R.mipmap.ltc,
            R.mipmap.usd,
            R.mipmap.binance,
            R.mipmap.eth,
            R.mipmap.abucoins,
            R.mipmap.act,
            R.mipmap.ada,
            R.mipmap.adx,
            R.mipmap.ae,
            R.mipmap.aex,
            R.mipmap.agi,
            R.mipmap.aion,
            R.mipmap.allcoin,
            R.mipmap.amp,
            R.mipmap.ant,
            R.mipmap.appc,
            R.mipmap.ardr,
            R.mipmap.ark,
            R.mipmap.arn,
            R.mipmap.atm,
            R.mipmap.average,
            R.mipmap.awr,
            R.mipmap.bat,
            R.mipmap.bay,
            R.mipmap.bcc,
            R.mipmap.bch,
            R.mipmap.bcn,
            R.mipmap.bco,
            R.mipmap.bcpt,
            R.mipmap.bdl,
            R.mipmap.bela,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_palette, container, false);
        mDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDataBinding.recyclerView.setAdapter(new PaletteAdapter());
        return mDataBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();
    }

    private class PaletteAdapter extends BaseRecyclerViewAdapter {

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BaseViewHolder(inflateBinding(parent, R.layout.item_palette));
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            int res = mData[position];
            ItemPaletteBinding binding = (ItemPaletteBinding) holder.viewBinding;
            binding.imageView.setImageResource(res);

            int defaultColor = Color.BLUE;

            Palette palette = Palette.from(BitmapFactory.decodeResource(getResources(), res)).generate();
            binding.dominant.setBackgroundColor(palette.getDominantColor(defaultColor));
            binding.vibrant.setBackgroundColor(palette.getVibrantColor(defaultColor));
            binding.vibrantDark.setBackgroundColor(palette.getDarkVibrantColor(defaultColor));
            binding.vibrantLight.setBackgroundColor(palette.getLightVibrantColor(defaultColor));
            binding.muted.setBackgroundColor(palette.getMutedColor(defaultColor));
            binding.mutedDark.setBackgroundColor(palette.getDarkMutedColor(defaultColor));
            binding.mutedLight.setBackgroundColor(palette.getLightMutedColor(defaultColor));
        }

        @Override
        public int getItemCount() {
            return mData.length;
        }
    }

}
