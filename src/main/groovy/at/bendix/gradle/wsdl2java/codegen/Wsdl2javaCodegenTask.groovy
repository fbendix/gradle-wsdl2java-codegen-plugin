package at.bendix.gradle.wsdl2java.codegen

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.io.output.TeeOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

class Wsdl2javaCodegenTask extends DefaultTask {

    Wsdl2javaCodegenTask() {
        group = "codegen"
        description = 'Run wsdl2java code generation'

        Wsdl2javaCodegenExtension wsdl2javaCodegenExt = project.wsdl2java

        wsdl2javaCodegenExt.cxfSpecs.each { Wsdl2javaSpec spec ->
            def wsdl = spec.inputFile
            def genSrcDir = spec.genSrcDir
            inputs.file wsdl
            outputs.dir genSrcDir
        }
        wsdl2javaCodegenExt.axisSpecs.each { Wsdl2javaSpec spec ->
            def wsdl = spec.inputFile
            def genSrcDir = spec.genSrcDir
            inputs.file wsdl
            outputs.dir genSrcDir
        }
    }

    @TaskAction
    public release() {
        Wsdl2javaCodegenExtension wsdl2javaCodegenExt = project.wsdl2java


        wsdl2javaCodegenExt.cxfSpecs.each { Wsdl2javaSpec spec ->
            def wsdl = spec.inputFile
            def genSrcDir = spec.genSrcDir

            def byteArrayOutputStream = new ByteArrayOutputStream()

            project.javaexec {
                classpath project.configurations.wsdl2javaCodegen_cxf
                main "org.apache.cxf.tools.wsdlto.WSDLToJava"
                args '-d', genSrcDir.toString(), wsdl.toString()
                errorOutput = new TeeOutputStream(System.err, byteArrayOutputStream)
            }

            def str = byteArrayOutputStream.toString()
            if (str.contains('Usage : wsdl2java') || str.contains('WSDLToJava Error')) {
                throw new TaskExecutionException(
                        project.tasks[name],
                        new IOException("WSDLToJava has failed. Please see output")
                )
            }
        }

        wsdl2javaCodegenExt.axisSpecs.each { Wsdl2javaSpec spec ->

            def wsdl = spec.inputFile
            def genSrcDir = spec.genSrcDir
            def databindingName = 'adb' // specify the binding type - xmlbeans, adb, or jibx
            def packageName = 'my.wsdl'

            def byteArrayOutputStream = new ByteArrayOutputStream()

            project.javaexec {
                classpath += project.configurations.wsdl2javaCodegen_axis
                main "org.apache.axis2.wsdl.WSDL2Java"
                args '-d', databindingName,
                        '-uri', wsdl.toString(),
                        '-o', genSrcDir.toString(),
                        '-p', packageName
                        '-or'
                    // '-ss',
                    // '-g',
                    // '-sd',
                errorOutput = new TeeOutputStream(System.err, byteArrayOutputStream)
            }

            def str = byteArrayOutputStream.toString()
            if (str.contains('Usage : wsdl2java') || str.contains('WSDLToJava Error')) {
                throw new TaskExecutionException(
                        project.tasks[name],
                        new IOException("WSDLToJava has failed. Please see output")
                )
            }
        }
    }
}




