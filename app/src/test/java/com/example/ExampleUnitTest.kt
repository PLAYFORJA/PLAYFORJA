package com.example

import com.example.data.local.SecurityUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun newPasswordHashesAreSaltedAndVerifiable() {
        val first = SecurityUtils.createStoredPassword("correct horse battery staple")
        val second = SecurityUtils.createStoredPassword("correct horse battery staple")

        assertNotEquals(first, second)
        assertTrue(SecurityUtils.verifyPassword("correct horse battery staple", first))
        assertFalse(SecurityUtils.verifyPassword("incorrect password", first))
        assertFalse(SecurityUtils.needsRehash(first))
    }

    @Test
    fun legacyPasswordHashCanBeVerifiedAndMarkedForUpgrade() {
        val legacySalt = "00112233445566778899aabbccddeeff"
        val legacyHash = SecurityUtils.hashPassword("playforja123", legacySalt)
        val legacyStoredValue = "$legacySalt:$legacyHash"

        assertTrue(SecurityUtils.verifyPassword("playforja123", legacyStoredValue))
        assertTrue(SecurityUtils.needsRehash(legacyStoredValue))
        assertFalse(SecurityUtils.verifyPassword("wrong", legacyStoredValue))
    }

    @Test
    fun malformedPasswordHashIsRejected() {
        assertFalse(SecurityUtils.verifyPassword("anything", "not-a-valid-hash"))
        assertFalse(SecurityUtils.verifyPassword("anything", "pbkdf2_sha256:0:zz:xx"))
    }
}
