package it.valeriovaudi.todolist

import it.valeriovaudi.resume.resumeservice.web.config.RepositoryConfig
import it.valeriovaudi.resume.resumeservice.web.route.PersonalDetailsRoute
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext

class TestContextInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(applicationContext: GenericApplicationContext) {
        RepositoryConfig.beans().initialize(applicationContext)
        PersonalDetailsRoute.routes().initialize(applicationContext)
    }
}