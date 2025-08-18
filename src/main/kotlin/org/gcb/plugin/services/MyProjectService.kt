package org.gcb.plugin.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import org.gcb.plugin.MyBundle

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
}
