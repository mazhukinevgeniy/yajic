package org.example.storage

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.Database
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class MetadataStorage(private val db: Database) {

    private val comparator = FileComparator()

    fun getFileComparison(fileName: String): FileComparison {
        val info = db.sourcesQueries.selectByCanonicalPath(fileName).executeAsOneOrNull()

        //TODO what if it's unknown? maybe we run differently
        //current flow is definitely unclean
        return comparator.makeComparison(fileName, info?.checkSum ?: 0, info?.size ?: 0)
    }

    fun update(updates: List<FileComparison>) {
        for (item in updates) {
            db.sourcesQueries.drop(item.name)
            db.sourcesQueries.insert(item.name, item.checksum, item.currentSize)
        }
    }

    companion object {
        private val lock = ReentrantReadWriteLock()

        private val databases = HashMap<String, Database>()
        private val collisionCheck = HashMap<Int, String>()

        private fun isAvailable(workDir: String): Boolean {
            if (workDir in databases) {
                return true
            }
            val hash = workDir.hashCode()
            check(hash !in collisionCheck || collisionCheck[hash] == workDir) {
                "can't open db for project, found collision"
            }
            return false
        }
        fun openProject(workDir: String): MetadataStorage {
            lock.read {
                if (isAvailable(workDir)) {
                    return MetadataStorage(databases[workDir]!!)
                }
            }
            lock.write {
                if (isAvailable(workDir)) {
                    return MetadataStorage(databases[workDir]!!)
                }
                collisionCheck[workDir.hashCode()] = workDir
                val driver = JdbcSqliteDriver("jdbc:sqlite:$workDir${File.separator}yajic_data.sql")
                Database.Schema.create(driver)
                databases[workDir] = Database(driver)
                return MetadataStorage(databases[workDir]!!)
            }
        }
    }
}
