package com.example

import com.example.domain.EvidenceBasedCvParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EvidenceBasedCvParserTest {
    @Test
    fun missingContactAndExperienceStayUnknown() {
        val profile = EvidenceBasedCvParser.parse(
            "Ali Veli\nPozisyon: Satış Uzmanı\nSatış ve Excel bilgisi"
        )

        assertEquals("Ali Veli", profile.fullName)
        assertEquals("", profile.email)
        assertEquals("", profile.phone)
        assertEquals("", profile.locationCity)
        assertTrue(profile.experiences.isEmpty())
        assertTrue(profile.achievements.isEmpty())
        assertTrue(profile.education.isEmpty())
        assertTrue(profile.certifications.isEmpty())
        assertTrue(profile.profileScore == 0)
    }

    @Test
    fun explicitExperienceIsParsedWithoutFabricatedMetrics() {
        val profile = EvidenceBasedCvParser.parse(
            "Ayşe Yılmaz\n\nABC A.Ş. | Satış Uzmanı | 2021-2024\nExcel, CRM"
        )

        assertEquals(1, profile.experiences.size)
        assertEquals("ABC A.Ş.", profile.experiences.first().company)
        assertEquals("Satış Uzmanı", profile.experiences.first().position)
        assertEquals("2021", profile.experiences.first().startYear)
        assertEquals("2024", profile.experiences.first().endYear)
        assertTrue(profile.experiences.first().achievements.isEmpty())
        assertTrue(profile.experiences.first().teamSize == 0)
    }
}