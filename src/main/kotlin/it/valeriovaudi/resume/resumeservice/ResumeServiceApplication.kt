package it.valeriovaudi.resume.resumeservice

import it.valeriovaudi.resume.resumeservice.web.config.RepositoryConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SpringBootApplication
class ResumeServiceApplication

fun main(args: Array<String>) {
    runApplication<ResumeServiceApplication>(*args) {
        addInitializers(RepositoryConfig.beans())
    }
}