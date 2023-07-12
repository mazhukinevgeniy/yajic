package org.example.analyzer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream


data class ClassSignatures(
    val published: Set<String>,
    val used: Set<String>
)

class ClassMetadataExtractor {

    fun extractSignatures(classFile: String): ClassSignatures {
        val classReader = ClassReader(FileInputStream(classFile))

        // for locating compiled inner classes
        val directory = classFile.substringBeforeLast(File.separatorChar)

        val publishedApi = HashSet<String>()
        val usedApi = HashSet<String>()

        classReader.accept(ExtractingClassVisitor(publishedApi, usedApi, directory), 0)
        return ClassSignatures(publishedApi, usedApi)
    }

    private inner class ExtractingClassVisitor(
        val published: MutableSet<String>,
        val used: MutableSet<String>,
        val directory: String
    ) : ClassVisitor(Opcodes.ASM9) {
        private lateinit var currentClass: String

        override fun visit(
            version: Int,
            access: Int,
            name: String?,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            requireNotNull(name)
            currentClass = name

            used.addAll(interfaces ?: emptyArray())
            if (access and Opcodes.ACC_INTERFACE != 0) {
                // for now reuse Dependency data model for interface-implementor relation
                published.add(name)
            }

            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
            val innerSignatures = extractSignatures("$directory${File.separator}$name.class")

            published.addAll(innerSignatures.published)
            used.addAll(innerSignatures.used)

            //this breaks the domain model a little bit, because "class" is most often used as "source file"

            super.visitInnerClass(name, outerName, innerName, access)
        }

        override fun visitMethod(
            access: Int,
            name: String,
            desc: String?,
            signature: String?,
            exceptions: Array<String?>?
        ): MethodVisitor? {
            if ((Opcodes.ACC_PUBLIC and access) > 0) {
                published.add("$currentClass.$name.$desc")
            }

            return object : MethodVisitor(Opcodes.ASM9) {
                override fun visitMethodInsn(
                    opcode: Int,
                    owner: String?,
                    name: String?,
                    descriptor: String?,
                    isInterface: Boolean
                ) {
                    used.add("$owner.$name.$descriptor")
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
            }
        }
    }
}
