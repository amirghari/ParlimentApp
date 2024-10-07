package com.example.parlimentapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parlimentapp.data.database.ParliamentDatabase
import com.example.parlimentapp.network.NetworkModule
import com.example.parlimentapp.network.ParliamentApiService

class UpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val apiService: ParliamentApiService = NetworkModule.apiService
    private val dao = ParliamentDatabase.getDatabase(context).parliamentMemberDao()

    override suspend fun doWork(): Result {
        return try {
            val members = apiService.getParliamentMembers()
            dao.insertAllMembers(members)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
