package at.bendix.gradle.wsdl2java.codegen

import org.gradle.api.Project

class Wsdl2javaCodegenExtension {

    List<Wsdl2javaSpec.Cxf> cxfSpecs = []
    List<Wsdl2javaSpec.Axis> axisSpecs = []

    Project project

    Wsdl2javaCodegenExtension(Project project) {
        this.project = project
    }

    def cxfSpec(Closure configureClosure) {
        Wsdl2javaSpec wsdl2javaSpec = new Wsdl2javaSpec.Cxf()
        configureClosure.resolveStrategy = Closure.DELEGATE_FIRST
        configureClosure.delegate = wsdl2javaSpec
        configureClosure()

        prepareSpec(wsdl2javaSpec)
        cxfSpecs << wsdl2javaSpec
    }

    def axisSpec(Closure configureClosure) {
        Wsdl2javaSpec wsdl2javaSpec = new Wsdl2javaSpec.Axis()
        configureClosure.resolveStrategy = Closure.DELEGATE_FIRST
        configureClosure.delegate = wsdl2javaSpec
        configureClosure()

        prepareSpec(wsdl2javaSpec)
        axisSpecs << wsdl2javaSpec
    }

    private def prepareSpec(Wsdl2javaSpec wsdl2javaSpec) {
        def sourceSetName = "generated-${wsdl2javaSpec.outputKey}"
        wsdl2javaSpec.genSrcDir = project.file("$project.projectDir/src/${sourceSetName}/java")

        wsdl2javaSpec.genSrcDir.mkdirs()
        project.with {
            sourceSets {
                "$sourceSetName" {
                    java {
                        srcDirs = [wsdl2javaSpec.genSrcDir.path]
                        compileClasspath += project.sourceSets.main.compileClasspath
                        runtimeClasspath += project.sourceSets.main.runtimeClasspath
                    }
                }
            }
        }
    }
}
