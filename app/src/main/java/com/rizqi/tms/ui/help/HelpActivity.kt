package com.rizqi.tms.ui.help

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnHelpBack.setOnClickListener { onBackPressed() }
            btnHelp.setOnClickListener {
                Intent(Intent.ACTION_SEND).apply {
                    data = Uri.parse("mailto:")
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.company_email)))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_email_subject))
                }.also { itn ->
                    try {
                        startActivity(Intent.createChooser(itn, "Send email..."))
                    } catch (e : ActivityNotFoundException){
                        Toast.makeText(this@HelpActivity, "There is no email client installed", Toast.LENGTH_SHORT).show()
                    } catch (_ : Exception){}
                }
            }
        }
    }
}