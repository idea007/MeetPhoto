/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.meetphoto.ui.page.home.view;

import static com.example.demo.meetphoto.data.model.ConstantsKt.CROSS_FADE_DURATION;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.demo.meetphoto.R;
import com.example.demo.meetphoto.data.model.Photo;

/**
 * An {@link RecyclerView.ViewHolder} that displays an item inside a Carousel.
 */
class CarouselItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final CarouselItemListener listener;

    CarouselItemViewHolder(@NonNull View itemView, CarouselItemListener listener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.carousel_image_view);
        this.listener = listener;
    }

    void bind(int position, Photo photo) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(Target.SIZE_ORIGINAL)
                .dontTransform();

        Glide.with(imageView.getContext())
                .load(photo.getUrls().getSmall())
                .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
                .apply(options)
                .placeholder(new ColorDrawable(Color.parseColor(photo.getColor())))
                .into(imageView);
        itemView.setOnClickListener(v -> listener.onItemClicked(v, photo, position));
    }

}
