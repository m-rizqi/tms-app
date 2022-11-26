package com.rizqi.tms.model

import androidx.annotation.LayoutRes

abstract class ViewType<T>(
    @LayoutRes
    val layoutId : Int,
    val data : T?
)