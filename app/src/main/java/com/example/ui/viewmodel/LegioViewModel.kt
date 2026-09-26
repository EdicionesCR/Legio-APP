package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LegioDatabase
import com.example.data.model.*
import com.example.data.repository.BirthdayItem
import com.example.data.repository.LegioRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LegioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LegioRepository

    init {
        val database = LegioDatabase.getDatabase(application, viewModelScope)
        repository = LegioRepository(database.legioDao())

        viewModelScope.launch {
            try {
                val currentList = repository.allUsers.first()
                val fotoscrUser = currentList.find { it.email.equals("fotoscr@legioncristorey.com.ar", ignoreCase = true) }
                if (fotoscrUser == null) {
                    val id = repository.insertUser(
                        UserEntity(
                            fullName = "Comunidad Fotos CR",
                            avatarUrl = "",
                            birthDate = "22-05-1995",
                            city = "Rosario, Santa Fe",
                            phone = "+54 9 341 555-0293",
                            email = "fotoscr@legioncristorey.com.ar",
                            memberType = MemberType.BROTHER.name,
                            ordinationDate = null,
                            hideMyData = false,
                            password = "22-Rosario-02931",
                            isAdmin = true,
                            isCurrentUser = true
                        )
                    )
                    repository.setCurrentUser(id)
                }
            } catch (_: Exception) {}
        }
    }

    // Flows from DB
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visibleUsers: StateFlow<List<UserEntity>> = repository.visibleUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allEvents: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNews: StateFlow<List<NewsEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.activeAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Birthdays
    val birthdays: StateFlow<List<BirthdayItem>> = allUsers.map { users ->
        repository.calculateBirthdays(users)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayOrNextBirthday: StateFlow<BirthdayItem?> = birthdays.map { list ->
        list.firstOrNull { it.isToday } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val nextUpcomingEvent: StateFlow<EventEntity?> = allEvents.map { list ->
        list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Search and filter states
    val userSearchQuery = MutableStateFlow("")
    val filterPriestsOnly = MutableStateFlow(false)

    val filteredUsers: StateFlow<List<UserEntity>> = combine(
        visibleUsers,
        userSearchQuery,
        filterPriestsOnly
    ) { users, query, priestsOnly ->
        users.filter { user ->
            val matchesQuery = query.isBlank() ||
                    user.fullName.contains(query, ignoreCase = true) ||
                    user.city.contains(query, ignoreCase = true)
            val matchesPriest = !priestsOnly || user.isPriest
            matchesQuery && matchesPriest
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Library filter states
    val librarySearchQuery = MutableStateFlow("")
    val selectedLibraryCategory = MutableStateFlow("Todas")

    val libraryCategories = listOf(
        "Todas",
        "Planes de formación",
        "Lecturas de profundización",
        "Documentación",
        "Liturgia de las Horas",
        "Cancioneros",
        "Documentos institucionales",
        "Otros"
    )

    val filteredDocuments: StateFlow<List<DocumentEntity>> = combine(
        allDocuments,
        librarySearchQuery,
        selectedLibraryCategory
    ) { docs, query, category ->
        docs.filter { doc ->
            val matchesCategory = category == "Todas" || doc.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    doc.title.contains(query, ignoreCase = true) ||
                    doc.description.contains(query, ignoreCase = true) ||
                    doc.keywords.contains(query, ignoreCase = true) ||
                    doc.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Access Key Validation
    // Institutional access key
    // Official requirement: vengatureino
    val validAccessKeys = setOf("vengatureino", "VENGATUREINO", "venga tu reino", "VENGA TU REINO", "CRISTOREY", "LEGIO")
    fun validateAccessKey(key: String): Boolean {
        val normalized = key.trim().lowercase().replace(" ", "")
        return normalized == "vengatureino" || validAccessKeys.any { it.trim().lowercase().replace(" ", "") == normalized }
    }

    // Auth & Profile Registration Flow
    fun registerNewMember(
        fullName: String,
        birthDate: String,
        city: String,
        phone: String,
        email: String,
        memberType: String = MemberType.NONE.name,
        ordinationDate: String? = null,
        hideMyData: Boolean = false,
        avatarUrl: String = "",
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val isPriest = memberType == MemberType.PRIEST.name
            val newUser = UserEntity(
                fullName = fullName,
                birthDate = birthDate,
                city = city,
                phone = phone,
                email = email,
                memberType = memberType,
                ordinationDate = if (isPriest) ordinationDate else null,
                hideMyData = hideMyData,
                avatarUrl = avatarUrl,
                isAdmin = false,
                isCurrentUser = true
            )
            val newId = repository.insertUser(newUser)
            repository.setCurrentUser(newId)
            onSuccess(newId)
        }
    }

    fun updateUserProfile(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun switchUser(userId: Long) {
        viewModelScope.launch {
            repository.setCurrentUser(userId)
        }
    }

    // Admin Operations
    fun saveNews(news: NewsEntity) {
        viewModelScope.launch {
            if (news.id == 0L) {
                repository.insertNews(news)
            } else {
                repository.updateNews(news)
            }
        }
    }

    fun deleteNews(news: NewsEntity) {
        viewModelScope.launch {
            repository.deleteNews(news)
        }
    }

    fun saveEvent(event: EventEntity) {
        viewModelScope.launch {
            if (event.id == 0L) {
                repository.insertEvent(event)
            } else {
                repository.updateEvent(event)
            }
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun saveDocument(document: DocumentEntity) {
        viewModelScope.launch {
            if (document.id == 0L) {
                repository.insertDocument(document)
            } else {
                repository.updateDocument(document)
            }
        }
    }

    fun deleteDocument(document: DocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(document)
        }
    }

    fun saveAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            if (announcement.id == 0L) {
                repository.insertAnnouncement(announcement)
            } else {
                repository.updateAnnouncement(announcement)
            }
        }
    }

    fun deleteAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }
}
