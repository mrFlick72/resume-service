package it.valeriovaudi.resume.resumeservice.web.config

import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoPersonalDetailsRepository
import org.springframework.context.support.beans

object RepositoryConfig {

    fun beans() = beans {
        bean<MongoPersonalDetailsRepository>()
    }
}