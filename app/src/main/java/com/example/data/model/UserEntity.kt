package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MemberType(val label: String) {
    NONE("Miembro"),
    PRIEST("Sacerdote"),
    BROTHER("Hermano"),
    CONSECRATED_LAITY("Laico/a Consagrado/a")
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val avatarUrl: String = "",
    val birthDate: String, // Format: DD-MM-AAAA e.g. "04-12-1999"
    val city: String,
    val phone: String,
    val email: String,
    val memberType: String = MemberType.NONE.name, // PRIEST, BROTHER, CONSECRATED_LAITY, NONE
    val ordinationDate: String? = null, // e.g. "28-10-2012" (only for priests)
    val hideMyData: Boolean = false, // If true, non-admins cannot see phone or email
    val password: String = "22-Rosario-02931",
    val isAdmin: Boolean = false,
    val isCurrentUser: Boolean = false
) {
    val isPriest: Boolean get() = memberType == MemberType.PRIEST.name
    val isBrother: Boolean get() = memberType == MemberType.BROTHER.name
    val isConsecrated: Boolean get() = memberType == MemberType.CONSECRATED_LAITY.name

    val canAccessThesaurus: Boolean get() = isPriest || isBrother

    val memberTypeDisplay: String
        get() = when (memberType) {
            MemberType.PRIEST.name -> "Sacerdote LCR"
            MemberType.BROTHER.name -> "Hermano LCR"
            MemberType.CONSECRATED_LAITY.name -> "Laico/a Consagrado/a"
            else -> if (isAdmin) "Administrador" else "Miembro"
        }
}
