package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.adapter.persistance.PersonalDetailsPersistanceModel
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto.Companion.emptyPersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.model.Sex
import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toMono
import java.time.Duration
import java.time.LocalDate
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.test.annotation.DirtiesContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


@DataMongoTest
@RunWith(SpringRunner::class)
class MongoPersonalDetailsRepositoryTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    lateinit var gridFsTemplate: GridFsTemplate

    lateinit var mongoPersonalDetailsRepository: MongoPersonalDetailsRepository
    @Autowired
    var gridFsOperations: GridFsOperations? = null

    @Before
    fun setUp() {
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate, gridFsTemplate)
    }


    @Test
    fun `save a personal details without photo`() {

        val resumeId = UUID.randomUUID().toString()

        val personalDetails = PersonalDetails(emptyPersonalDetailsPhoto(),
                "Valerio", "Vaudi", "Ennio Ferrari 30 street",
                "22100", "Como", "Como", "valerio.vaudi@gmail.com", "",
                LocalDate.now(), "Italy", Sex.M, "")

        mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                .toMono().blockOptional().ifPresent {
                    Assert.assertThat(it, `is`(personalDetails))
                }

        println("details")
        Assert.assertNotNull(mongoTemplate.findOne(query(Criteria.where("_id").`is`(resumeId)),
                PersonalDetailsPersistanceModel::class.java)
                .block(Duration.ofMinutes(2)))

        println("photo")
        Assert.assertNull(gridFsTemplate.findOne(query(Criteria.where("metadata.resume-id").`is`(resumeId))))
    }

    @Test
    fun `save a personal details with photo`() {
        val resumeId = UUID.randomUUID().toString()

        this::class.java.classLoader.getResourceAsStream("barca-a-vela.jpg")
                .use {
                    val personalDetails = PersonalDetails(PersonalDetailsPhoto(it.readAllBytes(), "barca-a-vela", "jpg"),
                            "Valerio", "Vaudi", "Ennio Ferrari 30 street",
                            "22100", "Como", "Como", "valerio.vaudi@gmail.com", "",
                            LocalDate.now(), "Italy", Sex.M, "")

                    mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                            .toMono().blockOptional().ifPresent {
                                Assert.assertThat(it, `is`(personalDetails))
                            }


                    println("details")
                    Assert.assertNotNull(mongoTemplate.findOne(query(Criteria.where("_id").`is`(resumeId)),
                            PersonalDetailsPersistanceModel::class.java)
                            .block(Duration.ofMinutes(2)))

                    println("photo")
                    Assert.assertNotNull(gridFsTemplate.findOne(query(Criteria.where("metadata.resume-id").`is`(resumeId))))
                    Assert.assertNotNull(gridFsTemplate.getResource(resumeId))
                }
    }

    @Test
    fun `find a personal details with photo`() {
        val resumeId = UUID.randomUUID().toString()

        this::class.java.classLoader.getResourceAsStream("barca-a-vela.jpg")
                .use {
                    val personalDetails = PersonalDetails(PersonalDetailsPhoto(it.readAllBytes(), "barca-a-vela", "jpg"),
                            "Valerio", "Vaudi", "Ennio Ferrari 30 street",
                            "22100", "Como", "Como", "valerio.vaudi@gmail.com", "",
                            LocalDate.now(), "Italy", Sex.NONE, "")

                    mongoPersonalDetailsRepository.save(resumeId, personalDetails)
                            .toMono().blockOptional().ifPresent {
                                Assert.assertThat(it, `is`(personalDetails))
                            }

                    val actual = mongoPersonalDetailsRepository.findOne(resumeId)
                            .toMono().block(Duration.ofMinutes(2))

                    println(actual!!.photo.content.size)
                    println(actual.photo.fileExtension)
                    println(actual.photo.fileName)
                    println(actual.firstName)
                }
    }
}