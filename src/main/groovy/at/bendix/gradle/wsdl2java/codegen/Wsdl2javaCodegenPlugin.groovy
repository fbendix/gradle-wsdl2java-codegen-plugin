package at.bendix.gradle.wsdl2java.codegen

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class Wsdl2javaCodegenPlugin implements Plugin<Project> {

    private static final String NAME = 'wsdl2java'
    private static final String CONFIG_NAME_CXF = 'wsdl2javaCodegen_cxf'
    private static final String CONFIG_NAME_AXIS = 'wsdl2javaCodegen_axis'

    void apply(Project project) {
        project.plugins.apply('java')
        project.extensions.create(NAME, Wsdl2javaCodegenExtension, project)
        project.task('wsdl2javaCodegen', type: Wsdl2javaCodegenTask)


        project.with {

            compileJava.dependsOn += wsdl2javaCodegen
            compileJava.source wsdl2javaCodegen.outputs.files, sourceSets.main.java

            repositories {
                jcenter()
                mavenCentral()
            }

            configurations {
                wsdl2javaCodegen_cxf { description = "CXF wsdl2javaCodegen dependencies"; transitive = true; }
                wsdl2javaCodegen_axis
            }

            dependencies {
                wsdl2javaCodegen_cxf "org.apache.cxf:cxf-tools-wsdlto-core:2.7.0"
                wsdl2javaCodegen_cxf "org.apache.cxf:cxf-tools-wsdlto-frontend-jaxws:2.7.0"
                wsdl2javaCodegen_cxf "org.apache.cxf:cxf-tools-wsdlto-databinding-jaxb:2.7.0"

                wsdl2javaCodegen_axis 'org.apache.axis2:axis2:1.6.2'
                wsdl2javaCodegen_axis 'org.apache.axis2:axis2-xmlbeans:1.6.2'
                wsdl2javaCodegen_axis 'org.apache.xmlbeans:xmlbeans:2.6.0'
            }
        }
    }
}
