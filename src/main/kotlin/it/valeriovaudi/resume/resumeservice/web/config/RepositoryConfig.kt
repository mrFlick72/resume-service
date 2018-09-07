package it.valeriovaudi.resume.resumeservice.web.config

import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoPersonalDetailsRepository
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoSkillsRepository
import org.springframework.context.support.beans

object RepositoryConfig {

    fun beans() = beans {
        bean<MongoSkillsRepository>()
        bean<MongoPersonalDetailsRepository>()
        bean<MongoResumeRepository>()
    }
}