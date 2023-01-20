package io.github.alaksion.sample.xml

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.alaksion.UiStateType
import io.github.alaksion.sample.SampleState
import io.github.alaksion.sample.StateViewModel
import io.github.alaksion.sample.databinding.BindingActivityBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class XmlActivity : AppCompatActivity() {

    private lateinit var binding: BindingActivityBinding
    private val viewModel by viewModels<StateViewModel>()

    private val viewAdapter by lazy {
        XmlActivityAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BindingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
    }

    private fun setUpViews() {
        setUpListeners()
        setupCollectors()
        setupRecycler()
    }

    private fun setupRecycler() {
        binding.rvItems.apply {
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(this@XmlActivity)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.left = 0
                    outRect.right = 0
                    outRect.bottom = 20
                }
            })
        }
    }

    private fun setUpListeners() {
        with(binding) {
            ibAddName.setOnClickListener {
                viewModel.submitName()
                itName.setText("")
            }

            itName.addTextChangedListener {
                viewModel.updateText(it.toString())
            }
        }
    }

    private fun setupCollectors() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { UiState ->
                    when (UiState.stateType) {
                        UiStateType.Loading -> setUpLoadingState()
                        UiStateType.Content -> setUpContentState(UiState.stateData)
                        is UiStateType.Error -> Unit
                    }
                }
            }
        }
    }

    private fun setUpContentState(state: SampleState) {
        binding.pbLoadingIndicator.visibility = View.GONE
        binding.clContentView.visibility = View.VISIBLE

        viewAdapter.updateItems(state.names)
    }

    private fun setUpLoadingState() {
        binding.pbLoadingIndicator.visibility = View.VISIBLE
        binding.clContentView.visibility = View.GONE
    }


}