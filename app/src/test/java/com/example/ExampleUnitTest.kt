package com.example

import com.example.data.model.MemberType
import com.example.data.model.UserEntity
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAccessKeyValidation() {
        val validAccessKeys = setOf("vengatureino", "VENGATUREINO", "venga tu reino", "VENGA TU REINO", "CRISTOREY", "LEGIO")
        fun validate(key: String): Boolean {
            val normalized = key.trim().lowercase().replace(" ", "")
            return normalized == "vengatureino" || validAccessKeys.any { it.trim().lowercase().replace(" ", "") == normalized }
        }

        assertTrue("vengatureino should be valid", validate("vengatureino"))
        assertTrue("VENGATUREINO uppercase should be valid", validate("VENGATUREINO"))
        assertTrue("venga tu reino with spaces should be valid", validate("venga tu reino"))
        assertFalse("wrong key should be rejected", validate("clave_invalida"))
    }

    @Test
    fun testMemberTypesAndThesaurusPermissions() {
        val priest = UserEntity(
            fullName = "P. Juan Manuel Álvarez",
            birthDate = "25-09-1980",
            city = "Buenos Aires",
            phone = "11223344",
            email = "juan@legio.org",
            memberType = MemberType.PRIEST.name
        )

        val brother = UserEntity(
            fullName = "Hno. Tomás Benítez",
            birthDate = "15-10-1994",
            city = "Mendoza",
            phone = "2604123344",
            email = "tomas@legio.org",
            memberType = MemberType.BROTHER.name
        )

        val consecrated = UserEntity(
            fullName = "María Eugenia Soler",
            birthDate = "08-12-1989",
            city = "Salta",
            phone = "3874119988",
            email = "eugenia@legio.org",
            memberType = MemberType.CONSECRATED_LAITY.name
        )

        val laity = UserEntity(
            fullName = "Santiago Rossi",
            birthDate = "20-01-1999",
            city = "Rosario",
            phone = "3415123344",
            email = "santiago@legio.org",
            memberType = MemberType.NONE.name
        )

        // Sacerdotes y Hermanos pueden acceder a Thesaurus
        assertTrue(priest.canAccessThesaurus)
        assertTrue(brother.canAccessThesaurus)

        // Laico/a consagrado/a y Laicos NO pueden acceder a Thesaurus
        assertFalse(consecrated.canAccessThesaurus)
        assertFalse(laity.canAccessThesaurus)

        // Exclusividad de tipos
        assertTrue(priest.isPriest)
        assertFalse(priest.isBrother)
        assertFalse(priest.isConsecrated)

        assertTrue(brother.isBrother)
        assertFalse(brother.isPriest)
        assertFalse(brother.isConsecrated)

        assertTrue(consecrated.isConsecrated)
        assertFalse(consecrated.isPriest)
        assertFalse(consecrated.isBrother)
    }

    @Test
    fun testPrivacyRules() {
        val userWithHiddenData = UserEntity(
            id = 10L,
            fullName = "María Eugenia Soler",
            birthDate = "08-12-1989",
            city = "Salta",
            phone = "3874119988",
            email = "eugenia@legio.org",
            hideMyData = true
        )

        val adminUser = UserEntity(
            id = 20L,
            fullName = "Gonzalo Admin",
            birthDate = "04-11-1991",
            city = "CABA",
            phone = "1199887766",
            email = "admin@legio.org",
            isAdmin = true
        )

        val regularMember = UserEntity(
            id = 30L,
            fullName = "Santiago Rossi",
            birthDate = "20-01-1999",
            city = "Rosario",
            phone = "3415123344",
            email = "santiago@legio.org",
            isAdmin = false
        )

        // Logic check: Can viewer see private contact?
        fun canViewerSeeContact(viewer: UserEntity, target: UserEntity): Boolean {
            return !target.hideMyData || viewer.isAdmin || (target.id != 0L && viewer.id == target.id)
        }

        // Regular member cannot see hidden contact
        assertFalse(canViewerSeeContact(regularMember, userWithHiddenData))

        // Admin CAN see hidden contact
        assertTrue(canViewerSeeContact(adminUser, userWithHiddenData))

        // The user themselves CAN see their own contact
        assertTrue(canViewerSeeContact(userWithHiddenData, userWithHiddenData))
    }
}
