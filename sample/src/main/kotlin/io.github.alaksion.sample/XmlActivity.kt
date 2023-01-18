package io.github.alaksion.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.alaksion.sample.databinding.BindingActivityBinding


class XmlActivity : AppCompatActivity() {

    private lateinit var binding: BindingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BindingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}