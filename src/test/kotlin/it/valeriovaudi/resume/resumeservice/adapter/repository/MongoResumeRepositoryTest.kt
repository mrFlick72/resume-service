package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import org.assertj.core.api.Assertions
import org.bson.Document
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toMono
import java.time.Duration
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoResumeRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate


    lateinit var mongoResumeRepository: MongoResumeRepository

    @Before
    fun setUp() {
        mongoResumeRepository = MongoResumeRepository(mongoTemplate)
    }

    @Test
    fun `save a resume`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume.emptyResume(resumeId, "A_USER", Language.EN)

        mongoResumeRepository.save(emptyResume).toMono().block(Duration.ofMinutes(1))

        val actualResumeDocument = mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                Document::class.java, "resume")
                .block(Duration.ofMinutes(2))

        Assert.assertNotNull(actualResumeDocument)
    }
}