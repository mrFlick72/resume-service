package it.valeriovaudi.resume.resumeservice

import it.valeriovaudi.resume.resumeservice.web.config.PrinterConfig
import it.valeriovaudi.resume.resumeservice.web.config.RepositoryConfig
import org.bson.Document
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class ResumeServiceApplication

fun main(args: Array<String>) {
    runApplication<ResumeServiceApplication>(*args) {
        addInitializers(
                RepositoryConfig.beans(),
                PrinterConfig.beans()
        )
    }
}

fun Document.getStringOrDefault(key: String, defaultValue: String? = "") = Optional.ofNullable(this.getString(key)).orElse(defaultValue)