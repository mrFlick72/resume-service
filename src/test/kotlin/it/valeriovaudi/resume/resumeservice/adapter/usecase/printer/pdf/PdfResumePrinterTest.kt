package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoEducationRepository
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoPersonalDetailsRepository
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoResumeRepository
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoSkillsRepository
import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toMono
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class PdfResumePrinterTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    lateinit var gridFsTemplate: GridFsTemplate

    lateinit var mongoResumeRepository: MongoResumeRepository

    lateinit var mongoPersonalDetailsRepository: MongoPersonalDetailsRepository

    lateinit var mongoSkillsRepository: MongoSkillsRepository

    lateinit var mongoEducationRepository: MongoEducationRepository

    lateinit var pdfResumePrinter: PdfResumePrinter

    @Before
    fun setUp() {
        mongoEducationRepository = MongoEducationRepository(mongoTemplate)
        mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate, gridFsTemplate)
        mongoResumeRepository = MongoResumeRepository(mongoTemplate, mongoPersonalDetailsRepository, mongoSkillsRepository, mongoEducationRepository)

        pdfResumePrinter = PdfResumePrinter(mongoResumeRepository)
    }


    @Test
    fun `new resume as pdf`() {
        val resumeId = UUID.randomUUID().toString()
        val emptyResume = Resume(resumeId, "A_USER", Language.EN, TestCase.personalDetailsWithPhoto(), skill = listOf(Skill("Family 1", listOf("skill_1", "skill_2"))))

        mongoResumeRepository.save(emptyResume).toMono().block(Duration.ofMinutes(1))

        val inputStream = pdfResumePrinter.printResumeFor(resumeId)

        Files.write(Paths.get(UUID.randomUUID().toString() + ".pdf"), inputStream.readAllBytes())

    }
}