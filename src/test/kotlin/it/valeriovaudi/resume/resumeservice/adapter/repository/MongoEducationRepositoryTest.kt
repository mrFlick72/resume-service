package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import org.bson.Document
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toFlux
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
    fun `save a new education`() {

        mongoEducationRepository = MongoEducationRepository(mongoTemplate)

        val resumeId = UUID.randomUUID().toString()
        val education = Education(id="id", title = "A_EDUCATION_TITLE", dateFrom = LocalDate.of(2018, 1, 1), type = EducationType.HING_SCOOL)
        mongoEducationRepository.save(resumeId, education).toMono().block(Duration.ofMinutes(1))

        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                Document::class.java, "education")
                .block(Duration.ofMinutes(2)))
    }

    @Test
    fun `findAll education items of a resume`() {
        mongoEducationRepository = MongoEducationRepository(mongoTemplate)

        val education1 = Education(id = "id1", title = "A_EDUCATION_TITLE", dateFrom = LocalDate.of(2018, 1, 1), dateTo = LocalDate.of(2018, 2, 1), type = EducationType.HING_SCOOL)
        val education2 = Education(id = "id2", title = "A_EDUCATION_TITLE2", dateFrom = LocalDate.of(2018, 1, 1), type = EducationType.HING_SCOOL)
        val resumeId = UUID.randomUUID().toString()

        mongoEducationRepository.save(resumeId, education1).toMono().block(Duration.ofMinutes(1))
        mongoEducationRepository.save(resumeId, education2).toMono().block(Duration.ofMinutes(1))

        val educationList = mongoEducationRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(2))

        Assert.assertNotNull(educationList)
        Assert.assertThat((educationList as MutableList).size, Is.`is`(2))
    }

    @Test
    fun `delete an education title from a resume`() {
        mongoEducationRepository = MongoEducationRepository(mongoTemplate)

        val education1 = Education(id = "id1", title = "A_EDUCATION_TITLE", dateFrom = LocalDate.of(2018, 1, 1), dateTo = LocalDate.of(2018, 2, 1), type = EducationType.HING_SCOOL)
        val education2 = Education(id = "id2", title = "A_EDUCATION_TITLE2", dateFrom = LocalDate.of(2018, 1, 1), type = EducationType.HING_SCOOL)
        val resumeId = UUID.randomUUID().toString()

        mongoEducationRepository.save(resumeId, education1).toMono().block(Duration.ofMinutes(1))
        mongoEducationRepository.save(resumeId, education2).toMono().block(Duration.ofMinutes(1))

        val educationList = mongoEducationRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(2))

        Assert.assertNotNull(educationList)
        Assert.assertThat((educationList as MutableList).size, Is.`is`(2))

        mongoEducationRepository.delete(resumeId, education1.id).toMono().block(Duration.ofMinutes(1))
        mongoEducationRepository.delete(resumeId, education2.id).toMono().block(Duration.ofMinutes(1))

        val deletedEducationList = mongoEducationRepository.findAll(resumeId).toFlux().collectList()
                .block(Duration.ofMinutes(2))

        Assert.assertNotNull(educationList)
        Assert.assertThat((deletedEducationList as MutableList).size, Is.`is`(0))
    }
}