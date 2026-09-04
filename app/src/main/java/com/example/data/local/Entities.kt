package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "career_profile")
data class ProfileEntity(
    @PrimaryKey val id: String = "master_user_profile",
    val profileJson: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "jobs")
data class JobListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val sector: String,
    val location: String,
    val salaryRange: String,
    val employmentType: String,
    val seniorityLevel: String,
    val description: String,
    val duties: List<String>,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val requiredExperienceYears: Int,
    val isManagementRole: Boolean,
    val jobUrl: String,
    val source: String,
    val publishedDate: String,
    val riskLevel: String, // "LOW", "MEDIUM", "HIGH"
    val riskReason: String
)

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val jobTitle: String,
    val company: String,
    val location: String,
    val dateApplied: String,
    val status: String, // e.g. "SAVED", "APPLIED", "INTERVIEW", etc.
    val cvVersionTitle: String,
    val coverLetterPreview: String,
    val interviewScore: Int?,
    val notes: String,
    val timestamp: Long
)

@Entity(tableName = "resume_versions")
data class ResumeVersionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val targetRoleOrJob: String,
    val professionalSummary: String,
    val keyHighlightedSkills: List<String>,
    val prioritizedExperienceIds: List<String>,
    val coverLetterText: String,
    val atsScoreEstimated: Int,
    val createdAtFormatted: String
)

@Entity(tableName = "interview_sessions")
data class InterviewSessionEntity(
    @PrimaryKey val id: String,
    val sessionJson: String,
    val timestamp: Long
)
