package com.example.data.model

data class InterviewSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val jobId: String = "",
    val jobTitle: String = "Genel Kariyer Simülasyonu",
    val company: String = "Hedef Şirket",
    val mode: InterviewMode = InterviewMode.JOB_SPECIFIC,
    val questions: List<InterviewQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val isCompleted: Boolean = false,
    val report: InterviewReport? = null,
    val discoveredExperiences: List<DiscoveredExperience> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class InterviewMode(val titleTr: String) {
    JOB_SPECIFIC("İlana Özel Mülakat"),
    TECHNICAL("Teknik Yetkinlik Mülakatı"),
    BEHAVIORAL("Davranışsal & STAR"),
    CASE_STUDY("Vaka & Kriz Yönetimi"),
    CV_DEEP_DIVE("CV & Deneyim Keşfi")
}

data class InterviewQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val questionText: String,
    val questionCategory: String, // "Teknik", "Davranışsal", "Problem Çözme", "Kriz Yönetimi"
    val contextReason: String, // "İlandaki FMCG bayi yönetimi gereksinimi için"
    var userAnswer: String = "",
    var isAnswered: Boolean = false,
    var score: Int = 0,
    var feedback: String = ""
)

data class InterviewReport(
    val overallScore: Int,
    val technicalScore: Int,
    val behavioralScore: Int,
    val problemSolvingScore: Int,
    val communicationScore: Int,
    val strongPoints: List<String>,
    val areasToImprove: List<String>,
    val executiveSummary: String
)

data class DiscoveredExperience(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val company: String,
    val suggestedSection: String, // "Deneyim", "Başarı", "Yetkinlik"
    var addedToProfile: Boolean = false
)
