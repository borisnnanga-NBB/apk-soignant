package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Int)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_entries ORDER BY id ASC")
    fun getAllScheduleEntries(): Flow<List<ScheduleEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleEntry(entry: ScheduleEntry)

    @Update
    suspend fun updateScheduleEntry(entry: ScheduleEntry)

    @Delete
    suspend fun deleteScheduleEntry(entry: ScheduleEntry)

    @Query("DELETE FROM schedule_entries WHERE id = :id")
    suspend fun deleteScheduleEntryById(id: Int)
}

@Dao
interface InquiryDao {
    @Query("SELECT * FROM patient_inquiries ORDER BY timestamp DESC")
    fun getAllInquiries(): Flow<List<PatientInquiry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: PatientInquiry)

    @Update
    suspend fun updateInquiry(inquiry: PatientInquiry)

    @Query("DELETE FROM patient_inquiries WHERE id = :id")
    suspend fun deleteInquiryById(id: Int)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntry)
}
