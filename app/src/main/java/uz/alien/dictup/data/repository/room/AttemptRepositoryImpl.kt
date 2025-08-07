package uz.alien.dictup.data.repository.room

import uz.alien.dictup.data.local.room.attempt.AttemptDao
import uz.alien.dictup.data.mapper.toAttempt
import uz.alien.dictup.data.mapper.toAttemptEntity
import uz.alien.dictup.domain.model.Attempt
import uz.alien.dictup.domain.repository.room.AttemptRepository

class AttemptRepositoryImpl(
    private val attemptDao: AttemptDao
) : AttemptRepository {

    override suspend fun insertAttempt(attempt: Attempt) {
        attemptDao.insertAttempt(attempt.toAttemptEntity())
    }

    override suspend fun deleteAttempt(attempt: Attempt) {
        attemptDao.deleteAttempt(attempt.toAttemptEntity())
    }

    override suspend fun getAttemptByTimestamp(timestamp: Long): Attempt? {
        return attemptDao.getAttemptByTimestamp(timestamp)?.toAttempt()
    }

    override suspend fun getAllAttempts(): List<Attempt> {
        return attemptDao.getAllAttempts().map { it.toAttempt() }
    }

    override suspend fun clearAllAttempts() {
        attemptDao.clearAllAttempts()
    }
}