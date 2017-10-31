package com.vipycm.mao.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentCaptureBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CaptureFragment
 * Created by mao on 2017/10/30.
 */
public class CaptureFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentCaptureBinding mDataBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_capture, container, false);
        mDataBinding.setHandler(this);
        mDataBinding.setShowVideo(true);
        return mDataBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.btn_image:
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.vipycm.mao.fileprovider",
                        createImageFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                getActivity().startActivityForResult(intent, 1);
                break;
            case R.id.btn_video:
                intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                getActivity().startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    public void onMaoActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
//            mDataBinding.imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mDataBinding.setShowVideo(false);
            mDataBinding.imageView.setImageBitmap(bitmap);
        } else if (requestCode == 2) {
            mDataBinding.setShowVideo(true);
            mDataBinding.videoView.setVideoURI(data.getData());
            mDataBinding.videoView.start();
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            mCurrentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
}
