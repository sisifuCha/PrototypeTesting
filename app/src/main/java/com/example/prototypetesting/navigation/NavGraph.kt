package com.example.prototypetesting.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import com.example.prototypetesting.ui.screens.AgentTestScreen
import com.example.prototypetesting.ui.screens.HomeScreen
import com.example.prototypetesting.ui.screens.ProfileScreen
import com.example.prototypetesting.ui.screens.ProjectDetailScreen
import com.example.prototypetesting.ui.screens.PrototypePreviewScreen
import com.example.prototypetesting.ui.screens.TestManagementScreen
import com.example.prototypetesting.ui.screens.TestReportScreen
import com.example.prototypetesting.ui.screens.TestRunnerScreen
import com.example.prototypetesting.ui.screens.UploadScreen
import com.example.prototypetesting.ui.screens.VideoUploadScreen
import com.example.prototypetesting.ui.screens.AiReportScreen
import com.example.prototypetesting.ui.screens.AgentSimulationScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.TestManagement.route) {
            TestManagementScreen(navController)
        }
        composable(Screen.AgentTest.route) {
            AgentTestScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.Upload.route) {
            UploadScreen(navController)
        }
        composable(Screen.VideoUpload.route) {
            VideoUploadScreen(navController)
        }
        composable(Screen.AiReport.route) {
            AiReportScreen(navController)
        }
        composable(
            route = "${Screen.AgentSimulation.route}/{personaType}",
            arguments = listOf(
                navArgument("personaType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val personaType = backStackEntry.arguments?.getString("personaType") ?: "elderly"
            AgentSimulationScreen(
                navController = navController,
                personaType = personaType
            )
        }
        composable(
            route = "${Screen.ProjectDetail.route}/{projectName}/{imageUris}",
            arguments = listOf(
                navArgument("projectName") { type = NavType.StringType },
                navArgument("imageUris") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedProjectName = backStackEntry.arguments?.getString("projectName") ?: ""
            val encodedImageUris = backStackEntry.arguments?.getString("imageUris") ?: ""
            
            val projectName = try {
                URLDecoder.decode(encodedProjectName, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedProjectName
            }
            
            val imageUrisString = try {
                URLDecoder.decode(encodedImageUris, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedImageUris
            }
            
            val imageUris = imageUrisString.split(",").filter { it.isNotEmpty() }
            ProjectDetailScreen(
                navController = navController,
                projectName = projectName,
                imageUris = imageUris
            )
        }
        composable(
            route = "${Screen.TestRunner.route}/{projectName}/{imageUris}/{testType}",
            arguments = listOf(
                navArgument("projectName") { type = NavType.StringType },
                navArgument("imageUris") { type = NavType.StringType },
                navArgument("testType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedProjectName = backStackEntry.arguments?.getString("projectName") ?: ""
            val encodedImageUris = backStackEntry.arguments?.getString("imageUris") ?: ""
            val testType = backStackEntry.arguments?.getString("testType") ?: "user"
            
            val projectName = try {
                URLDecoder.decode(encodedProjectName, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedProjectName
            }
            
            val imageUrisString = try {
                URLDecoder.decode(encodedImageUris, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedImageUris
            }
            
            val imageUris = imageUrisString.split(",").filter { it.isNotEmpty() }
            TestRunnerScreen(
                navController = navController,
                projectName = projectName,
                imageUris = imageUris,
                testType = testType
            )
        }
        composable(
            route = "${Screen.TestReport.route}/{projectName}",
            arguments = listOf(
                navArgument("projectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedProjectName = backStackEntry.arguments?.getString("projectName") ?: ""
            val projectName = try {
                URLDecoder.decode(encodedProjectName, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedProjectName
            }
            TestReportScreen(
                navController = navController,
                projectName = projectName
            )
        }

        // 原型预览页面 - "即画即测"交互
        composable(
            route = "${Screen.PrototypePreview.route}/{projectName}/{imageUris}/{initialPageIndex}",
            arguments = listOf(
                navArgument("projectName") { type = NavType.StringType },
                navArgument("imageUris") { type = NavType.StringType },
                navArgument("initialPageIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedProjectName = backStackEntry.arguments?.getString("projectName") ?: ""
            val encodedImageUris = backStackEntry.arguments?.getString("imageUris") ?: ""
            val initialPageIndex = backStackEntry.arguments?.getInt("initialPageIndex") ?: 0

            val projectName = try {
                URLDecoder.decode(encodedProjectName, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedProjectName
            }

            val imageUrisString = try {
                URLDecoder.decode(encodedImageUris, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                encodedImageUris
            }

            val imageUris = imageUrisString.split(",").filter { it.isNotEmpty() }
            PrototypePreviewScreen(
                navController = navController,
                projectName = projectName,
                imageUris = imageUris,
                initialPageIndex = initialPageIndex
            )
        }
    }
}
