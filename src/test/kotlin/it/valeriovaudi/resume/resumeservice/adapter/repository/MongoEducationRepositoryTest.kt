package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import org.bson.Document
import org.junit.Assert
import org.junit.Ignore
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
import java.time.LocalDate
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoEducationRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    lateinit var mongoEducationRepository: MongoEducationRepository

    @Test
    @Ignore
    fun `save a new education`() {

        mongoEducationRepository = MongoEducationRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val education = Education(title = "A_EDUCATION_TITLE", dateFrom = LocalDate.MAX, type = EducationType.HING_SCOOL)
        mongoEducationRepository.save(resumeId, education).toMono().block(Duration.ofMinutes(1 ))

        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "education")
                .block(Duration.ofMinutes(2)))
    }
}