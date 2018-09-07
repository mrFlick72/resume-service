package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.resume.resumeservice.domain.repository.SkillsRepository
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono

class MongoSkillsRepository(private val mongoTemplate: ReactiveMongoTemplate) : SkillsRepository {

    companion object {
        fun collectionName() = "skill"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").`is`(resumeId))

    }

    override fun findOne(resumeId: String): Publisher<Skill> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findOne(resumeId: String, skillFamily: String): Publisher<Skill> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(resumeId: String, skill: Skill): Publisher<Skill> =
            mongoTemplate.upsert(findOneQuery(resumeId = resumeId),
                    Update.fromDocument(SkillMapper.fromDomainToDocument(resumeId, skill)),
                    collectionName())
                    .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }
                    .map { skill }

    override fun delete(resumeId: String, skillFamily: String): Publisher<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}