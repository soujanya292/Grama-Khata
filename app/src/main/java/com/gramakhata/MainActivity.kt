package com.gramakhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.gramakhata.data.GramaKhataDatabase
import com.gramakhata.data.GramaKhataRepository
import com.gramakhata.ui.navigation.Screen
import com.gramakhata.ui.screens.AddEditCustomerScreen
import com.gramakhata.ui.screens.DashboardScreen
import com.gramakhata.ui.screens.SettingsScreen
import com.gramakhata.ui.screens.TransactionScreen
import com.gramakhata.ui.viewmodel.CustomerViewModel
import com.gramakhata.ui.viewmodel.DashboardViewModel
import com.gramakhata.ui.viewmodel.TransactionViewModel
import com.gramakhata.util.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI for simplicity in this example
        val database = GramaKhataDatabase.getDatabase(this)
        val repository = GramaKhataRepository(database.customerDao(), database.transactionDao())
        val preferenceManager = PreferenceManager(this)

        setContent {
            MaterialTheme {
                GramaKhataApp(repository, preferenceManager)
            }
        }
    }
}

@Composable
fun GramaKhataApp(repository: GramaKhataRepository, preferenceManager: PreferenceManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = DashboardViewModel(repository) as T
            })
            DashboardScreen(
                viewModel = viewModel,
                onCustomerClick = { id -> navController.navigate(Screen.Transactions.createRoute(id)) },
                onAddCustomerClick = { navController.navigate(Screen.AddEditCustomer.createRoute(null)) },
                onEditCustomerClick = { id -> navController.navigate(Screen.AddEditCustomer.createRoute(id)) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onReportClick = { /* Show Daily Report logic - can be a Dialog */ }
            )
        }

        composable(
            route = Screen.Transactions.route,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            val viewModel: TransactionViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = TransactionViewModel(repository, customerId, preferenceManager) as T
            })
            TransactionScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.AddEditCustomer.route,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId")
            val viewModel: CustomerViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = CustomerViewModel(repository) as T
            })
            AddEditCustomerScreen(viewModel = viewModel, customerId = customerId, onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(preferenceManager = preferenceManager, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MaterialTheme(content: @Composable () -> Unit) {
    androidx.compose.material3.MaterialTheme(content = content)
}
