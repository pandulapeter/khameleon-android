package com.pandulapeter.khameleon.feature

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pandulapeter.khameleon.BR

abstract class KhameleonFragment<B : ViewDataBinding, out VM : KhameleonViewModel>(@LayoutRes private val layoutResourceId: Int) : Fragment() {
    protected abstract val viewModel: VM
    protected abstract val title: Int
    protected lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        binding.setVariable(BR.viewModel, viewModel)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { it.title = it.getString(title) }
    }
}