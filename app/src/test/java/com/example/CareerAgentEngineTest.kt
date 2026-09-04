package com.example

import com.example.data.model.InterviewMode
import com.example.domain.CareerAgentEngine
import com.example.domain.SampleData
import org.junit.Assert.*
import org.junit.Test

class CareerAgentEngineTest {

    @Test
    fun testParseCvText_extractsProfileAndGeneratesQuestions() {
        val rawCv = """
            Ahmet Demir
            Saha Satış Şefi
            ahmet.demir@firma.com | 0532 999 88 77 | İstanbul
            Sütaş Süt Ürünleri'nde 60 bayi ve 18 kişilik saha ekibi yönettim.
            Ciro ve dağıtım maliyetlerini %14 düşürdüm.
        """.trimIndent()

        val profile = CareerAgentEngine.parseCvText(rawCv)

        assertEquals("Ahmet Demir", profile.fullName)
        assertEquals("ahmet.demir@firma.com", profile.email)
        assertEquals("İstanbul", profile.locationCity)
        assertTrue(profile.skills.isNotEmpty())
        assertTrue(profile.experiences.isNotEmpty())
        assertTrue(profile.pendingQuestions.isNotEmpty())
        assertTrue(profile.profileScore > 70)
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
    fun testTailoredCvAndCoverLetter_generatedWithoutHallucination() {
        val profile = SampleData.sampleInitialProfile
        val job = SampleData.sampleJobs.first()

        val tailored = CareerAgentEngine.generateTailoredCv(profile, job)

        assertTrue(tailored.title.contains(job.company))
        assertTrue(tailored.targetRoleOrJob.contains(job.title))
        assertTrue(tailored.coverLetterText.contains(job.company))
        assertTrue(tailored.atsScoreEstimated >= 85)
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
