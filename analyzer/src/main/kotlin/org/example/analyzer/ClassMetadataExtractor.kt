package org.example.analyzer

import org.objectweb.asm.*
import java.io.FileInputStream


data class ClassSignatures(
    val published: Set<String>,
    val used: Set<String>
)

class ClassMetadataExtractor {

    fun extractSignatures(classFile: String): ClassSignatures {
        val reader = ClassReader(FileInputStream(classFile))

        val signatures = getSignatures(reader)

        println("\nused: $classFile")
        for (api in signatures.used) {
            println(api)
        }
        println("own: $classFile")
        for (api in signatures.published) {
            println(api)
        }

        return signatures
    }

    private fun getSignatures(classReader: ClassReader): ClassSignatures {
        val publishedApi = HashSet<String>()
        val usedApi = HashSet<String>()

        classReader.accept(ExtractingClassVisitor(publishedApi, usedApi, classReader, allowInnerClass = true), 0)
        return ClassSignatures(publishedApi, usedApi)
    }

    private class ExtractingClassVisitor(
        val published: MutableSet<String>,
        val used: MutableSet<String>,
        val reader: ClassReader,
        val allowInnerClass: Boolean
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
            super.visitInnerClass(name, outerName, innerName, access)

            if (allowInnerClass) {
                reader.accept(ExtractingClassVisitor(published, used, reader, allowInnerClass = false), 0)
                //TODO suspicious, must test cases with multiple inner classes and other shenanigans
            }
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
