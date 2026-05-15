package com.gramakhata.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddEditCustomer : Screen("add_edit_customer?customerId={customerId}") {
        fun createRoute(customerId: String?) = "add_edit_customer?customerId=$customerId"
    }
    object Transactions : Screen("transactions/{customerId}") {
        fun createRoute(customerId: String) = "transactions/$customerId"
    }
    object Settings : Screen("settings")
}
