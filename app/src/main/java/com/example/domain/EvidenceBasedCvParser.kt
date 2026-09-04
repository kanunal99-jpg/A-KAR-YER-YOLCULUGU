package com.example.domain

import com.example.data.model.ExperienceItem
import com.example.data.model.LanguageItem
import com.example.data.model.MasterCareerProfile
import com.example.data.model.SkillItem
import java.util.Locale

/**
 * Conservative CV parser used for user-provided CV text.
 * It only promotes facts that are explicitly present in the source text.
 * Unknown values stay empty instead of being filled with demo data.
 */
object EvidenceBasedCvParser {
    private val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val phoneRegex = Regex("(?:\\+90|0)?\\s?5\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}")
    private val yearRangeRegex = Regex("(19\\d{2}|20\\d{2})\\s*[-–—]\\s*(19\\d{2}|20\\d{2}|devam|günümüz|current)", RegexOption.IGNORE_CASE)

    private val cityNames = listOf("İstanbul", "Ankara", "İzmir", "Bursa", "Kocaeli", "Antalya", "Sakarya", "Düzce", "Bolu", "Karabük")
    private val knownSkills = listOf(
        "Satış", "Bayi Yönetimi", "Distribütör Yönetimi", "Tanzim-Teşhir", "Merchandising",
        "FMCG", "Excel", "Power BI", "SQL", "SAP", "CRM", "Liderlik", "KPI", "Müzakere",
        "Müşteri İlişkileri", "Saha Operasyonu", "Proje Yönetimi"
    )

    fun parse(rawCv: String): MasterCareerProfile {
        val source = rawCv.trim()
        val lines = source.lines().map { it.trim() }.filter { it.isNotBlank() }
        val email = emailRegex.find(source)?.value.orEmpty()
        val phone = phoneRegex.find(source)?.value.orEmpty()
        val city = cityNames.firstOrNull { source.contains(it, ignoreCase = true) }.orEmpty()
        val fullName = extractName(lines)
        val title = extractLabeledValue(lines, listOf("ünvan", "unvan", "pozisyon", "title", "meslek"))
        val summary = extractLabeledValue(lines, listOf("özet", "profil", "hakkımda", "summary"))
        val skills = extractSkills(source)
        val experiences = extractExplicitExperiences(lines)
        val languages = extractLanguages(source)

        return MasterCareerProfile(
            fullName = fullName,
            title = title,
            email = email,
            phone = phone,
            locationCity = city,
            summary = summary,
            skills = skills,
            experiences = experiences,
            languages = languages,
            achievements = emptyList(),
            education = emptyList(),
            certifications = emptyList(),
            leadership = com.example.data.model.LeadershipProfile(),
            careerGoals = emptyList(),
            profileScore = 0,
            improvementAreas = emptyList(),
            pendingQuestions = emptyList()
        )
    }

    private fun extractName(lines: List<String>): String {
        val labeled = extractLabeledValue(lines, listOf("ad soyad", "isim", "name"))
        if (labeled.isNotBlank()) return labeled
        return lines.firstOrNull { line ->
            line.length in 3..60 &&
                !line.contains("cv", true) &&
                !line.contains("özgeçmiş", true) &&
                !emailRegex.containsMatchIn(line) &&
                !phoneRegex.containsMatchIn(line) &&
                line.count { it.isLetter() } >= 3
        }.orEmpty()
    }

    private fun extractLabeledValue(lines: List<String>, labels: List<String>): String {
        for (line in lines) {
            val match = labels.firstOrNull { label -> line.startsWith("$label:", true) || line.startsWith("$label -", true) }
            if (match != null) {
                return line.substringAfter(':', line.substringAfter("-", "")).trim()
            }
        }
        return ""
    }

    private fun extractSkills(source: String): List<SkillItem> = knownSkills.mapNotNull { skill ->
        if (!source.contains(skill, ignoreCase = true)) return@mapNotNull null
        SkillItem(
            name = skill,
            category = when (skill.lowercase(Locale("tr"))) {
                "excel", "power bi", "sql", "sap", "crm" -> "Teknik"
                "liderlik", "kpi" -> "Yönetim / Liderlik"
                "fmcg" -> "Sektör"
                else -> "Operasyonel"
            },
            level = "Belirtilmedi",
            evidence = "CV metninde açıkça geçti"
        )
    }

    private fun extractExplicitExperiences(lines: List<String>): List<ExperienceItem> {
        val result = mutableListOf<ExperienceItem>()
        for (line in lines) {
            val match = yearRangeRegex.find(line) ?: continue
            val beforeDates = line.substring(0, match.range.first).trim(' ', '-', '–', '—', '|')
            if (beforeDates.isBlank()) continue
            val parts = beforeDates.split("|", " / ", " — ", " - ").map { it.trim() }.filter { it.isNotBlank() }
            if (parts.size < 2) continue
            val start = match.groupValues[1]
            val end = match.groupValues[2]
            result += ExperienceItem(
                company = parts.first(),
                position = parts.drop(1).joinToString(" / "),
                sector = "",
                startYear = start,
                endYear = end,
                isCurrent = end.equals("devam", true) || end.equals("günümüz", true) || end.equals("current", true)
            )
        }
        return result
    }

    private fun extractLanguages(source: String): List<LanguageItem> {
        val result = mutableListOf<LanguageItem>()
        if (source.contains("Türkçe", true)) result += LanguageItem("Türkçe", "Belirtilmedi")
        if (source.contains("İngilizce", true) || source.contains("English", true)) result += LanguageItem("İngilizce", "Belirtilmedi")
        if (source.contains("Almanca", true) || source.contains("German", true)) result += LanguageItem("Almanca", "Belirtilmedi")
        return result
    }
}
