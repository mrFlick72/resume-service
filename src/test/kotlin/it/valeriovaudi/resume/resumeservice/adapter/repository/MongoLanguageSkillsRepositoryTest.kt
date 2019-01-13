package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.LanguageCapabilityLevel.*
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkill
import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import it.valeriovaudi.resume.resumeservice.domain.model.Speaking
import it.valeriovaudi.resume.resumeservice.domain.model.Understanding
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


        val document = mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)), Document::class.java, "languageSkill")
                .block(Duration.ofMinutes(1))
        Assert.assertNotNull(save)
        Assert.assertNotNull(document)

        println(document)
    }

    @Test
    fun `find one language skill set`() {
        val expected = LanguageSkills("A_NATIVE_LANGUAGE",
                listOf(LanguageSkill("A_LANGUAGE",
                        Understanding(B1, C1),
                        Speaking(A1, C1), C1)))

        mongoLanguageSkillsRepository = MongoLanguageSkillsRepository(mongoTemplate)
        val resumeId = UUID.randomUUID().toString()
        mongoLanguageSkillsRepository.save(resumeId, expected)
                .toMono().block(Duration.ofMinutes(1))


        val actual = mongoLanguageSkillsRepository.findOne(resumeId).toMono().block(Duration.ofMinutes(1))

        Assert.assertNotNull(actual)
        Assert.assertThat(actual, Is.`is`(expected))
    }

    @Test
    fun `find language skills details`() {
        mongoLanguageSkillsRepository = MongoLanguageSkillsRepository(mongoTemplate)
        val resumeId = UUID.randomUUID().toString()
        val expected = mongoLanguageSkillsRepository.save(resumeId, LanguageSkills("A_NATIVE_LANGUAGE",
                listOf(LanguageSkill("A_LANGUAGE",
                        Understanding(B1, B1),
                        Speaking(B1, B1), B1))))
                .toMono().block(Duration.ofMinutes(1))

        val actual = mongoLanguageSkillsRepository.findOne(resumeId).toMono().block(Duration.ofMinutes(1))
        Assert.assertThat(expected, Is.`is`(actual))
    }

    @Test
    fun `delete language skills details`() {
        mongoLanguageSkillsRepository = MongoLanguageSkillsRepository(mongoTemplate)
        val resumeId = UUID.randomUUID().toString()
        val expected = mongoLanguageSkillsRepository.save(resumeId, LanguageSkills("A_NATIVE_LANGUAGE",
                listOf(LanguageSkill("A_LANGUAGE",
                        Understanding(B1, B1),
                        Speaking(B1, B1), B1))))
                .toMono().block(Duration.ofMinutes(1))

        var actual = mongoLanguageSkillsRepository.findOne(resumeId).toMono().block(Duration.ofMinutes(1))
        Assert.assertThat(expected, Is.`is`(actual))

        mongoLanguageSkillsRepository.delete(resumeId).toMono().block(Duration.ofMinutes(1))

        actual = mongoLanguageSkillsRepository.findOne(resumeId).toMono().block(Duration.ofMinutes(1))
        Assert.assertNull(actual)
    }
}