package org.example.analyzer

import org.objectweb.asm.*
import java.io.FileInputStream


data class ClassSignatures(
    val published: List<String>,
    val used: List<String>
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
        val publishedApi = ArrayList<String>()
        val usedApi = ArrayList<String>()

        val implemented = ArrayList<String>()

        //todo minor optimization - don't track self-use

        classReader.accept(object : ClassVisitor(Opcodes.ASM9) {
            private lateinit var currentClass: String

            override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
            ) {
                requireNotNull(name) { "unexpected null class name in classReader for ${classReader.className}" }
                currentClass = name

                implemented.addAll(interfaces ?: emptyArray())
                if (access and Opcodes.ACC_INTERFACE != 0) {
                    // for now reuse Dependency data model for interface-implementor relation
                    publishedApi.add(name)
                }

                super.visit(version, access, name, signature, superName, interfaces)
            }

            //TODO clean up overrides
            override fun visitMethod(
                access: Int,
                name: String,
                desc: String?,
                signature: String?,
                exceptions: Array<String?>?
            ): MethodVisitor? {
                //TODO: does it work for internal/protected?
                if ((Opcodes.ACC_PUBLIC and access) > 0) {
                    publishedApi.add("$currentClass.$name.$desc")
                }

                //TODO fix Extractor api
                return object : MethodVisitor(Opcodes.ASM9) {
                    override fun visitInsn(opcode: Int) {
                        super.visitInsn(opcode)
                    }

                    override fun visitIntInsn(opcode: Int, operand: Int) {
                        super.visitIntInsn(opcode, operand)
                    }

                    override fun visitVarInsn(opcode: Int, varIndex: Int) {
                        super.visitVarInsn(opcode, varIndex)
                    }

                    override fun visitTypeInsn(opcode: Int, type: String?) {
                        super.visitTypeInsn(opcode, type)
                    }

                    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
                        super.visitFieldInsn(opcode, owner, name, descriptor)
                    }

                    override fun visitMethodInsn(
                        opcode: Int,
                        owner: String?,
                        name: String?,
                        descriptor: String?,
                        isInterface: Boolean
                    ) {
                        //TODO when is isInterface useful?
                        usedApi.add("$owner.$name.$descriptor")
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                    }

                    override fun visitInvokeDynamicInsn(
                        name: String?,
                        descriptor: String?,
                        bootstrapMethodHandle: Handle?,
                        vararg bootstrapMethodArguments: Any?
                    ) {
                        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
                    }

                    override fun visitJumpInsn(opcode: Int, label: Label?) {
                        super.visitJumpInsn(opcode, label)
                    }

                    override fun visitLdcInsn(value: Any?) {
                        super.visitLdcInsn(value)
                    }

                    override fun visitIincInsn(varIndex: Int, increment: Int) {
                        super.visitIincInsn(varIndex, increment)
                    }

                    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {
                        super.visitTableSwitchInsn(min, max, dflt, *labels)
                    }

                    override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {
                        super.visitLookupSwitchInsn(dflt, keys, labels)
                    }

                    override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {
                        super.visitMultiANewArrayInsn(descriptor, numDimensions)
                    }

                    override fun visitInsnAnnotation(
                        typeRef: Int,
                        typePath: TypePath?,
                        descriptor: String?,
                        visible: Boolean
                    ): AnnotationVisitor {
                        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible)
                    }
                }
            }
        }, 0)
        return ClassSignatures(publishedApi, usedApi.plus(implemented))
    }
}
