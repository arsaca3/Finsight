package com.example.finsight20.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.finsight20.R
import com.example.finsight20.databinding.FragmentVeriBinding

class veriFragment : Fragment() {

    private lateinit var binding: FragmentVeriBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVeriBinding.inflate(inflater, container, false)

        binding.gelir.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.gelirGecis)
        }

        binding.gider.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.giderGecis)
        }

        return binding.root
    }

}