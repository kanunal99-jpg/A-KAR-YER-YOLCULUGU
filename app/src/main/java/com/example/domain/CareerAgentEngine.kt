package com.example.domain

import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object CareerAgentEngine {

    /**
     * Parses raw CV text semantically into a Master Career Profile.
     * Supports Turkish, English, unstructured or structured CV formats.
     */
    fun parseCvText(rawCv: String): MasterCareerProfile =
        EvidenceBasedCvParser.parse(rawCv)

    /**
     * Expands target roles from candidate profile into multiple adjacent discovery titles.
     */
    fun expandCareerDiscoveryRoles(profile: MasterCareerProfile): List<String> {
        return listOf(
            "Saha Satış Yöneticisi",
            "Bölge Satış Müdürü",
            "Bayi Şefi",
            "Satış Süpervizörü",
            "Kanal Yöneticisi (Horeca / Geleneksel)",
            "Merchandising Supervisor",
            "Key Account Manager (KAM)",
            "Saha Operasyon Yöneticisi"
        )
    }

    /**
     * Semantic Job Matching:
     * Calculates overall %, breakdown by skills, experience, industry, seniority, location, education.
     * Generates explainable reasons for fit & gaps, and deep intent of the job.
     */
    fun evaluateJobMatch(profile: MasterCareerProfile, job: JobListing): JobMatchResult {
        // Match calculation based on profile evidence
        val profileSkillNames = profile.skills.map { it.name.lowercase() }
        val matchedSkills = job.requiredSkills.filter { req ->
            profileSkillNames.any { p -> p.contains(req.lowercase()) || req.lowercase().contains(p) }
        }
        val skillsScore = if (job.requiredSkills.isNotEmpty()) {
            ((matchedSkills.size.toFloat() / job.requiredSkills.size) * 100).toInt().coerceIn(0, 100)
        } else 85

        val experienceScore = if (profile.experiences.size >= 2) 92 else 75
        val industryScore = if (job.sector.contains("FMCG", ignoreCase = true) || job.sector.contains("Gıda", ignoreCase = true) || job.sector.contains("Satış", ignoreCase = true)) 95 else 78
        val seniorityScore = if (profile.leadership.hasLeadershipExperience && job.isManagementRole) 90 else 80
        val locationScore = if (profile.locationCity.equals(job.location, ignoreCase = true) || job.location.contains(profile.locationCity, ignoreCase = true) || profile.relocationWillingness) 100 else 75
        val educationScore = 88

        val overall = ((skillsScore * 0.35) + (experienceScore * 0.20) + (industryScore * 0.15) + (seniorityScore * 0.15) + (locationScore * 0.10) + (educationScore * 0.05)).toInt()

        // Explain matching from candidate/advert evidence, not a fictional sample CV.
        val whyFit = mutableListOf<String>()
        if (matchedSkills.isNotEmpty()) {
            whyFit.add("CV'de bulunan ve ilanla eşleşen beceriler: ${matchedSkills.joinToString(", ")}")
        }
        profile.experiences.firstOrNull { it.company.isNotBlank() || it.position.isNotBlank() }?.let { exp ->
            val positionAndCompany = listOf(exp.position, exp.company)
                .filter { it.isNotBlank() }.joinToString(" / ")
            whyFit.add("CV'de beyan edilen deneyim: $positionAndCompany")
        }
        profile.achievements.firstOrNull { it.verified && it.title.isNotBlank() }?.let {
            whyFit.add("CV'de belirtilen başarı: ${it.title}")
        }
        if (profile.locationCity.isNotBlank() && job.location.isNotBlank() &&
            (profile.locationCity.equals(job.location, ignoreCase = true) ||
                job.location.contains(profile.locationCity, ignoreCase = true))) {
            whyFit.add("CV'de belirtilen şehir, ilandaki konumla eşleşiyor: ${profile.locationCity}")
        }

        val missingRequiredSkills = job.requiredSkills.filterNot { it in matchedSkills }
        val missingPreferredSkills = job.preferredSkills.filterNot { preferred ->
            profileSkillNames.any { skill ->
                skill.isNotBlank() && (skill.contains(preferred.lowercase()) ||
                    preferred.lowercase().contains(skill))
            }
        }
        val whyNotFit = mutableListOf<String>()
        if (missingRequiredSkills.isNotEmpty()) {
            whyNotFit.add("CV'de doğrulanamayan zorunlu beceriler: ${missingRequiredSkills.joinToString(", ")}")
        }
        if (missingPreferredSkills.isNotEmpty()) {
            whyNotFit.add("CV'de doğrulanamayan tercih edilen beceriler: ${missingPreferredSkills.joinToString(", ")}")
        }
        if (whyNotFit.isEmpty()) {
            whyNotFit.add("İşverenin ek koşulları ve deneyim düzeyi görüşmede ayrıca doğrulanmalıdır.")
        }
        val missingGaps = buildList {
            if (missingRequiredSkills.isNotEmpty()) {
                add("Zorunlu beceriler için CV'de kanıt veya eğitim planı ekleyin: ${missingRequiredSkills.joinToString(", ")}")
            }
            if (missingPreferredSkills.isNotEmpty()) {
                add("Tercih edilen becerilere ilişkin varsa deneyim kanıtı ekleyin: ${missingPreferredSkills.joinToString(", ")}")
            }
            if (isEmpty()) add("CV'nizdeki mevcut beceri ve başarı kanıtlarını görüşmede açıklamaya hazırlanın.")
        }

        val aiInsight = when {
            job.duties.isNotEmpty() ->
                "İlanda açıklanan görevler: ${job.duties.take(3).joinToString("; ")}."
            job.description.isNotBlank() ->
                "İlan açıklaması: ${job.description.take(200)}"
            else -> "Pozisyon: ${job.title}. Gereksinimler işverenle ayrıca doğrulanmalıdır."
        }

        return JobMatchResult(
            jobId = job.id,
            overallMatchPercent = overall,
            skillsMatchPercent = skillsScore,
            experienceMatchPercent = experienceScore,
            industryMatchPercent = industryScore,
            seniorityMatchPercent = seniorityScore,
            locationMatchPercent = locationScore,
            educationMatchPercent = educationScore,
            whyYouAreFit = whyFit,
            whyYouAreNotFit = whyNotFit,
            missingGapsToClose = missingGaps,
            aiInsight = aiInsight
        )
    }

    /**
     * Optimizes Master Profile for a specific job without hallucinating.
     */
    fun generateTailoredCv(profile: MasterCareerProfile, job: JobListing): ResumeVersion {
        val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date())

        // Only use details supplied by the candidate, never sample-company metrics.
        val highlightedSkills = job.requiredSkills.filter { required ->
            profile.skills.any { it.name.contains(required, ignoreCase = true) ||
                required.contains(it.name, ignoreCase = true) }
        }.ifEmpty { profile.skills.map { it.name }.filter { it.isNotBlank() }.take(6) }

        val evidencedRoles = profile.experiences.take(3).mapNotNull { experience ->
            val parts = listOf(experience.position, experience.company).filter { it.isNotBlank() }
            parts.joinToString(" / ").takeIf { it.isNotBlank() }
        }
        val candidateAchievements = profile.achievements
            .filter { it.verified && it.title.isNotBlank() }
            .take(3).map { it.title }

        val summaryParts = mutableListOf(
            "${job.title} pozisyonu için adayın sağladığı CV bilgilerine dayanan profil."
        )
        if (evidencedRoles.isNotEmpty()) {
            summaryParts.add("Deneyim: ${evidencedRoles.joinToString("; ")}.")
        }
        if (highlightedSkills.isNotEmpty()) {
            summaryParts.add("Yetkinlikler: ${highlightedSkills.joinToString(", ")}.")
        }
        if (candidateAchievements.isNotEmpty()) {
            summaryParts.add("CV'de belirtilen başarılar: ${candidateAchievements.joinToString("; ")}.")
        }
        val summary = summaryParts.joinToString(" ")

        val coverLetter = buildString {
            appendLine("Sayın ${job.company} İşe Alım Ekibi,")
            appendLine()
            appendLine("${job.title} pozisyonuna başvurmak istiyorum.")
            if (evidencedRoles.isNotEmpty()) {
                appendLine("CV'mde yer alan deneyimlerim: ${evidencedRoles.joinToString("; ")}.")
            }
            if (highlightedSkills.isNotEmpty()) {
                appendLine("Profilimde belirtilen ilgili yetkinliklerim: ${highlightedSkills.joinToString(", ")}.")
            }
            if (candidateAchievements.isNotEmpty()) {
                appendLine("CV'mde belirttiğim başarılar: ${candidateAchievements.joinToString("; ")}.")
            }
            appendLine("Başvurumun gerekliliklerle uyumunu görüşmede değerlendirmekten memnuniyet duyarım.")
            appendLine()
            appendLine("Saygılarımla,")
            appendLine(profile.fullName.ifBlank { "Aday" })
            val contact = listOf(profile.phone, profile.email).filter { it.isNotBlank() }
            if (contact.isNotEmpty()) appendLine(contact.joinToString(" | "))
        }.trim()

        return ResumeVersion(
            id = "cv_tailored_${job.id}_${System.currentTimeMillis()}",
            title = "${job.company} — ${job.title} Özel CV",
            targetRoleOrJob = "${job.title} (${job.company})",
            professionalSummary = summary,
            keyHighlightedSkills = highlightedSkills,
            prioritizedExperienceIds = profile.experiences.map { it.id },
            coverLetterText = coverLetter,
            atsScoreEstimated = evaluateJobMatch(profile, job).overallMatchPercent,
            createdAtFormatted = dateStr
        )
    }

    /**
     * Generates a preset role-based CV version from Master Profile (e.g. Sales CV, Leadership CV, FMCG CV).
     */
    fun generatePresetCvVersion(profile: MasterCareerProfile, presetType: String): ResumeVersion {
        val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date())
        val (title, targetRole) = when (presetType) {
            "management" -> "Yönetim & Liderlik Odaklı CV" to "Yönetim / Liderlik"
            "operations" -> "Saha Operasyon & FMCG CV" to "Saha Operasyon"
            else -> "Satış & İş Geliştirme CV" to "Satış / İş Geliştirme"
        }

        // A preset changes presentation, not the candidate's employment history.
        val skills = profile.skills.map { it.name.trim() }.filter { it.isNotEmpty() }.take(6)
        val roles = profile.experiences.take(3).mapNotNull { experience ->
            listOf(experience.position, experience.company)
                .filter { it.isNotBlank() }
                .joinToString(" / ")
                .takeIf { it.isNotBlank() }
        }
        val achievements = profile.achievements
            .filter { it.verified && it.title.isNotBlank() }
            .take(3).map { it.title }

        val evidence = mutableListOf<String>()
        if (profile.summary.isNotBlank()) evidence.add(profile.summary.trim())
        if (roles.isNotEmpty()) evidence.add("Deneyimler: ${roles.joinToString("; ")}.")
        if (skills.isNotEmpty()) evidence.add("Beceriler: ${skills.joinToString(", ")}.")
        if (achievements.isNotEmpty()) evidence.add("CV'de belirtilen başarılar: ${achievements.joinToString("; ")}.")

        return ResumeVersion(
            id = "cv_preset_${System.currentTimeMillis()}_${presetType}",
            title = title,
            targetRoleOrJob = targetRole,
            professionalSummary = evidence.joinToString(" ").ifBlank {
                "Mesleki deneyim ve yetkinlik bilgileri aday profiline henüz eklenmedi."
            },
            keyHighlightedSkills = skills,
            prioritizedExperienceIds = profile.experiences.map { it.id },
            // A generic preset has no target job, so an ATS match cannot be measured.
            atsScoreEstimated = 0,
            createdAtFormatted = dateStr
        )
    }

    /**
     * Generates position-tailored interview questions for candidate.
     */
    fun generateInterviewSession(job: JobListing, profile: MasterCareerProfile, mode: InterviewMode): InterviewSession {
        val questions = when (mode) {
            InterviewMode.JOB_SPECIFIC -> listOf(
                InterviewQuestion(
                    questionText = "${job.company} bünyesindeki ${job.title} pozisyonunda sorumlu olacağınız bölgedeki bayi ağının satış ve stok KPI'larını ilk 90 günde nasıl ele alırsınız?",
                    questionCategory = "İlana Özel Strateji",
                    contextReason = "${job.company} ilanındaki bayi hedefleri ve yönetim şartı"
                ),
                InterviewQuestion(
                    questionText = "İlanımızda ${job.requiredSkills.take(2).joinToString(" ve ")} yetkinlikleri kritik önemde. Geçmişte bu iki yetkinliği birlikte kullanarak çözdüğünüz zorlu bir iş problemini anlatır mısınız?",
                    questionCategory = "Teknik / Yetkinlik",
                    contextReason = "İlandaki zorunlu yetkinlik analizi"
                ),
                InterviewQuestion(
                    questionText = "Sahada ekibinizin motivasyonunun düştüğü ve aylık hedefin %20 gerisinde kaldığınız bir kriz anında ne tür bir aksiyon planı uygularsınız?",
                    questionCategory = "Kriz & Liderlik",
                    contextReason = "Yöneticilik pozisyonunda liderlik dayanıklılığı"
                ),
                InterviewQuestion(
                    questionText = profile.achievements.firstOrNull { it.verified && it.title.isNotBlank() }
                        ?.let { "CV'nizde belirttiğiniz '${it.title}' başarısını hangi adımlarla elde ettiniz? Nasıl doğrularsınız?" }
                        ?: "Önceki çalışmalarınızdan ölçülebilir bir başarı örneğiniz varsa hangi adımları izlediğinizi anlatır mısınız?",
                    questionCategory = "CV & Kanıt Doğrulama",
                    contextReason = "Adayın kendi başarı kanıtının incelenmesi"
                ),
                InterviewQuestion(
                    questionText = "Bizim sektörümüzde rekabet çok sert ve pazar dinamikleri hızla değişiyor. Şirketimizi rakiplerimizden ayrıştıracak saha tanzim-teşhir inovasyonu için ne önerirsiniz?",
                    questionCategory = "Sektörel Vizyon",
                    contextReason = "Sektör bilgi derinliği"
                )
            )
            InterviewMode.BEHAVIORAL -> listOf(
                InterviewQuestion(
                    questionText = "Birlikte çalıştığınız bir bayi veya müşteri yöneticisiyle yaşadığınız en ciddi görüş ayrılığını STAR (Durum, Görev, Eylem, Sonuç) yöntemiyle aktarır mısınız?",
                    questionCategory = "STAR Davranışsal",
                    contextReason = "İletişim ve çatışma yönetimi"
                ),
                InterviewQuestion(
                    questionText = "Ekibinizden bir çalışanın performans hedeflerinin gerisinde kaldığını fark ettiğinizde nasıl bir geri bildirim süreci işletirsiniz?",
                    questionCategory = "Performans Geri Bildirimi",
                    contextReason = "İnsan yönetimi ve empatik liderlik"
                ),
                InterviewQuestion(
                    questionText = "Beklenmedik bir mevzuat veya şirket politika değişikliği sonucu tüm planlarınız alt üst olduğunda gösterdiğiniz adaptasyon örneği nedir?",
                    questionCategory = "Çeviklik & Esneklik",
                    contextReason = "Değişim yönetimi"
                )
            )
            InterviewMode.CASE_STUDY -> listOf(
                InterviewQuestion(
                    questionText = "Vaka: Bölgenizdeki en büyük distribütörünüz nakit akışı krizi gerekçesiyle siparişlerini durdurdu ve vadesi geçmiş 2 Milyon ₺ borcu var. Sevkiyatı durdurursanız şirket cironuz %30 düşecek, devam ederseniz finansal risk büyüyecek. Hangi somut 3 adımı atarsınız?",
                    questionCategory = "Finans & Ticari Kriz",
                    contextReason = "Risk analizi ve ticari müzakere"
                ),
                InterviewQuestion(
                    questionText = "Vaka: Rakip firma bayilerinize %8 daha yüksek iskonto teklif ederek tüm reyonları kapattı. 48 saat içinde saha hakimiyetini geri kazanmak için ne yaparsınız?",
                    questionCategory = "Pazar Rekabeti",
                    contextReason = "Saha refleksleri ve taktiksel satış"
                )
            )
            else -> listOf(
                InterviewQuestion(
                    questionText = "Kariyerinizde bugüne kadar aldığınız en büyük profesyonel risk neydi ve bu karardan ne öğrendiniz?",
                    questionCategory = "Genel Kariyer",
                    contextReason = "Öz farkındalık"
                ),
                InterviewQuestion(
                    questionText = "CV'nizde yer almayan ama karakterinizi ve çalışma disiplininizi en iyi yansıtan bir deneyiminizi paylaşır mısınız?",
                    questionCategory = "Gizli Yetenek Keşfi",
                    contextReason = "Ek deneyim tespiti"
                )
            )
        }

        return InterviewSession(
            id = UUID.randomUUID().toString(),
            jobId = job.id,
            jobTitle = job.title,
            company = job.company,
            mode = mode,
            questions = questions,
            currentQuestionIndex = 0,
            isCompleted = false
        )
    }

    /**
     * Evaluates candidate's answer to an interview question.
     * Evaluates communication, concrete examples, and problem solving.
     * Critically detects if candidate disclosed a new experience not in their CV!
     */
    fun evaluateAnswer(
        session: InterviewSession,
        questionIndex: Int,
        userAnswer: String
    ): InterviewSession {
        val updatedQuestions = session.questions.toMutableList()
        val q = updatedQuestions.getOrNull(questionIndex) ?: return session

        val wordCount = userAnswer.split("\\s+".toRegex()).size
        val hasNumbersOrMetrics = Regex("\\d+%|%\\d+|\\d+\\s*(kişi|bayi|lira|tl|₺|bin|milyon|ay|yıl)", RegexOption.IGNORE_CASE).containsMatchIn(userAnswer)
        val hasConcreteExample = userAnswer.contains("örneğin", ignoreCase = true) || userAnswer.contains("yaşadığım", ignoreCase = true) || userAnswer.contains("yaptım", ignoreCase = true) || userAnswer.contains("sağladık", ignoreCase = true)

        var score = 65
        if (wordCount > 25) score += 15
        else if (wordCount > 12) score += 8
        if (hasNumbersOrMetrics) score += 12
        if (hasConcreteExample || userAnswer.contains("düşür", ignoreCase = true) || userAnswer.contains("artır", ignoreCase = true) || userAnswer.contains("yönet", ignoreCase = true)) score += 8
        score = score.coerceIn(50, 98)

        val feedback = when {
            hasNumbersOrMetrics && wordCount > 35 ->
                "Mükemmel bir cevap. Somut metrikler ve rakamlar vererek etkinizi net biçimde kanıtladınız. STAR metoduna tam uyum sağlandı."
            wordCount > 25 ->
                "Oldukça net ve tutarlı bir yaklaşım. Cevabınızı 'Bu aksiyon sonucunda ciro/maliyet % kaç etkilendi?' gibi somut bir veriyle taçlandırırsanız puanınız tam olur."
            else ->
                "Cevap yönü doğru ancak biraz kısa kaldı. Mülakatçıya güven vermek için yaşanmış somut bir örnek ve takip ettiğiniz KPI'ları eklemenizi öneririm."
        }

        q.userAnswer = userAnswer
        q.isAnswered = true
        q.score = score
        q.feedback = feedback

        // Check for newly discovered experiences during interview
        val discovered = session.discoveredExperiences.toMutableList()
        if (userAnswer.contains("yeni", ignoreCase = true) || userAnswer.contains("proje", ignoreCase = true) || hasNumbersOrMetrics) {
            val titleCandidate = if (userAnswer.length > 50) userAnswer.take(45) + "..." else userAnswer
            val existing = discovered.any { it.description == userAnswer }
            if (!existing && userAnswer.length > 20) {
                discovered.add(
                    DiscoveredExperience(
                        title = "Mülakatta Ortaya Çıkan Başarı",
                        description = userAnswer,
                        company = session.company,
                        suggestedSection = "Başarılar & Yetkinlikler",
                        addedToProfile = false
                    )
                )
            }
        }

        val allAnswered = updatedQuestions.all { it.isAnswered }
        var report: InterviewReport? = session.report
        if (allAnswered) {
            val avgScore = updatedQuestions.map { it.score }.average().toInt()
            report = InterviewReport(
                overallScore = avgScore,
                technicalScore = (avgScore * 1.05).toInt().coerceAtMost(100),
                behavioralScore = (avgScore * 0.96).toInt(),
                problemSolvingScore = (avgScore * 1.02).toInt().coerceAtMost(100),
                communicationScore = (avgScore * 0.98).toInt(),
                strongPoints = listOf(
                    "Saha ve bayi dinamiklerine hakimiyet ve KPI takibi",
                    "Kriz durumlarında rasyonel ve çözüm odaklı yaklaşım",
                    "Ekip yönetiminde net sorumluluk dağılımı"
                ),
                areasToImprove = listOf(
                    "Finansal tasarruf ve kar marjı metriklerini daha sık vurgulamak",
                    "Müzakere süreçlerinde ikinci alternatif planı önceden belirtmek"
                ),
                executiveSummary = "Aday, ${session.company} - ${session.jobTitle} pozisyonu için güçlü bir saha liderliği ve ticari olgunluk sergilemiştir. Teknik ve operasyonel yetkinliği pozisyonun gereklilikleriyle yüksek oranda örtüşmektedir."
            )
        }

        return session.copy(
            questions = updatedQuestions,
            currentQuestionIndex = if (questionIndex + 1 < updatedQuestions.size) questionIndex + 1 else questionIndex,
            isCompleted = allAnswered,
            report = report,
            discoveredExperiences = discovered
        )
    }

    /**
     * AI Career Coach Response Engine
     */
    fun answerCoachQuery(profile: MasterCareerProfile, query: String): String {
        val qLower = query.lowercase()
        return when {
            qLower.contains("3 yıl") || qLower.contains("gelecek") || qLower.contains("kariyer simülasyon") ->
                """
📊 **3 Yıllık Kariyer Simülasyonu:**
Mevcut 7 yıllık saha ve bayi yönetimi profiliniz incelendiğinde önünüzde 3 güçlü ana yol bulunmaktadır:

1. **Yönetim / Direktörlük Yolu (%75 Hazırlık):**
   - 1. Yıl: Bölge Satış Müdürü (85K-110K ₺)
   - 2-3. Yıl: Türkiye Satış Direktörü veya Saha Operasyon Direktörü (140K-200K ₺)
   - *Gereksinim:* SAP SD ve P&L (Kâr/Zarar tablosu) bütçe sertifikasyonu.

2. **Ulusal Zincir & KAM Uzmanlığı (%65 Hazırlık):**
   - Key Account Manager (Ulusal Marketler)
   - *Gereksinim:* BİM/Migros JBP (Joint Business Plan) tecrübesi ve ticari İngilizce.

3. **E-Ticaret & Hızlı Teslimat Lojistiği (%80 Hazırlık):**
   - Trendyol / Getir Dağıtım Operasyon Müdürü
   - *Gereksinim:* Filo SLA ve teknoloji odaklı operasyon takibi.
                """.trimIndent()

            qLower.contains("iş bulamadım") || qLower.contains("uygun iş yok") || qLower.contains("filtre") ->
                """
🎯 **'İş Bulamadım' Kriter Analiz Raporu:**
Profiliniz oldukça güçlü. Ancak mevcut arama filtrelerinizde şu esnemeleri yaptığımızda pazar anında genişliyor:

- **Şehir Filtresi:** Sadece İstanbul yerine 'Kocaeli & Tekirdağ (Marmara Geneli)' eklendiğinde **+38 yeni ilan** açılıyor.
- **Unvan Esnekliği:** Sadece 'Bölge Müdürü' yerine 'Bayi Şefi' ve 'Saha Satış Yöneticisi' tarandığında **+64 yeni ilan** bulunuyor.
- **Uzaktan / Hibrit:** Hibrit operasyon rolleri dahil edildiğinde **+22 teknoloji ve lojistik ilanı** eşleşiyor.

👉 Toplamda **124 yeni uygun ilan** keşfedilebilir! Filtrelerinizi genişletmek ister misiniz?
                """.trimIndent()

            qLower.contains("cv") || qLower.contains("puan") || qLower.contains("geliştir") ->
                """
📈 **CV Gücü ve Geliştirme Analizi:**
Mevcut CV Güç Puanınız: **${profile.profileScore} / 100**

Güçlü Yönleriniz:
- Bayi ve distribütör yönetiminde kanıtlanmış 60+ nokta tecrübesi.
- 18 kişilik saha ekibi yönetimi.

Hemen Geliştirilebilecek 2 Kritik Nokta:
1. **Ölçülebilir Metrikler:** '%14 tasarruf sağlandı' ifadesi gibi finansal başarıların sayısını 3'ten 6'ya çıkaralım.
2. **Eksik Soruları Cevaplayın:** Bekleyen 2 soruyu cevapladığınızda CV'nize otomatik olarak 'Kriz Yönetimi' ve 'Satış Koçluğu' yetkinlikleri kanıtlı olarak eklenecek!
                """.trimIndent()

            else ->
                """
Merhaba ${profile.fullName}! Ben senin ARAMA Kişisel Kariyer Ajanınım. 

Bugün senin için şunları yapabiliriz:
1. **CV'ni Geliştir:** Bekleyen soruları cevaplayarak CV güç puanını 90+'a çıkarabiliriz.
2. **Yeni İş Eşleşmeleri:** Profiline uyan ETİ, Trendyol ve Migros gibi güncel ilanları inceleyebiliriz.
3. **İlana Özel Mülakat:** Hedeflediğin bir ilan için 5 dakikalık mülakat simülasyonu yapıp eksiklerini görebiliriz.

Hangi alanda adım atmak istersin?
                """.trimIndent()
        }
    }
}
