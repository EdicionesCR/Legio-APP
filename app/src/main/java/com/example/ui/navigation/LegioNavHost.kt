package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.LegioBottomBar
import com.example.ui.screens.admin.*
import com.example.ui.screens.announcements.AnnouncementsScreen
import com.example.ui.screens.auth.AccessKeyScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.events.EventDetailScreen
import com.example.ui.screens.events.EventsScreen
import com.example.ui.screens.events.RegistrationModal
import com.example.ui.screens.home.BirthdaysListScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.library.DocumentDetailScreen
import com.example.ui.screens.library.LibraryScreen
import com.example.ui.screens.news.NewsDetailScreen
import com.example.ui.screens.news.NewsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.users.UserProfileScreen
import com.example.ui.screens.users.UsersScreen
import com.example.ui.screens.welcome.WelcomeScreen
import com.example.ui.viewmodel.LegioViewModel

@Composable
fun LegioNavHost(
    viewModel: LegioViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // DB state collection
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val visibleUsers by viewModel.visibleUsers.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val filteredUsers by viewModel.filteredUsers.collectAsStateWithLifecycle()
    val userSearchQuery by viewModel.userSearchQuery.collectAsStateWithLifecycle()
    val filterPriestsOnly by viewModel.filterPriestsOnly.collectAsStateWithLifecycle()

    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val nextEvent by viewModel.nextUpcomingEvent.collectAsStateWithLifecycle()

    val allNews by viewModel.allNews.collectAsStateWithLifecycle()

    val allDocuments by viewModel.allDocuments.collectAsStateWithLifecycle()
    val filteredDocuments by viewModel.filteredDocuments.collectAsStateWithLifecycle()
    val librarySearchQuery by viewModel.librarySearchQuery.collectAsStateWithLifecycle()
    val selectedLibraryCategory by viewModel.selectedLibraryCategory.collectAsStateWithLifecycle()

    val activeAnnouncements by viewModel.activeAnnouncements.collectAsStateWithLifecycle()
    val allAnnouncements by viewModel.allAnnouncements.collectAsStateWithLifecycle()

    val birthdays by viewModel.birthdays.collectAsStateWithLifecycle()
    val todayOrNextBirthday by viewModel.todayOrNextBirthday.collectAsStateWithLifecycle()

    // Determine if bottom bar should be visible
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.UsersList.route,
        Screen.Library.route,
        Screen.Events.route,
        Screen.NewsList.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                LegioBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Welcome Screen
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToAccessKey = { navController.navigate(Screen.AccessKey.route) },
                    onDirectEnter = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    users = allUsers,
                    onLoginSuccess = { userId ->
                        viewModel.switchUser(userId)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToAccessKey = { navController.navigate(Screen.AccessKey.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 3. Access Key Screen (Member Verification)
            composable(Screen.AccessKey.route) {
                AccessKeyScreen(
                    onKeyValidated = {
                        navController.navigate(Screen.Register.route)
                    },
                    onBackClick = { navController.popBackStack() },
                    onValidateKey = { viewModel.validateAccessKey(it) }
                )
            }

            // 4. Register / Create Profile Screen
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { fullName, birthDate, city, phone, email, memberType, ordinationDate, hideMyData, avatarUrl ->
                        viewModel.registerNewMember(
                            fullName = fullName,
                            birthDate = birthDate,
                            city = city,
                            phone = phone,
                            email = email,
                            memberType = memberType,
                            ordinationDate = ordinationDate,
                            hideMyData = hideMyData,
                            avatarUrl = avatarUrl,
                            onSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 5. Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    currentUser = currentUser,
                    todayOrNextBirthday = todayOrNextBirthday,
                    nextEvent = nextEvent,
                    latestNewsList = allNews,
                    activeAnnouncements = activeAnnouncements,
                    onNavigateToBirthdays = { navController.navigate(Screen.BirthdaysList.route) },
                    onNavigateToEventDetail = { id -> navController.navigate(Screen.EventDetail.createRoute(id)) },
                    onNavigateToNewsDetail = { id -> navController.navigate(Screen.NewsDetail.createRoute(id)) },
                    onNavigateToAnnouncements = { navController.navigate(Screen.Announcements.route) },
                    onNavigateToUsers = { navController.navigate(Screen.UsersList.route) },
                    onNavigateToLibrary = { navController.navigate(Screen.Library.route) },
                    onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                    onNavigateToNews = { navController.navigate(Screen.NewsList.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // Birthdays List Screen
            composable(Screen.BirthdaysList.route) {
                BirthdaysListScreen(
                    birthdays = birthdays,
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 6. Users Directory Screen
            composable(Screen.UsersList.route) {
                UsersScreen(
                    users = filteredUsers,
                    searchQuery = userSearchQuery,
                    onSearchQueryChange = { viewModel.userSearchQuery.value = it },
                    filterPriestsOnly = filterPriestsOnly,
                    onFilterPriestsChange = { viewModel.filterPriestsOnly.value = it },
                    onUserClick = { userId ->
                        navController.navigate(Screen.UserDetail.createRoute(userId))
                    },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // 7. User Profile Detail Screen
            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(navArgument("userId") { type = NavType.LongType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                val user = allUsers.firstOrNull { it.id == userId }
                UserProfileScreen(
                    user = user,
                    currentUser = currentUser,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 8. Library Screen
            composable(Screen.Library.route) {
                LibraryScreen(
                    documents = filteredDocuments,
                    categories = viewModel.libraryCategories,
                    selectedCategory = selectedLibraryCategory,
                    onCategorySelected = { viewModel.selectedLibraryCategory.value = it },
                    searchQuery = librarySearchQuery,
                    onSearchQueryChange = { viewModel.librarySearchQuery.value = it },
                    onDocumentClick = { docId ->
                        navController.navigate(Screen.DocumentDetail.createRoute(docId))
                    },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // 9. Document Detail Screen
            composable(
                route = Screen.DocumentDetail.route,
                arguments = listOf(navArgument("docId") { type = NavType.LongType })
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getLong("docId") ?: 0L
                val document = allDocuments.firstOrNull { it.id == docId }
                DocumentDetailScreen(
                    document = document,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 10. Events Screen
            composable(Screen.Events.route) {
                EventsScreen(
                    events = allEvents,
                    onEventClick = { eventId ->
                        navController.navigate(Screen.EventDetail.createRoute(eventId))
                    },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // 11. Event Detail Screen
            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
                val event = allEvents.firstOrNull { it.id == eventId }
                EventDetailScreen(
                    event = event,
                    onNavigateToRegistration = { id ->
                        navController.navigate(Screen.EventRegistration.createRoute(id))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 12. Event Registration Modal / Screen
            composable(
                route = Screen.EventRegistration.route,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
                val event = allEvents.firstOrNull { it.id == eventId }
                RegistrationModal(
                    event = event,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 13. News List Screen
            composable(Screen.NewsList.route) {
                NewsScreen(
                    newsList = allNews,
                    onNewsClick = { newsId ->
                        navController.navigate(Screen.NewsDetail.createRoute(newsId))
                    },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // 14. News Detail Screen
            composable(
                route = Screen.NewsDetail.route,
                arguments = listOf(navArgument("newsId") { type = NavType.LongType })
            ) { backStackEntry ->
                val newsId = backStackEntry.arguments?.getLong("newsId") ?: 0L
                val news = allNews.firstOrNull { it.id == newsId }
                NewsDetailScreen(
                    news = news,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 15. Announcements Screen
            composable(Screen.Announcements.route) {
                AnnouncementsScreen(
                    announcements = activeAnnouncements,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 16. Profile & Settings Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    currentUser = currentUser,
                    onUpdateProfile = { updated -> viewModel.updateUserProfile(updated) },
                    onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                    onLogout = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 17. Admin Dashboard Screen
            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    userCount = allUsers.size,
                    eventCount = allEvents.size,
                    newsCount = allNews.size,
                    documentCount = allDocuments.size,
                    announcementCount = allAnnouncements.size,
                    onNavigateToManageNews = { navController.navigate(Screen.AdminNews.route) },
                    onNavigateToManageEvents = { navController.navigate(Screen.AdminEvents.route) },
                    onNavigateToManageLibrary = { navController.navigate(Screen.AdminLibrary.route) },
                    onNavigateToManageUsers = { navController.navigate(Screen.AdminUsers.route) },
                    onNavigateToManageAnnouncements = { navController.navigate(Screen.AdminAnnouncements.route) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 18. Admin News Screen
            composable(Screen.AdminNews.route) {
                AdminNewsScreen(
                    newsList = allNews,
                    onSaveNews = { viewModel.saveNews(it) },
                    onDeleteNews = { viewModel.deleteNews(it) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 19. Admin Events Screen
            composable(Screen.AdminEvents.route) {
                AdminEventsScreen(
                    events = allEvents,
                    onSaveEvent = { viewModel.saveEvent(it) },
                    onDeleteEvent = { viewModel.deleteEvent(it) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 20. Admin Library Screen
            composable(Screen.AdminLibrary.route) {
                AdminLibraryScreen(
                    documents = allDocuments,
                    categories = viewModel.libraryCategories,
                    onSaveDocument = { viewModel.saveDocument(it) },
                    onDeleteDocument = { viewModel.deleteDocument(it) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 21. Admin Users Screen
            composable(Screen.AdminUsers.route) {
                AdminUsersScreen(
                    users = allUsers,
                    onUpdateUser = { viewModel.updateUserProfile(it) },
                    onDeleteUser = { viewModel.deleteUser(it) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 22. Admin Announcements Screen
            composable(Screen.AdminAnnouncements.route) {
                AdminAnnouncementsScreen(
                    announcements = allAnnouncements,
                    onSaveAnnouncement = { viewModel.saveAnnouncement(it) },
                    onDeleteAnnouncement = { viewModel.deleteAnnouncement(it) },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
