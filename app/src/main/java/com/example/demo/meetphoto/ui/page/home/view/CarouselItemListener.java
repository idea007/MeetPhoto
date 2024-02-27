package com.example.demo.meetphoto.ui.page.home.view;

import android.view.View;

import com.example.demo.meetphoto.data.model.Photo;

public interface CarouselItemListener {
    void onItemClicked(View view, Photo item, int position);
}
