package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.resume.resumeservice.domain.repository.SkillsRepository
import org.reactivestreams.Publisher

class MongoSkillsRepository : SkillsRepository {
    override fun findOne(resumeId: String): Publisher<Skill> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findOne(resumeId: String, skillFamily: String): Publisher<Skill> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(resumeId: String, skill: Skill): Publisher<Skill> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(resumeId: String, skillFamily: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}