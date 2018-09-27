package it.valeriovaudi.resume.resumeservice.web.config

import it.valeriovaudi.resume.resumeservice.adapter.repository.*
import org.springframework.context.support.beans

object RepositoryConfig {

    fun beans() = beans {
        bean<MongoSkillsRepository>()
        bean<MongoPersonalDetailsRepository>()
        bean<MongoResumeRepository>()
        bean<MongoWorkExperienceRepository>()
        bean<MongoEducationRepository>()
    }
}