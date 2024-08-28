/*
 * This file is part of Doodle Android.
 *
 * Doodle Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Doodle Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Doodle Android. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2019-2023 by Patrick Zedler
 */

package com.dafay.demo.biz.settings.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.graphics.ColorUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ResUtil {

  private static final String TAG = ResUtil.class.getSimpleName();

  @NonNull
  public static String getRawText(Context context, @RawRes int resId) {
    InputStream inputStream = context.getResources().openRawResource(resId);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder text = new StringBuilder();
    try {
      for (String line; (line = bufferedReader.readLine()) != null; ) {
        text.append(line).append('\n');
      }
      text.deleteCharAt(text.length() - 1);
      inputStream.close();
    } catch (Exception e) {
      Log.e(TAG, "getRawText", e);
    }
    return text.toString();
  }

  public static void share(Context context, @StringRes int resId) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.putExtra(Intent.EXTRA_TEXT, context.getString(resId));
    intent.setType("text/plain");
    context.startActivity(Intent.createChooser(intent, null));
  }

  public static int getColorAttr(Context context, @AttrRes int resId) {
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(resId, typedValue, true);
    return typedValue.data;
  }

  public static int getColorAttr(Context context, @AttrRes int resId, float alpha) {
    return ColorUtils.setAlphaComponent(getColorAttr(context, resId), (int) (alpha * 255));
  }

  public static int getColorBg(Context context) {
    return getColorAttr(context, android.R.attr.colorBackground);
  }

  public static int getColorOutline(Context context) {
    return getColorAttr(context, com.google.android.material.R.attr.colorOutline);
  }

  public static int getColorOutlineSecondary(Context context) {
    return getColorAttr(context, com.google.android.material.R.attr.colorOutline, 0.4f);
  }

  public static int getColorHighlight(Context context) {
    return getColorAttr(context, com.google.android.material.R.attr.colorSecondary, 0.09f);
  }

  public static void tintMenuItemIcon(Context context, MenuItem item) {
    if (item != null) {
      tintIcon(context, item.getIcon());
    }
  }

  public static void tintIcon(Context context, Drawable icon) {
    if (icon != null) {
      icon.setTint(ResUtil.getColorAttr(context,com.google.android.material.R.attr.colorOnSurfaceVariant));
    }
  }
}
