package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CareerRepository
import com.example.domain.CareerAgentEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER, AI_COACH
}

class CareerViewModel(
    private val repository: CareerRepository
) : ViewModel() {

    val profile: StateFlow<MasterCareerProfile> = repository.currentProfile
    val jobs: Flow<List<JobListing>> = repository.jobs
    val applications: Flow<List<ApplicationItem>> = repository.applications
    val resumeVersions: Flow<List<ResumeVersion>> = repository.resumeVersions
    val interviewSession: StateFlow<InterviewSession?> = repository.currentInterviewSession

    private val _selectedJob = MutableStateFlow<JobListing?>(null)
    val selectedJob: StateFlow<JobListing?> = _selectedJob.asStateFlow()

    private val _jobMatchResult = MutableStateFlow<JobMatchResult?>(null)
    val jobMatchResult: StateFlow<JobMatchResult?> = _jobMatchResult.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _coachMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.AI_COACH,
                text = "Merhaba! Ben senin kişisel AI Kariyer Koçunum. Profilini derinlemesine tanıdım. Bugün CV'ni geliştirebilir, sana en uygun gerçek iş ilanlarını eşleştirebilir veya hedefin için birebir mülakat simülasyonu yapabiliriz. Nereden başlayalım?"
            )
        )
    )
    val coachMessages: StateFlow<List<ChatMessage>> = _coachMessages.asStateFlow()

    private val _activeFilterRole = MutableStateFlow<String>("Tümü")
    val activeFilterRole: StateFlow<String> = _activeFilterRole.asStateFlow()

    private val _userSearchQuery = MutableStateFlow<String>("")
    val userSearchQuery: StateFlow<String> = _userSearchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun setFilterRole(role: String) {
        _activeFilterRole.value = role
    }

    fun selectJob(job: JobListing) {
        _selectedJob.value = job
        val match = CareerAgentEngine.evaluateJobMatch(profile.value, job)
        _jobMatchResult.value = match
    }

    fun clearJobSelection() {
        _selectedJob.value = null
        _jobMatchResult.value = null
    }

    fun uploadAndAnalyzeCv(rawText: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            kotlinx.coroutines.delay(800) // Realistic AI extraction feedback
            val parsed = CareerAgentEngine.parseCvText(rawText)
            repository.updateMasterProfile(parsed)
            _isProcessing.value = false
        }
    }

    fun answerGapQuestion(questionId: String, answer: String) {
        viewModelScope.launch {
            repository.answerPendingQuestion(questionId, answer)
        }
    }

    fun generateTailoredCvForJob(job: JobListing, onDone: (ResumeVersion) -> Unit) {
        viewModelScope.launch {
            _isProcessing.value = true
            kotlinx.coroutines.delay(600)
            val tailored = CareerAgentEngine.generateTailoredCv(profile.value, job)
            repository.saveResumeVersion(tailored)
            _isProcessing.value = false
            onDone(tailored)
        }
    }

    fun generatePresetCv(presetType: String) {
        viewModelScope.launch {
            val preset = CareerAgentEngine.generatePresetCvVersion(profile.value, presetType)
            repository.saveResumeVersion(preset)
        }
    }

    fun startInterview(job: JobListing, mode: InterviewMode = InterviewMode.JOB_SPECIFIC) {
        val session = CareerAgentEngine.generateInterviewSession(job, profile.value, mode)
        repository.startInterviewSession(session)
    }

    fun submitInterviewAnswer(answer: String) {
        val current = interviewSession.value ?: return
        val evaluated = CareerAgentEngine.evaluateAnswer(
            session = current,
            questionIndex = current.currentQuestionIndex,
            userAnswer = answer
        )
        repository.updateInterviewSession(evaluated)
    }

    fun addDiscoveredExperienceToProfile(discovered: DiscoveredExperience) {
        viewModelScope.launch {
            repository.addDiscoveredExperienceToProfile(discovered)
            val current = interviewSession.value ?: return@launch
            val updatedList = current.discoveredExperiences.map {
                if (it.id == discovered.id) it.copy(addedToProfile = true) else it
            }
            repository.updateInterviewSession(current.copy(discoveredExperiences = updatedList))
        }
    }

    fun applyToJob(job: JobListing, cvVersionTitle: String, coverLetter: String, notes: String) {
        viewModelScope.launch {
            val app = ApplicationItem(
                jobId = job.id,
                jobTitle = job.title,
                company = job.company,
                location = job.location,
                dateApplied = "Bugün",
                status = ApplicationStatus.APPLIED,
                cvVersionTitle = cvVersionTitle,
                coverLetterPreview = coverLetter,
                notes = notes
            )
            repository.saveApplication(app)
        }
    }

    fun saveJobForLater(job: JobListing) {
        viewModelScope.launch {
            val app = ApplicationItem(
                jobId = job.id,
                jobTitle = job.title,
                company = job.company,
                location = job.location,
                dateApplied = "Bugün",
                status = ApplicationStatus.SAVED,
                cvVersionTitle = "Master CV",
                coverLetterPreview = "",
                notes = "İlan daha sonra başvurulmak üzere kaydedildi."
            )
            repository.saveApplication(app)
        }
    }

    fun updateApplicationStatus(id: String, status: ApplicationStatus) {
        viewModelScope.launch {
            repository.updateApplicationStatus(id, status)
        }
    }

    fun sendCoachMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(sender = MessageSender.USER, text = text)
        val currentList = _coachMessages.value.toMutableList()
        currentList.add(userMsg)
        _coachMessages.value = currentList

        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            val replyText = CareerAgentEngine.answerCoachQuery(profile.value, text)
            val aiMsg = ChatMessage(sender = MessageSender.AI_COACH, text = replyText)
            val updated = _coachMessages.value.toMutableList()
            updated.add(aiMsg)
            _coachMessages.value = updated
        }
    }
}
