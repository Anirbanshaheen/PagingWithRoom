package com.example.pagingwithroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pagingwithroom.databinding.ActivityMainBinding
import com.example.pagingwithroom.paging.LoadAdapter
import com.example.pagingwithroom.paging.QuotePagingAdapter
import com.example.pagingwithroom.viewmodels.QuoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var quoteViewModel: QuoteViewModel
    private lateinit var adapter: QuotePagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quoteViewModel = ViewModelProvider(this).get(QuoteViewModel::class.java)
        adapter = QuotePagingAdapter()

        binding.quoteListRV.layoutManager = LinearLayoutManager(this)
        binding.quoteListRV.setHasFixedSize(true)
        binding.quoteListRV.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadAdapter(),
            footer = LoadAdapter()
        )

        quoteViewModel.list.observe(this, Observer {
            adapter.submitData(lifecycle, it)
        })
    }
}