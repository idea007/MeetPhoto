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

package com.example.demo.meetphoto.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.Settings.Global;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;

import com.example.demo.meetphoto.R;


public class UiUtil {

  public static final int SCRIM = 0x55000000;
  public static final int SCRIM_DARK_DIALOG = 0xFF0c0c0e;
  public static final int SCRIM_LIGHT_DIALOG = 0xFF666666;

  public static void layoutEdgeToEdge(Window window) {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
      window.setDecorFitsSystemWindows(false);
    } else {
      int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
      View decorView = window.getDecorView();
      decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | flags);
    }
  }

  public static void setLightNavigationBar(@NonNull View view, boolean isLight) {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
      view.getWindowInsetsController().setSystemBarsAppearance(
          isLight ? WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS : 0,
          WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
      );
    } else if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
      int flags = view.getSystemUiVisibility();
      if (isLight) {
        flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
      } else {
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
      }
      view.setSystemUiVisibility(flags);
    }
  }

  public static void setLightStatusBar(@NonNull View view, boolean isLight) {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
      view.getWindowInsetsController().setSystemBarsAppearance(
          isLight ? WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS : 0,
          WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
      );
    } else if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
      int flags = view.getSystemUiVisibility();
      if (isLight) {
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
      } else {
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
      }
      view.setSystemUiVisibility(flags);
    }
  }

  public static boolean isDarkModeActive(Context context) {
    int uiMode = context.getResources().getConfiguration().uiMode;
    return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
  }

  public static boolean isNavigationModeGesture(Context context) {
    final int NAV_GESTURE = 2;
    Resources resources = context.getResources();
    @SuppressLint("DiscouragedApi")
    int resourceId = resources.getIdentifier(
        "config_navBarInteractionMode", "integer", "android"
    );
    int mode = resourceId > 0 ? resources.getInteger(resourceId) : 0;
    return mode == NAV_GESTURE;
  }

  public static boolean isOrientationPortrait(Context context) {
    int orientation = context.getResources().getConfiguration().orientation;
    return orientation == Configuration.ORIENTATION_PORTRAIT;
  }

  public static boolean isLayoutRtl(Context context) {
    int direction = context.getResources().getConfiguration().getLayoutDirection();
    return direction == View.LAYOUT_DIRECTION_RTL;
  }

  public static boolean isFullWidth(Context context) {
    int maxWidth = context.getResources().getDimensionPixelSize(R.dimen.max_content_width);
    return maxWidth >= getDisplayWidth(context);
  }

  // Unit conversions

  public static int dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) float dp) {
    Resources r = context.getResources();
    return Math.round(dp * r.getDisplayMetrics().density);
  }

  public static int spToPx(@NonNull Context context, @Dimension(unit = Dimension.SP) float sp) {
    Resources r = context.getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
  }

  // Display metrics

  public static int getDisplayWidth(Context context) {
    return getDisplayMetrics(context, true);
  }

  public static int getDisplayHeight(Context context) {
    return getDisplayMetrics(context, false);
  }

  private static int getDisplayMetrics(Context context, boolean useWidth) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
      WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
      if (useWidth) {
        return windowMetrics.getBounds().width();
      } else {
        return windowMetrics.getBounds().height();
      }
    } else {
      DisplayMetrics displayMetrics = new DisplayMetrics();
      windowManager.getDefaultDisplay().getMetrics(displayMetrics);
      return useWidth ? displayMetrics.widthPixels : displayMetrics.heightPixels;
    }
  }

  // A11y animation reduction

  public static boolean areAnimationsEnabled(Context context) {
    boolean duration = Global.getFloat(
        context.getContentResolver(), Global.ANIMATOR_DURATION_SCALE, 0
    ) != 0;
    boolean transition = Global.getFloat(
        context.getContentResolver(), Global.TRANSITION_ANIMATION_SCALE, 0
    ) != 0;
    boolean window = Global.getFloat(
        context.getContentResolver(), Global.WINDOW_ANIMATION_SCALE, 0
    ) != 0;
    return duration && transition && window;
  }
}
