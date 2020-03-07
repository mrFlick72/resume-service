package it.valeriovaudi.resume.resumeservice.adapter.usecase.printer.pdf

import it.valeriovaudi.resume.resumeservice.TestCase
import it.valeriovaudi.resume.resumeservice.TestableS3AsyncClient
import it.valeriovaudi.resume.resumeservice.TestableS3AsyncClient.bucket
import it.valeriovaudi.resume.resumeservice.adapter.repository.*
import it.valeriovaudi.resume.resumeservice.domain.model.Language
import it.valeriovaudi.resume.resumeservice.domain.model.Resume
import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.toMono
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.util.*

@DataMongoTest
@RunWith(SpringRunner::class)
class PdfResumePrinterTest {

    @Autowired
    lateinit var mongoTemplate: ReactiveMongoTemplate

    lateinit var mongoResumeRepository: MongoResumeRepository

    lateinit var mongoPersonalDetailsRepository: MongoPersonalDetailsRepository

    lateinit var mongoSkillsRepository: MongoSkillsRepository

    lateinit var mongoEducationRepository: MongoEducationRepository

    lateinit var mongoWorkExperienceRepository: MongoWorkExperienceRepository

    lateinit var pdfResumePrinter: PdfResumePrinter

    @Before
    fun setUp() {
        mongoWorkExperienceRepository = MongoWorkExperienceRepository(mongoTemplate)
        mongoEducationRepository = MongoEducationRepository(mongoTemplate)
        mongoSkillsRepository = MongoSkillsRepository(mongoTemplate)
        mongoPersonalDetailsRepository = MongoPersonalDetailsRepository(mongoTemplate, bucket, TestableS3AsyncClient.s3AsyncClient())
        mongoResumeRepository = MongoResumeRepository(mongoTemplate,
                mongoPersonalDetailsRepository,
                mongoSkillsRepository,
                mongoEducationRepository,
                mongoWorkExperienceRepository)

        pdfResumePrinter = PdfResumePrinter(mongoResumeRepository,
                PersonalDetailsPdfSectionProducer(),
                EducationsPdfSectionProducer(),
                SkillsPdfSectionProducer(),
                WorkExperiencePdfSectionProducer())
    }

    @Test
    fun `new resume as pdf`() {
        val resumeId = UUID.randomUUID().toString()
        val resume = TestCase.aResume(resumeId)!!
        mongoResumeRepository.save(resume).toMono().block(Duration.ofMinutes(1))

        val inputStream = pdfResumePrinter.printResumeFor(resumeId)

        Files.write(Paths.get(UUID.randomUUID().toString() + ".pdf"), inputStream.block()!!)

    }
}