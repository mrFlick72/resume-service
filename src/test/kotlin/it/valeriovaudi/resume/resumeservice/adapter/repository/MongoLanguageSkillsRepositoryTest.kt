package it.valeriovaudi.resume.resumeservice.adapter.repository

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.junit4.SpringRunner

@DataMongoTest
@RunWith(SpringRunner::class)
class MongoLanguageSkillsRepositoryTest {
    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    lateinit var mongoLanguageSkillsRepository: MongoLanguageSkillsRepository

}