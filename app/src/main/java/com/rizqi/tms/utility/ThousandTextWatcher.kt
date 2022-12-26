package com.rizqi.tms.utility

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ThousandTextWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        try {
            editText.removeTextChangedListener(this)
            val value = p0.toString()
            if (value.isEmpty()) {
                editText.removeTextChangedListener(this)
                editText.addTextChangedListener(this)
            }
            if (!value.isNullOrBlank()) {
                if (value.startsWith("0")) {
                    editText.setText("")
                }
                val str = ThousandFormatter.format(value)
                editText.setText(str)
                editText.setSelection(editText.text.toString().length)
                editText.addTextChangedListener(this)
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
            editText.addTextChangedListener(this)
        }
    }
}