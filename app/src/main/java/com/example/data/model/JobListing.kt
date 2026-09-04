package com.example.data.model

data class JobListing(
    val id: String,
    val title: String,
    val company: String,
    val sector: String,
    val location: String,
    val salaryRange: String,
    val employmentType: String, // "Tam Zamanlı", "Hibrit", "Uzaktan", "Sözleşmeli"
    val seniorityLevel: String, // "Giriş", "Uzman", "Süpervizör", "Yönetici", "Direktör"
    val description: String,
    val duties: List<String>,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val requiredExperienceYears: Int,
    val isManagementRole: Boolean,
    val jobUrl: String,
    val source: String, // "LinkedIn", "Kariyer.net", "Indeed", "Resmi Kariyer Portalı"
    val publishedDate: String,
    val riskLevel: JobRiskLevel = JobRiskLevel.LOW,
    val riskReason: String = "Şirket künyesi doğrulanmış ve resmi kariyer portalında aktif ilan."
)

enum class JobRiskLevel {
    LOW, MEDIUM, HIGH
}

data class JobMatchResult(
    val jobId: String,
    val overallMatchPercent: Int,
    val skillsMatchPercent: Int,
    val experienceMatchPercent: Int,
    val industryMatchPercent: Int,
    val seniorityMatchPercent: Int,
    val locationMatchPercent: Int,
    val educationMatchPercent: Int,
    val whyYouAreFit: List<String>,
    val whyYouAreNotFit: List<String>,
    val missingGapsToClose: List<String>,
    val aiInsight: String // İlanın gerçek anlamı ("Bu ilan aslında dinamik ekip yönetimi ve saha KPI takibi arıyor")
)
