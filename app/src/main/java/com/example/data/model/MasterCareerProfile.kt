package com.example.data.model

data class MasterCareerProfile(
    val id: String = "master_profile_user",
    val fullName: String = "",
    val title: String = "",
    val email: String = "",
    val phone: String = "",
    val locationCity: String = "",
    val locationCountry: String = "Türkiye",
    val workPreference: String = "Hibrit / Tam Zamanlı", // Uzaktan, Ofis, Hibrit
    val relocationWillingness: Boolean = true,
    val summary: String = "",
    val skills: List<SkillItem> = emptyList(),
    val experiences: List<ExperienceItem> = emptyList(),
    val achievements: List<AchievementItem> = emptyList(),
    val education: List<EducationItem> = emptyList(),
    val certifications: List<CertificationItem> = emptyList(),
    val languages: List<LanguageItem> = emptyList(),
    val leadership: LeadershipProfile = LeadershipProfile(),
    val careerGoals: List<String> = emptyList(),
    val profileScore: Int = 0,
    val improvementAreas: List<ImprovementArea> = emptyList(),
    val pendingQuestions: List<AiQuestion> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class SkillItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val category: String, // "Teknik", "Operasyonel", "Yönetim / Liderlik", "Soft Skills", "Sektör"
    val level: String = "İleri", // "Başlangıç", "Orta", "İleri", "Uzman"
    val isHiddenDiscovered: Boolean = false,
    val evidence: String = ""
)

data class ExperienceItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val company: String,
    val position: String,
    val sector: String,
    val startYear: String,
    val endYear: String,
    val isCurrent: Boolean = false,
    val isManagement: Boolean = false,
    val teamSize: Int = 0,
    val directReports: Int = 0,
    val responsibilities: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val kpis: List<String> = emptyList(),
    val tools: List<String> = emptyList()
)

data class AchievementItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val metric: String,
    val companyOrContext: String,
    val verified: Boolean = true
)

data class EducationItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val school: String,
    val department: String,
    val degree: String,
    val graduationYear: String
)

data class CertificationItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val issuer: String,
    val year: String
)

data class LanguageItem(
    val language: String,
    val level: String // "A1", "A2", "B1", "B2", "C1", "C2", "Anadili"
)

data class LeadershipProfile(
    val hasLeadershipExperience: Boolean = false,
    val maxTeamSize: Int = 0,
    val directReports: Int = 0,
    val budgetManagedUsdOrTry: String = "",
    val keyLeadershipKpis: List<String> = emptyList()
)

data class ImprovementArea(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val actionLabel: String = "Bunu Keşfet",
    val gapType: String = "achievement" // "achievement", "leadership", "skills", "ats"
)

data class AiQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val relatedSection: String, // "Deneyim: Bayi Yönetimi", "Liderlik", "KPI"
    val triggerPhrase: String, // CV'de geçen ifade
    val questionText: String,
    val hint: String = "",
    val potentialHiddenSkills: List<String> = emptyList(),
    var answered: Boolean = false,
    var userAnswer: String = ""
)
