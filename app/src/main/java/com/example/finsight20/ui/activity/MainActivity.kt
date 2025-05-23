package com.example.finsight20.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.finsight20.R
import com.example.finsight20.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttom.setOnItemSelectedListener { menuItem ->
            val navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

            // Önce zaten bu destination'da mıyız kontrol et
            if (menuItem.itemId != navController.currentDestination?.id) {
                navController.popBackStack(navController.graph.startDestinationId, false)
                navController.navigate(menuItem.itemId)
            }

            true
        }

        val navController = (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.buttom.menu.findItem(destination.id)?.isChecked = true
        }
        //val navHostFragment1 = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        //NavigationUI.setupWithNavController(binding.buttom,navHostFragment1.navController)
    }
}