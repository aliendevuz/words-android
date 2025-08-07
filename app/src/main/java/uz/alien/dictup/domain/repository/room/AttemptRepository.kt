package uz.alien.dictup.domain.repository.room

import uz.alien.dictup.domain.model.Attempt

interface AttemptRepository {

    suspend fun insertAttempt(attempt: Attempt)

    suspend fun deleteAttempt(attempt: Attempt)

    suspend fun getAttemptByTimestamp(timestamp: Long): Attempt?

    suspend fun getAllAttempts(): List<Attempt>

    suspend fun clearAllAttempts()
}