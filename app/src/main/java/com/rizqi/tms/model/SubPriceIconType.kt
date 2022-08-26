package com.rizqi.tms.model

import androidx.annotation.DrawableRes
import com.rizqi.tms.R

sealed class SubPriceIconType(@DrawableRes val iconRes : Int) {
    class Merchant : SubPriceIconType(R.drawable.ic_p)
    class Consumer : SubPriceIconType(R.drawable.ic_e)
}