package it.valeriovaudi.resume.resumeservice.web.config

import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoPersonalDetailsRepository
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import org.springframework.context.support.beans

object RepositoryConfig {

    fun beans() = beans {
        bean<MongoPersonalDetailsRepository>()
        bean<MongoResumeRepository>()
    }
}