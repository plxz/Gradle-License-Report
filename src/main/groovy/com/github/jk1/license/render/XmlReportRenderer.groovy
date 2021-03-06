package com.github.jk1.license.render

import com.github.jk1.license.ImportedModuleData
import com.github.jk1.license.LicenseReportPlugin.LicenseReportExtension
import com.github.jk1.license.ModuleData
import com.github.jk1.license.ProjectData
import org.gradle.api.Project

/**
 * Renders dependency report in the following XML notation:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <chapter title="Libraries" id="Libs">
 *   <table>
 *     <tr>
 *       <td>Project</td>
 *       <td>Version</td>
 *       <td>License</td>
 *     </tr>
 *     <tr>
 *       <td><a href="http://commons.apache.org/exec/">commons-exec</a></td>
 *       <td>1.1</td>
 *       <td><a href="http://www.apache.org/licenses/LICENSE-2.0">Apache-2.0</a></td>
 *     </tr>
 *   </table>
 * </chapter>
 */
class XmlReportRenderer extends SingleInfoReportRenderer {

    private Project project
    private LicenseReportExtension config
    private File output

    def void render(ProjectData data) {
        project = data.project
        config = project.licenseReport
        output = new File(config.outputDir, 'index.xml')
        output.text = '<?xml version="1.0" encoding="UTF-8"?>\n'
        output << '<chapter title="Hub Back-end Libraries" id="Back_End_Libs">\n'
        output << '<table>\n'
        output << '<tr><td>Project</td><td>Version</td><td>License</td></tr>\n'
        data.configurations.collect { it.dependencies }.flatten().sort().each {
            printDependency(it)
        }
        data.importedModules.each { printImportedModule(it) }
        output << '</table>\n'
        output << '</chapter>'

    }

    private def void printDependency(ModuleData data) {
        def moduleName = "${data.group}:${data.name}"
        def moduleVersion = data.version
        def (String moduleUrl, String moduleLicense, String moduleLicenseUrl) = moduleLicenseInfo(config, data)

        output << "<tr>\n"
        if (moduleUrl) {
            output << "<td><a href='$moduleUrl'>$moduleName</a></td>\n"
        } else {
            output << "<td>$moduleName</td>\n"
        }
        output << "<td>$moduleVersion</td>\n"
        if (moduleLicense) {
            if (moduleLicenseUrl) {
                output << "<td><a href='$moduleLicenseUrl'>$moduleLicense</a></td>\n"
            } else {
                output << "<td>$moduleLicense</td>\n"
            }
        } else {
            output << '<td>No license information found</td>\n'
        }

        output << "</tr>\n"
    }

    private def void printImportedModule(ImportedModuleData data) {
        output << "<tr>\n"
        if (data.projectUrl) {
            output << "<td><a href='$data.projectUrl'>$data.name</a></td>\n"
        } else {
            output << "<td>$data.name</td>\n"
        }
        output << "<td>$data.version</td>\n"
        if (data.license) {
            if (data.licenseUrl) {
                output << "<td><a href='$data.licenseUrl'>$data.license</a></td>\n"
            } else {
                output << "<td>$data.license</td>\n"
            }
        } else {
            output << '<td>No license information found</td>\n'
        }

        output << "</tr>\n"
    }

}
