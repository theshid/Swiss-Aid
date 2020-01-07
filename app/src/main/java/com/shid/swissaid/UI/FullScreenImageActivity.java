package com.shid.swissaid.UI;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ortiz.touchview.TouchImageView;
import com.shid.swissaid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class FullScreenImageActivity extends BaseActivity {
    private com.ortiz.touchview.TouchImageView mImageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindViews() {
        progressDialog = new ProgressDialog(this);
        mImageView = findViewById(R.id.imageView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setValues() {
        String urlPhotoClick;
        urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
        Log.i("TAG", "imagem recebida " + urlPhotoClick);


        Glide.with(this).asBitmap().load(urlPhotoClick).override(640, 640).fitCenter().into(new
                                                                                                    CustomViewTarget<TouchImageView, Bitmap>(mImageView) {

                                                                                                        @Override
                                                                                                        protected void onResourceLoading(@Nullable Drawable placeholder) {
                                                                                                            progressDialog.setMessage(getString(R.string.loading));
                                                                                                            progressDialog.show();
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                                                                            Toast.makeText(FullScreenImageActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                                                                                                            progressDialog.dismiss();
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                                                            progressDialog.dismiss();
                                                                                                            mImageView.setImageBitmap(resource);
                                                                                                        }

                                                                                                        @Override
                                                                                                        protected void onResourceCleared(@Nullable Drawable placeholder) {

                                                                                                        }
                                                                                                    });
    }

}
