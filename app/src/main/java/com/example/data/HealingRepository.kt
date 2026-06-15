package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HealingRepository(private val database: AppDatabase) {

    val allMedicines: Flow<List<Medicine>> = database.medicineDao().getAllMedicines()
    val allScheduleEntries: Flow<List<ScheduleEntry>> = database.scheduleDao().getAllScheduleEntries()
    val allInquiries: Flow<List<PatientInquiry>> = database.inquiryDao().getAllInquiries()
    val allOrders: Flow<List<OrderEntry>> = database.orderDao().getAllOrders()

    suspend fun insertMedicine(medicine: Medicine) {
        database.medicineDao().insertMedicine(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        database.medicineDao().updateMedicine(medicine)
    }

    suspend fun deleteMedicine(medicine: Medicine) {
        database.medicineDao().deleteMedicine(medicine)
    }

    suspend fun deleteMedicineById(id: Int) {
        database.medicineDao().deleteMedicineById(id)
    }

    suspend fun insertScheduleEntry(entry: ScheduleEntry) {
        database.scheduleDao().insertScheduleEntry(entry)
    }

    suspend fun updateScheduleEntry(entry: ScheduleEntry) {
        database.scheduleDao().updateScheduleEntry(entry)
    }

    suspend fun deleteScheduleEntry(entry: ScheduleEntry) {
        database.scheduleDao().deleteScheduleEntry(entry)
    }

    suspend fun deleteScheduleEntryById(id: Int) {
        database.scheduleDao().deleteScheduleEntryById(id)
    }

    suspend fun insertInquiry(inquiry: PatientInquiry) {
        database.inquiryDao().insertInquiry(inquiry)
    }

    suspend fun updateInquiry(inquiry: PatientInquiry) {
        database.inquiryDao().updateInquiry(inquiry)
    }

    suspend fun deleteInquiryById(id: Int) {
        database.inquiryDao().deleteInquiryById(id)
    }

    suspend fun insertOrder(order: OrderEntry) {
        database.orderDao().insertOrder(order)
    }

    // Seeds default traditional remedies and papa George's schedule if empty
    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentMedicines = allMedicines.first()
        if (currentMedicines.isEmpty()) {
            val defaultRemedies = listOf(
                Medicine(
                    name = "Écorce de Moabi",
                    description = "L'écorce sacrée de Moabi est reconnue pour ses propriétés anti-inflammatoires et son aide à la régénération cellulaire profonde. Idéale en décoction pour les douleurs articulaires chroniques.",
                    category = "Écorces",
                    price = 7500.0,
                    properties = "Anti-inflammatoire, Calmant",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDOzIzywZ52Rgvtew3ckp4wEBrmljSU1FSzuzJi6Y4kQgoqxwPemplxGGYP9OexXzTOBACeuwb4lk4X9zlQ6EyYIk6HfO-AAcJ3jVKfvBRUFCLiz2mpqMFdR-2Ln3He7e9uoo6Up6GXWSgZvMpevXzPLKOT8FOkiA_xmYqcGov2KEMAxCn2HcUUC0yN9GwyG3dH583_OGsCdZzHBdH3qLO6Zfo7ZgGfab3KTwT4C8HfdXfEryn1-kB2PdS5feXoxnOouj1u5n8A3CUN"
                ),
                Medicine(
                    name = "Miel de Forêt",
                    description = "Miel sauvage récolté dans les profondeurs forestières de l'Est, riche en pollen médicinal et enzymes protectrices.",
                    category = "Tout",
                    price = 4000.0,
                    properties = "Énergie, Fortifiant, Antiseptique",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDs1R9yGRnRYaB79kwVFOc4GW6twenzRNZBUoiE0kC2wYMPleeU4wh92l3XQ1FuuLtn3Bb2zA3m6cOJ7szT64RabQ8LKvDPHZPb7PBOh-rROk1vo5HVo1_PrdFlGmxFXYztbiUDNopY-y5HErFiRwURomCxS9v2PhuKdCjFD9fEapj0zXGX1wzE-tle_1VdMNCmNG-lBoc_43KjM4OMksIYuFcyiySnz3rkc_Mi8UxUlwKkA0U6AVu62eIk84JWkftA32pL6RN-psf9"
                ),
                Medicine(
                    name = "Racines de Djansang",
                    description = "Poudre pure de Djansang sauvage pour la fortification immunitaire, l'équilibre et l'énergie vitale quotidienne.",
                    category = "Racines",
                    price = 3500.0,
                    properties = "Immunité, Tonique, Digestif",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDldURZrfwAf7Pbk0CJURkBTlICtYlV5NHBO4LBfwPTs4CcxntjUowL3bBsJ0zUDHkbXwgkQAf6e3TzJapxPqf4gpu3DdFVvwU_wrUBMce-Y9UGrDzoPVzWr4V9NgK3we915SPynT1TZafD2BH4ySw2p5rblYSR9BtxTEPnOQutpbc8fzLHiZZ_ItaBeNJAu79irbRqst2YNMuxbDqVuDq89JfwCcU-iUYteNRPVZ4TjTvivxzFkh_1tMOnD9A-PoxBMJwZdCxvXvJa"
                ),
                Medicine(
                    name = "Thé de Mimboman",
                    description = "Mélange secret de Papa George pour purifier le sang, calmer l'anxiété et favoriser un sommeil réparateur profond.",
                    category = "Infusions",
                    price = 2200.0,
                    properties = "Dépuratif, Sommeil, Apaisant",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBrZsyEamHztgybCH00mc7oR2c-x_JPgSD_moRCQtN3HjGRZn2Iwyvmwu-QWFUnKdD9UdAwWYFkbwTOrsl1Oa5hLq2ZQ2qE-yvpXNsW3VfOXAnYHJ-AnYq4wLP37jJ0J7RAfvgKKLZFTO1HHAPpp614---75W7nGVGfJ7PirxHR412YAmahw967yx7LAeLf-bx_gTrnmqrdSYHn7Gp7T6jck9o4ib8n9tXeAhVQALHavFuEc0CFDyLPftnA9rZ9S-6XHgDgYeX-vfxy"
                ),
                Medicine(
                    name = "Sacred Bark Salve",
                    description = "Onguent de guérison traditionnel pour la peau, formulé avec des écorces antiseptiques de Mimboman et du beurre de karité bio.",
                    category = "Infusions",
                    price = 15000.0,
                    properties = "Cicatrisant, Peau, Antifongique",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCg8fHTMZuqwriohW0spdCOdZkhwPJvb--hHaKAunccGdr-RhxefWnLdR-OcpbI-6ba-r9wHgbYb3jD4cHBNt1DyGUrvksGPq03yi3nFR94ik9N13ESMfo7s4E178-oO89JHlGYZJ22rbJ8fSYojpqD9oTpn-ndM6s5nCLjm55K6rFf_G9bIT_XRTm37ImPWyNGooG7TnxGBghWYmkfhjyEH3hVjsloK6vGLyTlVf4vF2COQ65wS2TGLrdOky0RgCHaCXi7t_4dIFqj",
                    isStockLow = true
                )
            )
            for (med in defaultRemedies) {
                database.medicineDao().insertMedicine(med)
            }
        }

        val currentSchedule = allScheduleEntries.first()
        if (currentSchedule.isEmpty()) {
            val defaultSchedule = listOf(
                ScheduleEntry(
                    day = "Lundi",
                    place = "Mimboman",
                    reason = "Consultations générales",
                    durationString = "2h",
                    statusString = "Disponible"
                ),
                ScheduleEntry(
                    day = "Mardi",
                    place = "Mimboman",
                    reason = "Rituels de protection",
                    durationString = "2h",
                    statusString = "Occupé"
                ),
                ScheduleEntry(
                    day = "Mercredi",
                    place = "Sanctuaire",
                    reason = "Préparation remèdes",
                    durationString = "4h",
                    statusString = "Repos"
                ),
                ScheduleEntry(
                    day = "Jeudi",
                    place = "Mimboman",
                    reason = "Problèmes familiaux et spirituels",
                    durationString = "2h",
                    statusString = "Disponible"
                )
            )
            for (entry in defaultSchedule) {
                database.scheduleDao().insertScheduleEntry(entry)
            }
        }
        
        // Seed an initial inquiry for healer view to look beautifully populated with real messages
        val currentInquiries = allInquiries.first()
        if (currentInquiries.isEmpty()) {
            val defaultInquiries = listOf(
                PatientInquiry(
                    patientName = "Mamadou Koulibaly",
                    patientPhone = "+237699112233",
                    inquiryType = "Problème physique",
                    messageText = "J'ai d'intenses douleurs chroniques au dos suite à une mauvaise chute. Les traitements modernes n'ont rien donné.",
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    responseText = "Papa George vous attend ce mardi à 14:00 à l'entrée salle des fêtes Florencia."
                ),
                PatientInquiry(
                    patientName = "Lucie Diop",
                    patientPhone = "+237651887766",
                    inquiryType = "Maladie pathologique",
                    messageText = "Je viens pour un renouvellement de mon traitement pour réguler mon diabète avec les infusions de Mimboman.",
                    timestamp = System.currentTimeMillis() - 3600000 * 5
                ),
                PatientInquiry(
                    patientName = "Binta Keita",
                    patientPhone = "+237682443322",
                    inquiryType = "Problème spirituel",
                    messageText = "Bloquages à répétition dans mes affaires et cauchemars constants. J'ai besoin de rituels de bénédiction.",
                    timestamp = System.currentTimeMillis() - 3600000 * 24
                )
            )
            for (inq in defaultInquiries) {
                database.inquiryDao().insertInquiry(inq)
            }
        }
    }
}
