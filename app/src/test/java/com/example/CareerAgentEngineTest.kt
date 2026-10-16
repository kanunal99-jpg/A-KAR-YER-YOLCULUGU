package com.example

import com.example.data.model.InterviewMode
import com.example.domain.CareerAgentEngine
import com.example.domain.SampleData
import org.junit.Assert.*
import org.junit.Test

class CareerAgentEngineTest {

    @Test
    fun testParseCvText_extractsOnlySourceFacts() {
        val rawCv = """
            Ahmet Demir
            Pozisyon: Saha Satış Şefi
            ahmet.demir@firma.com | 0532 999 88 77 | İstanbul
            Sütaş Süt Ürünleri | Saha Satış Şefi | 2021-2024
            Başarı: Dağıtım maliyetlerini %14 düşürdüm.
        """.trimIndent()

        val profile = CareerAgentEngine.parseCvText(rawCv)
        assertEquals("Ahmet Demir", profile.fullName)
        assertEquals("ahmet.demir@firma.com", profile.email)
        assertEquals("İstanbul", profile.locationCity)
        assertEquals(1, profile.experiences.size)
        assertEquals("Sütaş Süt Ürünleri", profile.experiences.single().company)
        assertTrue(profile.achievements.any { it.title.contains("%14") })
        assertEquals(0, profile.profileScore)
    }

    @Test
    fun testLegacyParserNeverInventsCompaniesOrAchievements() {
        val profile = CareerAgentEngine.parseCvText(
            "Ali Veli\nPozisyon: Satış Yöneticisi\nSatış ve Excel bilgisi"
        )
        assertEquals("", profile.email)
        assertEquals("", profile.phone)
        assertEquals("", profile.locationCity)
        assertTrue(profile.experiences.isEmpty())
        assertTrue(profile.achievements.isEmpty())
        assertTrue(profile.education.isEmpty())
        assertTrue(profile.certifications.isEmpty())
    }

    @Test
    fun testJobMatching_evaluatesFitAndGaps() {
        val profile = SampleData.sampleInitialProfile
        val job = SampleData.sampleJobs.first()

        val match = CareerAgentEngine.evaluateJobMatch(profile, job)

        assertTrue(match.overallMatchPercent in 70..100)
        assertTrue(match.whyYouAreFit.isNotEmpty())
        assertTrue(match.whyYouAreNotFit.isNotEmpty())
        assertTrue(match.missingGapsToClose.isNotEmpty())
        assertTrue(match.aiInsight.isNotBlank())
    }

    @Test
    fun testJobMatching_skillPercentIsNotArtificiallyInflated() {
        val profile = SampleData.sampleInitialProfile.copy(skills = emptyList())
        val job = SampleData.sampleJobs.first().copy(
            requiredSkills = listOf("Özel Sertifika A", "Özel Sertifika B")
        )

        val noSkills = CareerAgentEngine.evaluateJobMatch(profile, job)
        assertEquals(0, noSkills.skillsMatchPercent)

        val oneSkill = profile.copy(
            skills = listOf(com.example.data.model.SkillItem(name = "Özel Sertifika A", category = "Teknik"))
        )
        assertEquals(50, CareerAgentEngine.evaluateJobMatch(oneSkill, job).skillsMatchPercent)

        val bothSkills = oneSkill.copy(
            skills = oneSkill.skills + com.example.data.model.SkillItem(name = "Özel Sertifika B", category = "Teknik")
        )
        assertEquals(100, CareerAgentEngine.evaluateJobMatch(bothSkills, job).skillsMatchPercent)
    }

    @Test
    fun testMatchExplanation_doesNotInventCandidateHistory() {
        val profile = com.example.data.model.MasterCareerProfile(
            fullName = "Deniz Örnek",
            skills = emptyList(),
            experiences = emptyList(),
            achievements = emptyList(),
            languages = emptyList()
        )
        val job = SampleData.sampleJobs.first().copy(
            requiredSkills = listOf("SAP SD"),
            preferredSkills = listOf("İngilizce")
        )
        val match = CareerAgentEngine.evaluateJobMatch(profile, job)
        val explanations = (
            match.whyYouAreFit + match.whyYouAreNotFit +
                match.missingGapsToClose + listOf(match.aiInsight)
        ).joinToString(" ")

        assertTrue(match.whyYouAreFit.isEmpty())
        assertTrue(match.whyYouAreNotFit.any { it.contains("SAP SD") })
        assertFalse(explanations.contains("B1 (Orta)"))
        assertFalse(explanations.contains("18 kişilik"))
        assertFalse(explanations.contains("%112"))
        assertFalse(explanations.contains("7+ yıllık"))
        assertFalse(explanations.contains("Sütaş"))
        assertFalse(explanations.contains("Peyman"))
    }

    @Test
    fun testInterviewQuestion_doesNotInventAchievement() {
        val profile = com.example.data.model.MasterCareerProfile(fullName = "Deniz Örnek")
        val job = SampleData.sampleJobs.first()
        val session = CareerAgentEngine.generateInterviewSession(
            job, profile, InterviewMode.JOB_SPECIFIC
        )
        val questions = session.questions.joinToString(" ") { it.questionText }
        assertFalse(questions.contains("Sütaş"))
        assertFalse(questions.contains("%14"))
        assertTrue(questions.contains("ölçülebilir bir başarı"))
    }

    @Test
    fun testTailoredCvAndCoverLetter_generatedWithoutHallucination() {
        val profile = SampleData.sampleInitialProfile
        val job = SampleData.sampleJobs.first()

        val tailored = CareerAgentEngine.generateTailoredCv(profile, job)

        assertTrue(tailored.title.contains(job.company))
        assertTrue(tailored.targetRoleOrJob.contains(job.title))
        assertTrue(tailored.coverLetterText.contains(job.company))
        assertEquals(CareerAgentEngine.evaluateJobMatch(profile, job).overallMatchPercent, tailored.atsScoreEstimated)
    }

    @Test
    fun testTailoredCv_neverInventsExperienceForEmptyCandidateProfile() {
        val profile = com.example.data.model.MasterCareerProfile(
            fullName = "Deniz Örnek",
            email = "deniz@example.com"
        )
        val job = SampleData.sampleJobs.first()

        val tailored = CareerAgentEngine.generateTailoredCv(profile, job)

        assertTrue(tailored.coverLetterText.contains("Deniz Örnek"))
        assertTrue(tailored.coverLetterText.contains(job.company))
        assertFalse(tailored.coverLetterText.contains("Sütaş"))
        assertFalse(tailored.coverLetterText.contains("Peyman"))
        assertFalse(tailored.coverLetterText.contains("%14"))
        assertFalse(tailored.coverLetterText.contains("7 yılı"))
        assertFalse(tailored.professionalSummary.contains("60+"))
        assertTrue(tailored.keyHighlightedSkills.isEmpty())
    }

    @Test
    fun testPresetCv_usesOnlyCandidateEvidence() {
        val emptyProfile = com.example.data.model.MasterCareerProfile(fullName = "Deniz Örnek")
        for (type in listOf("management", "operations", "sales")) {
            val result = CareerAgentEngine.generatePresetCvVersion(emptyProfile, type)
            assertFalse(result.professionalSummary.contains("Sütaş"))
            assertFalse(result.professionalSummary.contains("Peyman"))
            assertFalse(result.professionalSummary.contains("18 kişilik"))
            assertFalse(result.professionalSummary.contains("60+"))
            assertFalse(result.professionalSummary.contains("%110"))
            assertTrue(result.keyHighlightedSkills.isEmpty())
            assertEquals(0, result.atsScoreEstimated)
        }
    }

    @Test
    fun testInterviewFlowAndDiscoveredExperience_integrationWithCv() {
        val profile = SampleData.sampleInitialProfile
        val job = SampleData.sampleJobs.first()

        val session = CareerAgentEngine.generateInterviewSession(job, profile, InterviewMode.JOB_SPECIFIC)
        assertTrue(session.questions.isNotEmpty())

        val answer = "Sütaş'ta 18 kişilik ekibimle rut optimizasyonu yaparak bölge dağıtım maliyetlerini %14 düşürdük ve 85 yeni bayi noktası açtık."
        val updated = CareerAgentEngine.evaluateAnswer(session, 0, answer)

        val q1 = updated.questions.first()
        assertTrue(q1.isAnswered)
        assertTrue(q1.score >= 80)
        assertTrue(q1.feedback.isNotBlank())
        // Should detect newly mentioned accomplishment for CV integration:
        assertTrue(updated.discoveredExperiences.isNotEmpty())
    }

    @Test
    fun testCoachEngine_handlesSimulationAndJobNotFound() {
        val profile = SampleData.sampleInitialProfile

        val simResponse = CareerAgentEngine.answerCoachQuery(profile, "3 yıl sonra nerede olabilirim?")
        assertTrue(simResponse.contains("3 Yıllık Kariyer Simülasyonu"))

        val filterResponse = CareerAgentEngine.answerCoachQuery(profile, "Bana uygun iş bulamadım")
        assertTrue(filterResponse.contains("İş Bulamadım"))
    }
}
