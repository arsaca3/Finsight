package com.example.finsight20.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import com.example.finsight20.R
import com.example.finsight20.databinding.FragmentAyarlarBinding
import com.example.finsight20.ui.activity.loginActivity
import com.example.finsight20.util.PrefsManager



class ayarlarFragment : Fragment() {


    private lateinit var prefsManager: PrefsManager
    private lateinit var binding: FragmentAyarlarBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAyarlarBinding.inflate(inflater, container, false)

        prefsManager = PrefsManager(requireContext())

        binding.txtHesap.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.profilAyarGecis)
        }
        binding.arrow1.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.profilAyarGecis)
        }
        binding.txtbutce.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.butcegecis)
        }
        binding.butcearrow.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.butcegecis)
        }

        binding.txtchat.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.chatgecis)
        }
        binding.chararrow.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.chatgecis)
        }

        binding.switchNotify.isChecked = prefsManager.getBildirimTercihi()

        binding.switchNotify.setOnCheckedChangeListener { _, isChecked ->
            prefsManager.setBildirimTercihi(isChecked)

            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            2001
                        )
                    }
                }
            }
        }


        binding.txtCikis.setOnClickListener{
            val intent = Intent(requireContext(), loginActivity::class.java)
            val prefs = PrefsManager(requireContext())
            prefs.clear()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(context, "Çıkış Yapılıyor", Toast.LENGTH_SHORT).show()
        }
        binding.arrow2.setOnClickListener{
            val intent = Intent(requireContext(), loginActivity::class.java)
            val prefs = PrefsManager(requireContext())
            prefs.clear()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(context, "Çıkış Yapılıyor", Toast.LENGTH_SHORT).show()
        }
        binding.switchDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Bildirim izni verildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show()
                binding.switchNotify.isChecked = false
            }
        }
    }


}