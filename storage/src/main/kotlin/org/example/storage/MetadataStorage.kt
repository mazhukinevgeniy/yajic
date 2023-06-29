package org.example.storage

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.example.Database
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class MetadataStorage(private val db: Database) {

    private val comparator = FileComparator()

    //TODO should be 'getFileMetadata' with no comparator
    fun getFileComparison(fileName: String): FileComparison {
        val info = db.sourcesQueries.selectByCanonicalPath(fileName).executeAsOneOrNull()

        //TODO what if it's unknown? maybe we run differently
        //current flow is definitely unclean
        return comparator.makeComparison(fileName, info?.checkSum ?: 0, info?.size ?: 0)
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


    fun updateFileMetadata(updates: List<FileComparison>) {
        for (item in updates) {
            db.sourcesQueries.drop(item.name)
            db.sourcesQueries.insert(item.name, item.checksum, item.currentSize)
        }
    }

    fun updateClassApis(className: String, toDelete: List<String>, toInsert: List<String>) {
        for (item in toDelete) {
            db.apisQueries.drop(className, item)
        }
        for (item in toInsert) {
            db.apisQueries.insert(className, item)
        }
        //TODO this is inefficient, do multi-row insertions/removals
    }

    fun updateDependencies(className: String, toDelete: List<String>, toInsert: List<String>) {
        for (item in toDelete) {
            db.dependenciesQueries.drop(className, item)
        }
        for (item in toInsert) {
            db.dependenciesQueries.insert(className, item)
        }
    }

    companion object {
        //TODO just drop this nonsense? we aren't making the demonized version, it's okay to assume one project per one process
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
