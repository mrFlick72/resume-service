package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import org.reactivestreams.Publisher

class MongoResumeRepository : ResumeRepository {
    override fun findOne(resumeId: String): Publisher<Resume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findOneByUserName(userName: String, language: Language): Publisher<Resume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(resume: Resume): Publisher<Resume> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(resumeId: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}