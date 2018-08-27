package it.valeriovaudi.resume.resumeservice

import it.valeriovaudi.resume.resumeservice.web.config.RepositoryConfig
import it.valeriovaudi.resume.resumeservice.web.route.PersonalDetailsRoute
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ResumeServiceApplication

fun main(args: Array<String>) {
    runApplication<ResumeServiceApplication>(*args) {
        addInitializers(RepositoryConfig.beans())
        addInitializers(PersonalDetailsRoute.routes())
    }
}

