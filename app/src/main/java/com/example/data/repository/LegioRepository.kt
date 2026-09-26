package com.example.data.repository

import com.example.data.dao.LegioDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

data class BirthdayItem(
    val user: UserEntity,
    val formattedBirthday: String, // e.g. "25 de Septiembre"
    val daysUntil: Int,
    val isToday: Boolean
)

class LegioRepository(private val dao: LegioDao) {

    // Users
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val visibleUsers: Flow<List<UserEntity>> = dao.getVisibleUsers()
    val currentUserFlow: Flow<UserEntity?> = dao.getCurrentUserFlow()

    suspend fun getUserById(id: Long): UserEntity? = dao.getUserById(id)
    suspend fun getCurrentUser(): UserEntity? = dao.getCurrentUser()
    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun deleteUser(user: UserEntity) = dao.deleteUser(user)
    suspend fun setCurrentUser(id: Long) {
        dao.clearCurrentUserFlag()
        dao.setCurrentUserFlag(id)
    }

    // Events
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
    suspend fun getEventById(id: Long): EventEntity? = dao.getEventById(id)
    suspend fun insertEvent(event: EventEntity): Long = dao.insertEvent(event)
    suspend fun updateEvent(event: EventEntity) = dao.updateEvent(event)
    suspend fun deleteEvent(event: EventEntity) = dao.deleteEvent(event)

    // News
    val allNews: Flow<List<NewsEntity>> = dao.getAllNews()
    suspend fun getNewsById(id: Long): NewsEntity? = dao.getNewsById(id)
    suspend fun insertNews(news: NewsEntity): Long = dao.insertNews(news)
    suspend fun updateNews(news: NewsEntity) = dao.updateNews(news)
    suspend fun deleteNews(news: NewsEntity) = dao.deleteNews(news)

    // Documents (Biblioteca)
    val allDocuments: Flow<List<DocumentEntity>> = dao.getAllDocuments()
    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>> = dao.getDocumentsByCategory(category)
    suspend fun getDocumentById(id: Long): DocumentEntity? = dao.getDocumentById(id)
    suspend fun insertDocument(document: DocumentEntity): Long = dao.insertDocument(document)
    suspend fun updateDocument(document: DocumentEntity) = dao.updateDocument(document)
    suspend fun deleteDocument(document: DocumentEntity) = dao.deleteDocument(document)

    // Announcements
    val activeAnnouncements: Flow<List<AnnouncementEntity>> = dao.getActiveAnnouncements()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long = dao.insertAnnouncement(announcement)
    suspend fun updateAnnouncement(announcement: AnnouncementEntity) = dao.updateAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: AnnouncementEntity) = dao.deleteAnnouncement(announcement)

    // Birthday calculation helpers
    fun calculateBirthdays(users: List<UserEntity>): List<BirthdayItem> {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val todayMonth = today.get(Calendar.MONTH)
        val todayDay = today.get(Calendar.DAY_OF_MONTH)

        val monthFormat = SimpleDateFormat("d 'de' MMMM", Locale("es", "ES"))

        return users.mapNotNull { user ->
            try {
                // Supports DD-MM-AAAA (primary) and YYYY-MM-DD (legacy fallback)
                val cleanDate = user.birthDate.replace("/", "-")
                val parts = cleanDate.split("-")
                val bMonth: Int
                val bDay: Int
                if (parts.size >= 3) {
                    if (parts[0].length == 4) {
                        // YYYY-MM-DD
                        bMonth = parts[1].toInt() - 1
                        bDay = parts[2].toInt()
                    } else {
                        // DD-MM-AAAA
                        bDay = parts[0].toInt()
                        bMonth = parts[1].toInt() - 1
                    }
                } else if (parts.size == 2) {
                    bDay = parts[0].toInt()
                    bMonth = parts[1].toInt() - 1
                } else {
                    return@mapNotNull null
                }

                val bCal = Calendar.getInstance().apply {
                    set(Calendar.MONTH, bMonth)
                    set(Calendar.DAY_OF_MONTH, bDay)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val isToday = (bMonth == todayMonth && bDay == todayDay)

                // Calculate next occurrence
                var nextYear = currentYear
                val testCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.MONTH, bMonth)
                    set(Calendar.DAY_OF_MONTH, bDay)
                }

                // If birthday has already passed this year (and not today), next is next year
                if (testCal.before(today) && !isToday) {
                    nextYear++
                }

                val nextBirthdayCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, nextYear)
                    set(Calendar.MONTH, bMonth)
                    set(Calendar.DAY_OF_MONTH, bDay)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val diffMillis = nextBirthdayCal.timeInMillis - today.timeInMillis
                val daysUntil = if (isToday) 0 else ((diffMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0) + 1)

                BirthdayItem(
                    user = user,
                    formattedBirthday = monthFormat.format(bCal.time),
                    daysUntil = daysUntil,
                    isToday = isToday
                )
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.daysUntil }
    }
}
