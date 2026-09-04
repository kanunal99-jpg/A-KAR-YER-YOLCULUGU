package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerDao {
    // Profile
    @Query("SELECT * FROM career_profile WHERE id = 'master_user_profile' LIMIT 1")
    fun getProfileFlow(): Flow<ProfileEntity?>

    @Query("SELECT * FROM career_profile WHERE id = 'master_user_profile' LIMIT 1")
    suspend fun getProfileSync(): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(entity: ProfileEntity)

    // Jobs
    @Query("SELECT * FROM jobs ORDER BY id ASC")
    fun getAllJobs(): Flow<List<JobListingEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): JobListingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobListingEntity>)

    // Applications
    @Query("SELECT * FROM applications ORDER BY timestamp DESC")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationEntity)

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: String, status: String)

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplication(id: String)

    // Resume Versions
    @Query("SELECT * FROM resume_versions ORDER BY id DESC")
    fun getAllResumeVersions(): Flow<List<ResumeVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResumeVersion(resume: ResumeVersionEntity)

    // Interview Sessions
    @Query("SELECT * FROM interview_sessions ORDER BY timestamp DESC")
    fun getAllInterviewSessions(): Flow<List<InterviewSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviewSession(session: InterviewSessionEntity)
}
