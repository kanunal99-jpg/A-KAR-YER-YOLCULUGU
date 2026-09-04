package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import com.example.domain.CareerAgentEngine
import com.example.domain.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CareerRepository(
    private val careerDao: CareerDao,
    private val scope: CoroutineScope
) {
    private val _currentProfile = MutableStateFlow<MasterCareerProfile>(SampleData.sampleInitialProfile)
    val currentProfile: StateFlow<MasterCareerProfile> = _currentProfile.asStateFlow()

    private val _currentInterviewSession = MutableStateFlow<InterviewSession?>(null)
    val currentInterviewSession: StateFlow<InterviewSession?> = _currentInterviewSession.asStateFlow()

    val applications: Flow<List<ApplicationItem>> = careerDao.getAllApplications().map { entities ->
        entities.map { entity ->
            ApplicationItem(
                id = entity.id,
                jobId = entity.jobId,
                jobTitle = entity.jobTitle,
                company = entity.company,
                location = entity.location,
                dateApplied = entity.dateApplied,
                status = try { ApplicationStatus.valueOf(entity.status) } catch (e: Exception) { ApplicationStatus.SAVED },
                cvVersionTitle = entity.cvVersionTitle,
                coverLetterPreview = entity.coverLetterPreview,
                interviewScore = entity.interviewScore,
                notes = entity.notes,
                timestamp = entity.timestamp
            )
        }
    }

    val resumeVersions: Flow<List<ResumeVersion>> = careerDao.getAllResumeVersions().map { entities ->
        entities.map { entity ->
            ResumeVersion(
                id = entity.id,
                title = entity.title,
                targetRoleOrJob = entity.targetRoleOrJob,
                professionalSummary = entity.professionalSummary,
                keyHighlightedSkills = entity.keyHighlightedSkills,
                prioritizedExperienceIds = entity.prioritizedExperienceIds,
                coverLetterText = entity.coverLetterText,
                atsScoreEstimated = entity.atsScoreEstimated,
                createdAtFormatted = entity.createdAtFormatted
            )
        }
    }

    val jobs: Flow<List<JobListing>> = careerDao.getAllJobs().map { entities ->
        if (entities.isEmpty()) {
            SampleData.sampleJobs
        } else {
            entities.map { entity ->
                JobListing(
                    id = entity.id,
                    title = entity.title,
                    company = entity.company,
                    sector = entity.sector,
                    location = entity.location,
                    salaryRange = entity.salaryRange,
                    employmentType = entity.employmentType,
                    seniorityLevel = entity.seniorityLevel,
                    description = entity.description,
                    duties = entity.duties,
                    requiredSkills = entity.requiredSkills,
                    preferredSkills = entity.preferredSkills,
                    requiredExperienceYears = entity.requiredExperienceYears,
                    isManagementRole = entity.isManagementRole,
                    jobUrl = entity.jobUrl,
                    source = entity.source,
                    publishedDate = entity.publishedDate,
                    riskLevel = try { JobRiskLevel.valueOf(entity.riskLevel) } catch (e: Exception) { JobRiskLevel.LOW },
                    riskReason = entity.riskReason
                )
            }
        }
    }

    init {
        scope.launch(Dispatchers.IO) {
            initDatabase()
        }
    }

    private suspend fun initDatabase() {
        // Pre-populate jobs if empty
        val currentJobs = careerDao.getAllJobs().first()
        if (currentJobs.isEmpty()) {
            val entities = SampleData.sampleJobs.map { job ->
                JobListingEntity(
                    id = job.id,
                    title = job.title,
                    company = job.company,
                    sector = job.sector,
                    location = job.location,
                    salaryRange = job.salaryRange,
                    employmentType = job.employmentType,
                    seniorityLevel = job.seniorityLevel,
                    description = job.description,
                    duties = job.duties,
                    requiredSkills = job.requiredSkills,
                    preferredSkills = job.preferredSkills,
                    requiredExperienceYears = job.requiredExperienceYears,
                    isManagementRole = job.isManagementRole,
                    jobUrl = job.jobUrl,
                    source = job.source,
                    publishedDate = job.publishedDate,
                    riskLevel = job.riskLevel.name,
                    riskReason = job.riskReason
                )
            }
            careerDao.insertJobs(entities)
        }

        // Add a default preset CV version if none exists
        val currentCvs = careerDao.getAllResumeVersions().first()
        if (currentCvs.isEmpty()) {
            val initialCv = CareerAgentEngine.generatePresetCvVersion(SampleData.sampleInitialProfile, "sales")
            saveResumeVersion(initialCv)
        }
    }

    suspend fun updateMasterProfile(profile: MasterCareerProfile) {
        _currentProfile.value = profile
        val json = serializeProfile(profile)
        careerDao.insertProfile(ProfileEntity(profileJson = json))
    }

    suspend fun answerPendingQuestion(questionId: String, answer: String) {
        val current = _currentProfile.value
        val question = current.pendingQuestions.find { it.id == questionId } ?: return

        question.answered = true
        question.userAnswer = answer

        val updatedSkills = current.skills.toMutableList()
        question.potentialHiddenSkills.forEach { hiddenSkill ->
            if (updatedSkills.none { it.name.equals(hiddenSkill, ignoreCase = true) }) {
                updatedSkills.add(
                    SkillItem(
                        name = hiddenSkill,
                        category = "Yönetim / Liderlik",
                        level = "İleri",
                        isHiddenDiscovered = true,
                        evidence = "Kullanıcı soru-cevap mülakatında kanıtlandı: '$answer'"
                    )
                )
            }
        }

        val updatedAchievements = current.achievements.toMutableList()
        if (answer.contains("ciro", ignoreCase = true) || answer.contains("%", ignoreCase = true) || answer.contains("başarı", ignoreCase = true)) {
            updatedAchievements.add(
                AchievementItem(
                    title = "Doğrulanan Liderlik Başarısı",
                    metric = answer.take(50),
                    companyOrContext = "Sütaş / Saha Yönetimi",
                    verified = true
                )
            )
        }

        val updatedScore = (current.profileScore + 5).coerceAtMost(98)
        val updated = current.copy(
            skills = updatedSkills,
            achievements = updatedAchievements,
            profileScore = updatedScore,
            pendingQuestions = current.pendingQuestions
        )
        updateMasterProfile(updated)
    }

    suspend fun addDiscoveredExperienceToProfile(discovered: DiscoveredExperience) {
        val current = _currentProfile.value
        val updatedSkills = current.skills.toMutableList()
        val updatedAchievements = current.achievements.toMutableList()

        updatedAchievements.add(
            AchievementItem(
                title = discovered.title,
                metric = discovered.description,
                companyOrContext = discovered.company,
                verified = true
            )
        )

        val updatedScore = (current.profileScore + 4).coerceAtMost(100)
        updateMasterProfile(
            current.copy(
                achievements = updatedAchievements,
                profileScore = updatedScore
            )
        )
    }

    suspend fun saveApplication(application: ApplicationItem) {
        careerDao.insertApplication(
            ApplicationEntity(
                id = application.id,
                jobId = application.jobId,
                jobTitle = application.jobTitle,
                company = application.company,
                location = application.location,
                dateApplied = application.dateApplied,
                status = application.status.name,
                cvVersionTitle = application.cvVersionTitle,
                coverLetterPreview = application.coverLetterPreview,
                interviewScore = application.interviewScore,
                notes = application.notes,
                timestamp = application.timestamp
            )
        )
    }

    suspend fun updateApplicationStatus(id: String, status: ApplicationStatus) {
        careerDao.updateApplicationStatus(id, status.name)
    }

    suspend fun saveResumeVersion(version: ResumeVersion) {
        careerDao.insertResumeVersion(
            ResumeVersionEntity(
                id = version.id,
                title = version.title,
                targetRoleOrJob = version.targetRoleOrJob,
                professionalSummary = version.professionalSummary,
                keyHighlightedSkills = version.keyHighlightedSkills,
                prioritizedExperienceIds = version.prioritizedExperienceIds,
                coverLetterText = version.coverLetterText,
                atsScoreEstimated = version.atsScoreEstimated,
                createdAtFormatted = version.createdAtFormatted
            )
        )
    }

    fun startInterviewSession(session: InterviewSession) {
        _currentInterviewSession.value = session
    }

    fun updateInterviewSession(session: InterviewSession) {
        _currentInterviewSession.value = session
        scope.launch(Dispatchers.IO) {
            careerDao.insertInterviewSession(
                InterviewSessionEntity(
                    id = session.id,
                    sessionJson = session.jobTitle,
                    timestamp = session.timestamp
                )
            )
        }
    }

    private fun serializeProfile(p: MasterCareerProfile): String {
        val root = JSONObject()
        root.put("id", p.id)
        root.put("fullName", p.fullName)
        root.put("title", p.title)
        root.put("email", p.email)
        root.put("phone", p.phone)
        root.put("locationCity", p.locationCity)
        root.put("profileScore", p.profileScore)
        return root.toString()
    }
}
