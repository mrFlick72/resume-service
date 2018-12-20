package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageCapabilityLevel.A1
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
import reactor.core.publisher.toMono
import java.time.Duration
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoLanguageSkillsRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    lateinit var mongoLanguageSkillsRepository: MongoLanguageSkillsRepository

    @Test
    fun `save language skills in the resume`() {
        mongoLanguageSkillsRepository = MongoLanguageSkillsRepository(mongoTemplate)
        val resumeId = UUID.randomUUID().toString()
        val save = mongoLanguageSkillsRepository.save(resumeId, LanguageSkills("A_NATIVE_LANGUAGE",
                listOf(LanguageSkill("A_LANGUAGE",
                        Understanding(A1, A1),
                        Speaking(A1, A1), A1))))
                .toMono().block(Duration.ofMinutes(1))


        Assert.assertNotNull(save)
        Assert.assertNotNull(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)), Document::class.java, "languageSkill")
                .block(Duration.ofMinutes(1)))
    }

    @Test
    fun `find language skills details`() {
        mongoLanguageSkillsRepository = MongoLanguageSkillsRepository(mongoTemplate)
        val resumeId = UUID.randomUUID().toString()
        val expected = mongoLanguageSkillsRepository.save(resumeId, LanguageSkills("A_NATIVE_LANGUAGE",
                listOf(LanguageSkill("A_LANGUAGE",
                        Understanding(LanguageCapabilityLevel.B1, LanguageCapabilityLevel.B1),
                        Speaking(LanguageCapabilityLevel.B1, LanguageCapabilityLevel.B1), LanguageCapabilityLevel.B1))))
                .toMono().block(Duration.ofMinutes(1))

        val actual = mongoLanguageSkillsRepository.findOne(resumeId).toMono().block(Duration.ofMinutes(1))
        Assert.assertThat(expected, Is.`is`(actual))
    }
}