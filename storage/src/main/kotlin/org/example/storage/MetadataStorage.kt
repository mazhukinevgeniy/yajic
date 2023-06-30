package org.example.storage

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.Database
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


data class FileMetadata(
    val checksum: Long,
    val size: Long
)

class MetadataStorage(private val db: Database) {

    fun getFileMetadata(fileName: String): FileMetadata {
        val stored = db.sourcesQueries.selectByCanonicalPath(fileName).executeAsOneOrNull()
        return FileMetadata(stored?.checkSum ?: 0, stored?.size ?: 0)
    }

    fun getClassApis(className: String): List<String> {
        return db.apisQueries.selectByClass(className).executeAsList()
    }

    fun getDependencies(publicApis: List<String>): Set<String> {
        val result = HashSet<String>()

        for (api in publicApis) {
            result.addAll(db.dependenciesQueries.selectByApi(api).executeAsList())
        }

        return result
    }


    fun updateFileMetadata(updates: Map<String, FileMetadata>) {
        for ((name, data) in updates) {
            db.sourcesQueries.drop(name)
            db.sourcesQueries.insert(name, data.checksum, data.size)
        }
    }

    fun updateClassApis(className: String, toInsert: Set<String>) {
        db.apisQueries.dropClass(className)
        for (item in toInsert) {
            db.apisQueries.insert(className, item)
        }
        //TODO this is inefficient, do multi-row insertions/removals
    }

    fun updateDependencies(className: String, toInsert: Set<String>) {
        db.dependenciesQueries.dropClass(className)
        for (item in toInsert) {
            db.dependenciesQueries.insert(className, item)
        }
    }

    companion object {
        // if we make a YAJIC daemon, it might be good to cache sql connections

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
