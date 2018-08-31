package it.valeriovaudi.resume.resumeservice.domain.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import org.reactivestreams.Publisher

interface ResumeRepository {

    fun findOne(resumeId: String): Publisher<Resume>
    
    fun findOneByUserName(userName: String, language: Language): Publisher<Resume>
    
    fun save(resume: Resume): Publisher<Resume>

    fun delete(resumeId: String): Publisher<Unit>
}