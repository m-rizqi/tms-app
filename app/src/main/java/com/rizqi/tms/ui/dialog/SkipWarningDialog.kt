package com.rizqi.tms.ui.dialog

import com.rizqi.tms.R

class SkipWarningDialog(
    onPositiveClickListener : () -> Unit
) : WarningDialog(
    onPositiveClickListener,
    R.string.data_wont_saved,
    R.string.skip_alert_explanation
)