package com.example.data.model

data class ApplicationItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val jobId: String,
    val jobTitle: String,
    val company: String,
    val location: String,
    val dateApplied: String,
    val status: ApplicationStatus = ApplicationStatus.SAVED,
    val cvVersionTitle: String = "Master CV",
    val coverLetterPreview: String = "",
    val interviewScore: Int? = null,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class ApplicationStatus(val titleTr: String) {
    SAVED("Kaydedildi"),
    TO_APPLY("Başvurulacak"),
    APPLIED("Başvuruldu"),
    INTERVIEW("Mülakat"),
    OFFER("Teklif"),
    REJECTED("Reddedildi"),
    ACCEPTED("Kabul Edildi")
}

data class ResumeVersion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val targetRoleOrJob: String,
    val professionalSummary: String,
    val keyHighlightedSkills: List<String>,
    val prioritizedExperienceIds: List<String>,
    val coverLetterText: String = "",
    val atsScoreEstimated: Int = 85,
    val createdAtFormatted: String
)
