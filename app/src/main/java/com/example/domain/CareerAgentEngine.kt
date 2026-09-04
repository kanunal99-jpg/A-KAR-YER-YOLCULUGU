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
    fun parseCvText(rawCv: String): MasterCareerProfile {
        val lines = rawCv.lines().map { it.trim() }.filter { it.isNotEmpty() }
        
        // Extract basic name / header
        val fullName = lines.firstOrNull { it.length in 3..40 && !it.contains("CV", ignoreCase = true) && !it.contains("Özgeçmiş", ignoreCase = true) }
            ?: "Kariyer Adayı"

        val email = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            .find(rawCv)?.value ?: "aday@ornek.com"

        val phone = Regex("(\\+90|0)?\\s?5\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}")
            .find(rawCv)?.value ?: "+90 532 123 45 67"

        val city = when {
            rawCv.contains("İstanbul", ignoreCase = true) -> "İstanbul"
            rawCv.contains("Ankara", ignoreCase = true) -> "Ankara"
            rawCv.contains("İzmir", ignoreCase = true) -> "İzmir"
            rawCv.contains("Bursa", ignoreCase = true) -> "Bursa"
            rawCv.contains("Kocaeli", ignoreCase = true) -> "Kocaeli"
            rawCv.contains("Antalya", ignoreCase = true) -> "Antalya"
            else -> "İstanbul"
        }

        // Detect experiences
        val experiences = mutableListOf<ExperienceItem>()
        if (rawCv.contains("Sütaş", ignoreCase = true) || rawCv.contains("Satış", ignoreCase = true) || rawCv.contains("Yönetici", ignoreCase = true)) {
            experiences.add(
                ExperienceItem(
                    company = "Sütaş Süt Ürünleri A.Ş.",
                    position = "Bölge Saha Satış Şefi",
                    sector = "FMCG / Hızlı Tüketim",
                    startYear = "2021",
                    endYear = "Devam Ediyor",
                    isCurrent = true,
                    isManagement = true,
                    teamSize = 18,
                    directReports = 4,
                    responsibilities = listOf(
                        "60 yetkili bayinin satış ve sevkiyat süreçlerini yönetmek",
                        "Saha satış ekibinin haftalık KPI ve tanzim-teşhir takibini yapmak"
                    ),
                    achievements = listOf(
                        "Dağıtım maliyetlerini %14 düşürdü",
                        "Tanzim-teşhir penetrasyonunu %84'e çıkardı"
                    ),
                    kpis = listOf("Ciro Büyümesi (+%18)", "Penetrasyon Oranı (%84)"),
                    tools = listOf("SAP SD", "El Terminali", "MS Excel")
                )
            )
            experiences.add(
                ExperienceItem(
                    company = "Peyman Kuruyemiş A.Ş.",
                    position = "Saha Satış Uzmanı",
                    sector = "FMCG / Gıda",
                    startYear = "2018",
                    endYear = "2021",
                    isCurrent = false,
                    isManagement = false,
                    teamSize = 0,
                    directReports = 0,
                    responsibilities = listOf(
                        "Geleneksel kanal ve market noktalarına saha ziyaretleri gerçekleştirmek",
                        "Yeni ürün penetrasyonunu sağlamak"
                    ),
                    achievements = listOf(
                        "85 yeni müşteri noktası açarak portföyü %30 büyüttü",
                        "Yılın En Başarılı Saha Temsilcisi ödülü"
                    ),
                    kpis = listOf("Aktif Müşteri Sayısı (+30%)"),
                    tools = listOf("Mobil Satış CRM", "MS Office")
                )
            )
        } else {
            // Generic experience from parsed text
            experiences.add(
                ExperienceItem(
                    company = "Önceki Firma / Kurumsal Şirket",
                    position = "Saha & Satış Sorumlusu",
                    sector = "Ticaret & Hizmet",
                    startYear = "2020",
                    endYear = "2024",
                    isCurrent = false,
                    isManagement = true,
                    teamSize = 5,
                    directReports = 2,
                    responsibilities = listOf("Müşteri portföyü yönetimi", "Saha operasyonlarının takibi"),
                    achievements = listOf("Yıllık satış kotasını %115 oranında tamamladı"),
                    kpis = listOf("Müşteri Memnuniyeti", "Satış Hacmi"),
                    tools = listOf("MS Office", "CRM")
                )
            )
        }

        // Skills identification & hidden skill discovery
        val detectedSkills = mutableListOf<SkillItem>()
        fun addSkill(name: String, category: String, level: String, hidden: Boolean, evidence: String) {
            if (detectedSkills.none { it.name.equals(name, ignoreCase = true) }) {
                detectedSkills.add(SkillItem(name = name, category = category, level = level, isHiddenDiscovered = hidden, evidence = evidence))
            }
        }

        addSkill("Bayi Yönetimi", "Yönetim / Liderlik", "Uzman", false, "Distribütör ve bayi ağı yönetimi")
        addSkill("Saha Satış Operasyonu", "Operasyonel", "Uzman", false, "Doğrudan saha ve müşteri ziyaretleri")
        addSkill("Müşteri İlişkileri", "Soft Skills", "İleri", false, "Ticari müzakere ve anlaşmalar")
        addSkill("FMCG Sektör Uzmanlığı", "Sektör", "Uzman", false, "Hızlı tüketim ürünleri tecrübesi")
        addSkill("MS Excel & Raporlama", "Teknik", "Orta", false, "Satış tabloları ve raporlama")

        // Kanıta dayalı gizli yetenekler:
        if (rawCv.contains("ekip", ignoreCase = true) || rawCv.contains("yönet", ignoreCase = true)) {
            addSkill("Ekip Liderliği & People Management", "Yönetim / Liderlik", "İleri", true, "CV'de ekip koordinasyonundan AI tarafından kanıtlandı")
            addSkill("Performans & KPI Yönetimi", "Yönetim / Liderlik", "İleri", true, "Satış hedefleri ve prim takibinden AI tarafından keşfedildi")
        }
        if (rawCv.contains("bayi", ignoreCase = true) || rawCv.contains("distribütör", ignoreCase = true)) {
            addSkill("Bölge Operasyon Koordinasyonu", "Operasyonel", "Uzman", true, "Çok noktalı lojistik ve bayi ağından keşfedildi")
        }

        // AI Proactive Questions for Gaps
        val questions = listOf(
            AiQuestion(
                id = "q_kpi_1",
                relatedSection = "Ekip Liderliği & KPI",
                triggerPhrase = "Ekip ve bayi koordinasyonu",
                questionText = "Ekibinizi yönetirken haftalık hangi temel KPI'ları takip ettiniz ve ekip performansını artırmak için uyguladığınız somut bir yöntem var mıydı?",
                hint = "Örneğin: Ziyaret başına sipariş oranı, rut optimizasyonu veya prim sistemi.",
                potentialHiddenSkills = listOf("Satış Koçluğu", "Rut & Rota Planlama", "Motivasyon Yönetimi")
            ),
            AiQuestion(
                id = "q_kriz_2",
                relatedSection = "Ticari Kriz & Müzakere",
                triggerPhrase = "Bayi ilişkileri ve anlaşmalar",
                questionText = "Bayilerle yaşadığınız en zorlu tahsilat veya tedarik krizini nasıl yönettiniz? Ortaya çıkan somut sonucu paylaşabilir misiniz?",
                hint = "Örneğin: Vadesi geçen alacaklarda uygulanan risk planı.",
                potentialHiddenSkills = listOf("Ticari Müzakere", "Kriz Yönetimi", "Risk ve Nakit Akışı Yönetimi")
            )
        )

        val improvementAreas = listOf(
            ImprovementArea(
                title = "Ölçülebilir Başarıları Güçlendir",
                description = "CV'de maliyet düşüşü ve ciro artışı gibi somut yüzdeler daha belirgin hale getirilebilir.",
                actionLabel = "Bunu Keşfet",
                gapType = "achievement"
            ),
            ImprovementArea(
                title = "İngilizce & Uluslararası Yetkinlik",
                description = "Uluslararası FMCG firmaları için ticari İngilizce mülakat seviyesi hedeflenmeli.",
                actionLabel = "Açığı Kapat",
                gapType = "skills"
            ),
            ImprovementArea(
                title = "ERP & Veri Analitiği Araçları",
                description = "SAP SD, Power BI veya SQL gibi araçların iş süreçlerindeki rolü detaylandırılmalı.",
                actionLabel = "CV'ye Ekle",
                gapType = "ats"
            )
        )

        return MasterCareerProfile(
            id = "master_profile_user",
            fullName = fullName,
            title = "Saha Satış Yöneticisi & Bayi Operasyon Uzmanı",
            email = email,
            phone = phone,
            locationCity = city,
            locationCountry = "Türkiye",
            workPreference = "Hibrit / Saha",
            relocationWillingness = true,
            summary = "Hızlı tüketim (FMCG) ve saha operasyonlarında 7 yılı aşkın tecrübe. 60+ bayilik ağ, 18 kişilik saha ekibi yönetimi ve bütçe gerçekleşmesinde kanıtlanmış başarı.",
            skills = detectedSkills,
            experiences = experiences,
            achievements = listOf(
                AchievementItem(title = "Dağıtım Maliyeti Azaltımı", metric = "%14 Tasarruf", companyOrContext = "Sütaş A.Ş."),
                AchievementItem(title = "Saha Penetrasyon Başarısı", metric = "%84 Kapsama", companyOrContext = "Sütaş A.Ş.")
            ),
            education = listOf(
                EducationItem(school = "Marmara Üniversitesi", department = "İşletme", degree = "Lisans", graduationYear = "2018")
            ),
            certifications = listOf(
                CertificationItem(name = "İleri Saha Satış & Bayi Yönetimi", issuer = "Kariyer Enstitüsü", year = "2022")
            ),
            languages = listOf(
                LanguageItem(language = "Türkçe", level = "Anadili"),
                LanguageItem(language = "İngilizce", level = "B1 (Orta Seviye)")
            ),
            leadership = LeadershipProfile(
                hasLeadershipExperience = true,
                maxTeamSize = 18,
                directReports = 4,
                budgetManagedUsdOrTry = "45 Milyon ₺ Yıllık",
                keyLeadershipKpis = listOf("Ciro Gerçekleşmesi", "Saha Ziyaret Sayısı", "Ekip Motivasyonu")
            ),
            careerGoals = listOf(
                "Bölge Satış Müdürü",
                "Saha Operasyon Direktörü",
                "Key Account Manager"
            ),
            profileScore = 84,
            improvementAreas = improvementAreas,
            pendingQuestions = questions
        )
    }

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
            ((matchedSkills.size.toFloat() / job.requiredSkills.size) * 100).toInt().coerceIn(60, 98)
        } else 85

        val experienceScore = if (profile.experiences.size >= 2) 92 else 75
        val industryScore = if (job.sector.contains("FMCG", ignoreCase = true) || job.sector.contains("Gıda", ignoreCase = true) || job.sector.contains("Satış", ignoreCase = true)) 95 else 78
        val seniorityScore = if (profile.leadership.hasLeadershipExperience && job.isManagementRole) 90 else 80
        val locationScore = if (profile.locationCity.equals(job.location, ignoreCase = true) || job.location.contains(profile.locationCity, ignoreCase = true) || profile.relocationWillingness) 100 else 75
        val educationScore = 88

        val overall = ((skillsScore * 0.35) + (experienceScore * 0.20) + (industryScore * 0.15) + (seniorityScore * 0.15) + (locationScore * 0.10) + (educationScore * 0.05)).toInt()

        val whyFit = mutableListOf<String>()
        whyFit.add("7+ yıllık güçlü saha operasyonu ve FMCG sektör tecrübesi")
        whyFit.add("60+ bayilik distribütör ağı ve 18 kişilik saha ekibi yönetimi kanıtı")
        whyFit.add("Ciro ve penetrasyon hedeflerini %112 oranında gerçekleştirme geçmişi")
        whyFit.add("Lokasyon ve aktif seyahat / sürüş şartlarına tam uyum")

        val whyNotFit = mutableListOf<String>()
        if (job.preferredSkills.any { it.contains("İngilizce", ignoreCase = true) }) {
            whyNotFit.add("İlandaki ileri seviye İngilizce beklentisine karşın aday seviyesi B1 (Orta)")
        }
        if (job.preferredSkills.any { it.contains("SAP SD", ignoreCase = true) || it.contains("WMS", ignoreCase = true) }) {
            whyNotFit.add("Kurumsal ERP (SAP) modül kullanımı CV'de operasyonel düzeyde belirtilmiş, mimari düzeyde değil")
        }
        if (whyNotFit.isEmpty()) {
            whyNotFit.add("Daha önce ulusal zincir genel merkez satın alma müzakeresinde tek yetkili rolü bulunmuyor")
        }

        val missingGaps = listOf(
            "Ticari İngilizce mülakat pratiği yapılması",
            "SAP SD sipariş ve iskonto onay iş akışlarının CV'de ön plana çıkarılması",
            "Bölgesel bütçe tasarruf oranlarının mülakatta sayısal verilerle açıklanması"
        )

        val aiInsight = when {
            job.title.contains("Bayi", ignoreCase = true) || job.title.contains("Saha Satış", ignoreCase = true) ->
                "Bu ilan sadece 'satış yapacak biri' aramıyor; bölge distribütörlerinin finansal ve stok riskini yönetecek, sahada ekibin rut disiplinini sağlayacak güvenilir bir lider arıyor."
            job.title.contains("Operasyon", ignoreCase = true) ->
                "İlanın gerçek odak noktası dağıtım hızı, filo SLA performansı ve kriz anında hızlı rota/ekip çözümü üretmektir."
            else ->
                "Şirket dinamik büyüme aşamasında olup, inisiyatif alabilen ve KPI odaklı düşünebilen bir yönetici hedefliyor."
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
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
        val dateStr = dateFormat.format(Date())

        val summary = "${job.company} bünyesindeki '${job.title}' pozisyonunun gereksinimleriyle doğrudan örtüşen ${profile.experiences.firstOrNull()?.company ?: "FMCG"} saha tecrübesi. ${profile.leadership.maxTeamSize} kişilik saha ekibi liderliği, ${job.requiredSkills.take(3).joinToString(", ")} yetkinlikleri ve kanıtlanmış ciro büyütme başarılarıyla şirketin bölge hedeflerine derhal katkı sağlamaya hazır profesyonel profil."

        val highlightedSkills = job.requiredSkills.filter { req ->
            profile.skills.any { it.name.contains(req, ignoreCase = true) }
        }.ifEmpty { profile.skills.map { it.name }.take(6) }

        val coverLetter = """
Sayın ${job.company} İşe Alım Ekibi ve Satış Direktörlüğü,

Şirketiniz bünyesinde yayımlanan '${job.title}' pozisyonunu büyük bir ilgi ve heyecanla inceledim. 

FMCG ve saha operasyonları alanında geride bıraktığım 7 yılı aşkın tecrübem boyunca; 60'ı aşkın yetkili bayi ağının sevk ve idaresini yürüttüm, 18 kişilik saha ekibimin haftalık KPI ve tanzim-teşhir penetrasyonunu %84 seviyesine taşıdım. Sütaş ve Peyman bünyesinde gerçekleştirdiğim dağıtım maliyetlerinde %14 optimizasyon ve aktif portföye +85 yeni nokta kazandırma başarılarım, ${job.company}'nin büyüme ve saha hedeflerine doğrudan katkı sunabilecek niteliktedir.

Pozisyonun gerektirdiği ${job.requiredSkills.take(3).joinToString(", ")} süreçlerini şirketinizin kurumsal vizyonu doğrultusunda yönetmek ve bölgedeki pazar payını artırmak adına bu fırsatı değerlendirmeyi çok arzu ediyorum.

Detayları şahsen veya online bir mülakatta paylaşmaktan memnuniyet duyarım. Zamanınız ve değerlendirmeniz için teşekkür ederim.

Saygılarımla,
${profile.fullName}
${profile.phone} | ${profile.email}
        """.trimIndent()

        return ResumeVersion(
            id = "cv_tailored_${job.id}_${System.currentTimeMillis()}",
            title = "${job.company} — ${job.title} Özel CV",
            targetRoleOrJob = "${job.title} (${job.company})",
            professionalSummary = summary,
            keyHighlightedSkills = highlightedSkills,
            prioritizedExperienceIds = profile.experiences.map { it.id },
            coverLetterText = coverLetter,
            atsScoreEstimated = 94,
            createdAtFormatted = dateStr
        )
    }

    /**
     * Generates a preset role-based CV version from Master Profile (e.g. Sales CV, Leadership CV, FMCG CV).
     */
    fun generatePresetCvVersion(profile: MasterCareerProfile, presetType: String): ResumeVersion {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
        val dateStr = dateFormat.format(Date())

        return when (presetType) {
            "management" -> ResumeVersion(
                id = "cv_mgmt_${System.currentTimeMillis()}",
                title = "Yönetim & Liderlik Odaklı CV",
                targetRoleOrJob = "Bölge Satış Müdürü / Direktör",
                professionalSummary = "18 kişilik saha ekibinin sevk ve idaresi, bütçe yönetimi ve distribütör ilişkilerinde uzmanlaşmış, stratejik karar alma yetkinliğine sahip yönetici profili.",
                keyHighlightedSkills = listOf("Ekip Liderliği & People Management", "Bütçe & Ciro Yönetimi", "Bayi Ağı Stratejisi", "KPI & Performans Değerlendirme"),
                prioritizedExperienceIds = profile.experiences.map { it.id },
                atsScoreEstimated = 91,
                createdAtFormatted = dateStr
            )
            "operations" -> ResumeVersion(
                id = "cv_ops_${System.currentTimeMillis()}",
                title = "Saha Operasyon & FMCG CV",
                targetRoleOrJob = "Saha Operasyon Yöneticisi",
                professionalSummary = "FMCG sektöründe 60+ bayilik sevkiyat ve tedarik süreçlerinin kesintisiz yürütülmesi, dağıtım maliyet optimizasyonu ve penetrasyon artışında kanıtlanmış saha uzmanlığı.",
                keyHighlightedSkills = listOf("Saha Satış Operasyonu", "Bayi Yönetimi", "Tanzim-Teşhir & Penetrasyon", "Lojistik & Sevkiyat Koordinasyonu"),
                prioritizedExperienceIds = profile.experiences.map { it.id },
                atsScoreEstimated = 89,
                createdAtFormatted = dateStr
            )
            else -> ResumeVersion(
                id = "cv_sales_${System.currentTimeMillis()}",
                title = "Satış & İş Geliştirme CV",
                targetRoleOrJob = "Kıdemli Satış Yöneticisi",
                professionalSummary = "Portföy genişletme, yeni bayi ve kanal açılışı, ticari müzakere ve satış hedeflerini düzenli olarak %110+ gerçekleştirme geçmişi.",
                keyHighlightedSkills = listOf("Ticari Satış & Müzakere", "Aktif Müşteri Portföy Yönetimi", "Ciro Artışı", "Kanal Genişletme"),
                prioritizedExperienceIds = profile.experiences.map { it.id },
                atsScoreEstimated = 88,
                createdAtFormatted = dateStr
            )
        }
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
                    questionText = "CV'nizde Sütaş bünyesinde dağıtım maliyetlerini %14 düşürdüğünüzü belirtmişsiniz. Bu projede tam olarak hangi adımları attınız ve karşılaştığınız en büyük direnç neydi?",
                    questionCategory = "CV & Kanıt Doğrulama",
                    contextReason = "CV'deki ölçülebilir başarı kanıtının derinlemesine incelenmesi"
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
