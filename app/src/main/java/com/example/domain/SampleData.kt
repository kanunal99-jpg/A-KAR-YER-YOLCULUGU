package com.example.domain

import com.example.data.model.*

object SampleData {

    val sampleJobs = listOf(
        JobListing(
            id = "job_fmcg_101",
            title = "Saha Satış Yöneticisi / Bayi Şefi",
            company = "ETİ Gıda San. ve Tic. A.Ş.",
            sector = "FMCG / Hızlı Tüketim",
            location = "İstanbul (Anadolu) / Kocaeli",
            salaryRange = "75.000 ₺ - 95.000 ₺ + Prim + Şirket Aracı",
            employmentType = "Tam Zamanlı (Saha)",
            seniorityLevel = "Yönetici",
            description = "Bölgedeki distribütör ve bayi ağının yönetilmesi, satış hedeflerinin ve tanzim-teşhir KPI'larının gerçekleştirilmesi, saha satış ekibinin koordine edilmesi.",
            duties = listOf(
                "Sorumlu olunan bölgedeki 45+ yetkili bayinin satış ve stok hedeflerini yönetmek",
                "8 kişilik saha satış temsilcisi ekibini motive etmek ve haftalık KPI takibi yapmak",
                "Horeca ve geleneksel kanal penetrasyonunu artıracak ticari aksiyonları planlamak",
                "Bütçe ve iskonto kontrolünü şirket finans politikalarına uygun yürütmek"
            ),
            requiredSkills = listOf(
                "Bayi Yönetimi",
                "Ekip Liderliği",
                "Saha Operasyonu",
                "FMCG Deneyimi",
                "KPI & Bütçe Yönetimi",
                "B Sınıfı Ehliyet & Aktif Sürüş"
            ),
            preferredSkills = listOf(
                "SAP SD Modülü",
                "Horeca Kanalı Tecrübesi",
                "İngilizce (Orta/İleri)"
            ),
            requiredExperienceYears = 5,
            isManagementRole = true,
            jobUrl = "https://kariyer.etigida.com.tr/ilan/saha-satis-yoneticisi",
            source = "Resmi Kariyer Portalı",
            publishedDate = "Bugün",
            riskLevel = JobRiskLevel.LOW,
            riskReason = "Resmi ETİ Kariyer Portalı ve ATS sistemi üzerinden doğrulanmış kurumsal ilan."
        ),
        JobListing(
            id = "job_ecom_102",
            title = "Bölge Operasyon & Lojistik Süpervizörü",
            company = "Trendyol Group",
            sector = "E-Ticaret & Lojistik",
            location = "İstanbul / Kocaeli",
            salaryRange = "65.000 ₺ - 85.000 ₺ + Yemek Kartı + Yan Haklar",
            employmentType = "Tam Zamanlı (Hibrit)",
            seniorityLevel = "Süpervizör",
            description = "Trendyol Express dağıtım merkezleri operasyonlarının sevk ve idaresi, son kilometre (last-mile) dağıtım kurye ve araç filosu yönetimi, SLA ve teslimat hızının optimize edilmesi.",
            duties = listOf(
                "Dağıtım merkezi günlük paket işleme kapasitesini takip etmek ve darboğazları çözmek",
                "35 kişilik kurye ve filo ekibinin vardiya ve rota verimliliğini yönetmek",
                "Müşteri memnuniyeti (CSAT) ve zamanında teslimat (OTD) metriklerini raporlamak",
                "Saha kaza ve hasar oranlarını minimum seviyede tutacak eğitimleri organize etmek"
            ),
            requiredSkills = listOf(
                "Operasyon Yönetimi",
                "Filo & Kurye Koordinasyonu",
                "SLA & KPI Takibi",
                "Vardiya Planlama",
                "Problem Çözme"
            ),
            preferredSkills = listOf(
                "SQL / Excel İleri Seviye",
                "Depo Yönetim Sistemleri (WMS)",
                "Yalın Üretim / 5S"
            ),
            requiredExperienceYears = 3,
            isManagementRole = true,
            jobUrl = "https://careers.trendyol.com/job/operations-supervisor",
            source = "LinkedIn",
            publishedDate = "Dün",
            riskLevel = JobRiskLevel.LOW,
            riskReason = "Trendyol Kurumsal LinkedIn hesabı tarafından ilan edilmiş doğrulanmış pozisyon."
        ),
        JobListing(
            id = "job_tech_103",
            title = "Kıdemli Mobil Yazılım Mühendisi (Android)",
            company = "Getir Teknoloji",
            sector = "Yazılım & Teknoloji",
            location = "İstanbul (Uzaktan / Hibrit)",
            salaryRange = "110.000 ₺ - 150.000 ₺ + Hisse Opsiyonu",
            employmentType = "Uzaktan / Esnek",
            seniorityLevel = "Uzman / Kıdemli",
            description = "Milyonlarca kullanıcıya hizmet veren Getir Android uygulamasının modern Jetpack Compose ve Kotlin Coroutines mimarisiyle geliştirilmesi ve performans optimizasyonu.",
            duties = listOf(
                "Jetpack Compose, Clean Architecture ve MVI desenleriyle yüksek performanslı UI bileşenleri yazmak",
                "Uygulama açılış süresi ve bellek tüketimini optimize etmek",
                "Junior ve mid-level mühendislere mentorluk ve code review yapmak",
                "CI/CD otomasyonu ve modüler mimari geliştirmelerine liderlik etmek"
            ),
            requiredSkills = listOf(
                "Kotlin",
                "Jetpack Compose",
                "Coroutines & Flow",
                "Clean Architecture",
                "Unit & UI Testing",
                "İngilizce (İleri)"
            ),
            preferredSkills = listOf(
                "KMM (Kotlin Multiplatform)",
                "Gradle Optimization",
                "CI/CD Pipeline"
            ),
            requiredExperienceYears = 4,
            isManagementRole = false,
            jobUrl = "https://jobs.lever.co/getir/senior-android-engineer",
            source = "Resmi Kariyer Portalı",
            publishedDate = "3 gün önce",
            riskLevel = JobRiskLevel.LOW,
            riskReason = "Getir Lever kurumsal sayfası üzerinden doğrulanmış aktif ilan."
        ),
        JobListing(
            id = "job_retail_104",
            title = "Mağazalar Bölge Satış Müdürü",
            company = "Migros Ticaret A.Ş.",
            sector = "Perakende",
            location = "Bursa / Balıkesir / Çanakkale",
            salaryRange = "85.000 ₺ - 115.000 ₺ + Prim + Şirket Aracı",
            employmentType = "Tam Zamanlı (Saha)",
            seniorityLevel = "Yönetici",
            description = "Bölgedeki 28 Migros ve MM Migros mağazasının ciro, karlılık, müşteri memnuniyeti ve stok yönetimi hedeflerine ulaşmasını sağlamak.",
            duties = listOf(
                "28 mağazanın aylık ve yıllık satış bütçelerini takip etmek ve ciro artırıcı aksiyonlar almak",
                "Mağaza yöneticilerinin işe alım, performans değerlendirme ve kariyer gelişimlerini yönetmek",
                "Fire ve kayıp oranlarını şirket hedefleri dahilinde tutmak",
                "Müşteri şikayet ve denetim süreçlerini koordine etmek"
            ),
            requiredSkills = listOf(
                "Perakende Mağazacılık",
                "Bölge Satış Yönetimi",
                "Ciro & Karlılık KPI",
                "Ekip Liderliği",
                "Stok & Fire Yönetimi"
            ),
            preferredSkills = listOf(
                "SAP ERP",
                "Kategori Yönetimi"
            ),
            requiredExperienceYears = 6,
            isManagementRole = true,
            jobUrl = "https://kariyer.migros.com.tr/ilan/bolge-muduru",
            source = "Kariyer.net",
            publishedDate = "4 gün önce",
            riskLevel = JobRiskLevel.LOW,
            riskReason = "Migros İK onaylı doğrudan kurumsal iş ilanı."
        ),
        JobListing(
            id = "job_fmcg_105",
            title = "Key Account Manager (Ulusal Zincirler)",
            company = "Unilever Türkiye",
            sector = "FMCG / Hızlı Tüketim",
            location = "İstanbul (Ataşehir)",
            salaryRange = "95.000 ₺ - 130.000 ₺ + Yıllık Bonus",
            employmentType = "Hibrit",
            seniorityLevel = "Yönetici",
            description = "Ulusal market zincirleri (BİM, A101, Şok, Migros) ile yıllık ticari anlaşmaların yapılması, kategori büyüme stratejilerinin uygulanması ve JBP (Joint Business Plan) süreçlerinin yönetilmesi.",
            duties = listOf(
                "Atanan ulusal zincir müşterilerinde yıllık satış ve kar marjı hedeflerini tutturmak",
                "Ticari pazarlama ile ortak promosyon ve raf payı planları oluşturmak",
                "Tahsilat risklerini sıfıra indirmek ve tedarik zinciriyle stok uyumunu sağlamak"
            ),
            requiredSkills = listOf(
                "Ulusal Zincir Yönetimi (KAM)",
                "Ticari Müzakere & Sözleşme",
                "FMCG Kategori Yönetimi",
                "Joint Business Planning (JBP)",
                "İngilizce (İleri)"
            ),
            preferredSkills = listOf(
                "Nielsen / Kantar Veri Analitiği",
                "SAP R/3"
            ),
            requiredExperienceYears = 5,
            isManagementRole = true,
            jobUrl = "https://careers.unilever.com/turkey/key-account-manager",
            source = "Resmi Kariyer Portalı",
            publishedDate = "5 gün önce",
            riskLevel = JobRiskLevel.LOW,
            riskReason = "Unilever Global Careers resmi portalı ilanı."
        )
    )

    val sampleInitialProfile = MasterCareerProfile(
        id = "master_profile_user",
        fullName = "Emre Karahan",
        title = "Saha Satış Yöneticisi & Bayi Operasyon Uzmanı",
        email = "emre.karahan@example.com",
        phone = "+90 532 555 01 92",
        locationCity = "İstanbul",
        locationCountry = "Türkiye",
        workPreference = "Hibrit / Saha",
        relocationWillingness = true,
        summary = "Hızlı tüketim (FMCG) ve perakende sektöründe 7 yılı aşkın saha deneyimi. 60+ bayilik ağın yönetimi, 18 kişilik saha satış ekibinin liderliği ve yıllık 45M ₺ ciro bütçesinin takibi konusunda kanıtlanmış başarı.",
        skills = listOf(
            SkillItem(name = "Bayi Yönetimi", category = "Yönetim / Liderlik", level = "Uzman", isHiddenDiscovered = false, evidence = "60+ bayilik ağın sevk ve idaresi"),
            SkillItem(name = "Saha Satış & Operasyon", category = "Operasyonel", level = "Uzman", isHiddenDiscovered = false, evidence = "Geleneksel kanal ve süpermarket saha ziyareti"),
            SkillItem(name = "Ekip Liderliği & Koçluk", category = "Yönetim / Liderlik", level = "İleri", isHiddenDiscovered = true, evidence = "18 kişilik saha ekibinin günlük yönetimi"),
            SkillItem(name = "KPI & Ciro Yönetimi", category = "Yönetim / Liderlik", level = "İleri", isHiddenDiscovered = true, evidence = "Hedef/gerçekleşen takibi ve aylık prim bütçelemesi"),
            SkillItem(name = "Müşteri & Ticari İlişkiler", category = "Soft Skills", level = "Uzman", isHiddenDiscovered = false, evidence = "Bölge distribütörleriyle anlaşma yenileme"),
            SkillItem(name = "FMCG Sektör Uzmanlığı", category = "Sektör", level = "Uzman", isHiddenDiscovered = false, evidence = "7 yıllık gıda ve içecek saha tecrübesi"),
            SkillItem(name = "MS Excel & Raporlama", category = "Teknik", level = "Orta", isHiddenDiscovered = false, evidence = "Haftalık saha satış tabloları")
        ),
        experiences = listOf(
            ExperienceItem(
                company = "Sütaş Süt Ürünleri A.Ş.",
                position = "Bölge Saha Satış Şefi",
                sector = "FMCG / Gıda",
                startYear = "2021",
                endYear = "Devam Ediyor",
                isCurrent = true,
                isManagement = true,
                teamSize = 18,
                directReports = 4,
                responsibilities = listOf(
                    "Marmara Bölgesi'ndeki 60 yetkili bayinin sipariş, sevkiyat ve tahsilat süreçlerini yönetmek",
                    "18 satış temsilcisinin haftalık rota planlaması ve hedef gerçekleşmelerini takip etmek",
                    "Aylık ciro hedefini ortalama %112 oranında tutturmak"
                ),
                achievements = listOf(
                    "Yeni bayi konsolidasyon projesiyle dağıtım maliyetlerini %14 düşürdü",
                    "Bölge genelinde tanzim-teşhir penetrasyonunu 6 ayda %68'den %84'e çıkardı",
                    "18 kişilik ekip için performans karnesi (KPI) sistemini kurarak çalışan sirkülasyonunu %25 azalttı"
                ),
                kpis = listOf("Ciro Büyümesi (+%18)", "Penetrasyon Oranı (%84)", "Tahsilat Vadesi (38 gün)"),
                tools = listOf("SAP SD", "El Terminali (Saha Otomasyonu)", "MS Excel")
            ),
            ExperienceItem(
                company = "Peyman Kuruyemiş A.Ş.",
                position = "Saha Satış Uzmanı",
                sector = "FMCG / Atıştırmalık",
                startYear = "2018",
                endYear = "2021",
                isCurrent = false,
                isManagement = false,
                teamSize = 0,
                directReports = 0,
                responsibilities = listOf(
                    "Geleneksel kanal ve yerel zincir mağazalara düzenli ziyaretler gerçekleştirmek",
                    "Yeni ürün lansmanlarının rafta bulunurluğunu sağlamak",
                    "İskonto ve tanzim-teşhir malzemelerinin sahada uygulanmasını denetlemek"
                ),
                achievements = listOf(
                    "Sorumlu olunan bölgede 85 yeni bakkal ve yerel market noktası açarak aktif nokta sayısını %30 artırdı",
                    "2019 Yılı En Başarılı Saha Satış Temsilcisi ödülü aldı"
                ),
                kpis = listOf("Aktif Müşteri Sayısı (+30%)", "Yeni Ürün Lansman Satışı"),
                tools = listOf("Mobil Satış CRM", "MS Office")
            )
        ),
        achievements = listOf(
            AchievementItem(
                title = "Dağıtım Maliyeti Optimizasyonu",
                metric = "%14 Maliyet Azalması",
                companyOrContext = "Sütaş A.Ş."
            ),
            AchievementItem(
                title = "Saha Penetrasyon Artışı",
                metric = "%68'den %84'e Artış",
                companyOrContext = "Sütaş A.Ş."
            ),
            AchievementItem(
                title = "Aktif Müşteri Portföy Genişlemesi",
                metric = "+85 Yeni Nokta",
                companyOrContext = "Peyman A.Ş."
            )
        ),
        education = listOf(
            EducationItem(
                school = "Marmara Üniversitesi",
                department = "İktisadi ve İdari Bilimler Fakültesi — İşletme",
                degree = "Lisans",
                graduationYear = "2018"
            )
        ),
        certifications = listOf(
            CertificationItem(name = "İleri Saha Satış ve Bayi Yönetimi", issuer = "Kariyer Enstitüsü", year = "2022"),
            CertificationItem(name = "Temel Finans & Bütçe Yönetimi", issuer = "PwC Akademi", year = "2023")
        ),
        languages = listOf(
            LanguageItem(language = "Türkçe", level = "Anadili"),
            LanguageItem(language = "İngilizce", level = "B1 (Orta Seviye)")
        ),
        leadership = LeadershipProfile(
            hasLeadershipExperience = true,
            maxTeamSize = 18,
            directReports = 4,
            budgetManagedUsdOrTry = "45 Milyon ₺ / Yıllık Ciro Bütçesi",
            keyLeadershipKpis = listOf("Ekip Ciro Gerçekleşmesi", "Saha Ziyaret Verimliliği", "Personel Bağlılığı")
        ),
        careerGoals = listOf(
            "Bölge Satış Müdürü",
            "Saha Operasyon Direktörü",
            "Key Account Manager (FMCG Ulusal Zincirler)"
        ),
        profileScore = 82,
        improvementAreas = listOf(
            ImprovementArea(
                title = "Bütçe ve Finansal Metrikleri Netleştir",
                description = "Yönettiğiniz 45M ₺ bütçede maliyet tasarrufu ve kar marjı etkisini daha somut ölçülebilir hale getirebiliriz.",
                actionLabel = "Bunu Keşfet",
                gapType = "achievement"
            ),
            ImprovementArea(
                title = "İngilizce Yetkinliğini İş Ortamına Taşı",
                description = "Unilever ve uluslararası FMCG pozisyonları için B1 seviyesini ticari mülakat düzeyine çıkarmak avantaj sağlayacaktır.",
                actionLabel = "Eksik Analizi",
                gapType = "skills"
            ),
            ImprovementArea(
                title = "SAP ERP & CRM Deneyimini Vurgula",
                description = "CV'de SAP SD ve el terminali kullanımı sadece araç olarak geçmiş, süreç yönetimindeki rolü öne çıkarılmalı.",
                actionLabel = "CV'yi Güçlendir",
                gapType = "ats"
            )
        ),
        pendingQuestions = listOf(
            AiQuestion(
                id = "q_1",
                relatedSection = "Liderlik & Ekip Büyüklüğü",
                triggerPhrase = "18 kişilik saha ekibinin günlük yönetimi",
                questionText = "18 kişilik saha ekibinizi yönetirken haftalık hangi temel KPI'ları takip ediyordunuz ve ekip performansını artırmak için uyguladığınız somut bir yöntem var mıydı?",
                hint = "Örneğin: Ziyaret başına satış tutarı, tanzim teşhir denetim puanı veya prim sistemi",
                potentialHiddenSkills = listOf("Performans Yönetimi", "Satış Koçluğu", "Motivasyon & KPI Tasarımı")
            ),
            AiQuestion(
                id = "q_2",
                relatedSection = "Bayi Yönetimi & Kriz Çözümü",
                triggerPhrase = "60 yetkili bayinin sipariş, sevkiyat ve tahsilat süreçlerini yönetmek",
                questionText = "Bayilerle yaşadığınız en kritik ticari veya tahsilat krizini nasıl çözdünüz? Somut bir örnek verebilir misiniz?",
                hint = "Örneğin: Vadesi geçen bir alacakta uyguladığınız takas, teminat veya iskonto planı",
                potentialHiddenSkills = listOf("Ticari Müzakere", "Kriz Yönetimi", "Risk ve Tahsilat Yönetimi")
            )
        )
    )
}
