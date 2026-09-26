package com.example.ui.navigation

sealed class Screen(val route: String) {
    // Auth & Onboarding
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object AccessKey : Screen("access_key")
    data object CreateProfile : Screen("create_profile")

    // Main Sections
    data object Home : Screen("home")
    data object BirthdaysList : Screen("birthdays_list")
    
    data object UsersList : Screen("users_list")
    data object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: Long) = "user_detail/$userId"
    }

    data object Library : Screen("library")
    data object LibraryCategories : Screen("library_categories")
    data object LibrarySearch : Screen("library_search")
    data object DocumentDetail : Screen("document_detail/{docId}") {
        fun createRoute(docId: Long) = "document_detail/$docId"
    }

    data object Events : Screen("events")
    data object EventsList : Screen("events_list")
    data object EventsCalendar : Screen("events_calendar")
    data object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: Long) = "event_detail/$eventId"
    }
    data object EventRegistration : Screen("event_registration/{eventId}") {
        fun createRoute(eventId: Long) = "event_registration/$eventId"
    }

    data object NewsList : Screen("news_list")
    data object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: Long) = "news_detail/$newsId"
    }

    data object Announcements : Screen("announcements")
    data object Profile : Screen("profile")

    // Administration
    data object AdminDashboard : Screen("admin_dashboard")
    data object AdminNews : Screen("admin_news")
    data object AdminEvents : Screen("admin_events")
    data object AdminLibrary : Screen("admin_library")
    data object AdminUsers : Screen("admin_users")
    data object AdminAnnouncements : Screen("admin_announcements")
}
