package it.valeriovaudi.resume.resumeservice.web.route

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoWorkExperienceRepository
import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import it.valeriovaudi.resume.resumeservice.extractId
import it.valeriovaudi.resume.resumeservice.web.representation.WorkExperienceRepresentation
import it.valeriovaudi.todolist.TestContextInitializer
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.toMono
import java.time.Duration
import java.time.LocalDate
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class WorkExperienceRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var workExperienceRepository: MongoWorkExperienceRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper;

    @Test
    @WithMockUser(username = "user")
    fun `save a new workexperience`() {
        val workExperienceRepresentation = WorkExperienceRepresentation(startDate = "2018-01-01", endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"))
        val resumeId = UUID.randomUUID().toString()

        val location = this.webClient.post()
                .uri("/resume/${resumeId}/work-experience")
                .body(BodyInserters.fromObject(workExperienceRepresentation))
                .exchange()
                .expectStatus().isCreated
                .returnResult<Any>().responseHeaders.location

        Assert.assertNotNull(location!!.extractId())
    }

    @Test
    fun `update a new work experience`() {
        val workExperienceRepresentation = WorkExperienceRepresentation(startDate = "2018-01-01", endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))
        val expectedWorkExperience = WorkExperience(id = "AN_ID", startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))

        val workExperience = WorkExperience(id = "AN_ID", startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="", commitments = listOf())
        val resumeId = UUID.randomUUID().toString()

        workExperienceRepository.save(resumeId, workExperience).block(Duration.ofMinutes(1))

        this.webClient.put()
                .uri("/resume/${resumeId}/work-experience/AN_ID")
                .body(BodyInserters.fromObject(workExperienceRepresentation))
                .exchange()
                .expectStatus().isNoContent

        val actualUpdatedEducation = workExperienceRepository.findOne(resumeId, "AN_ID").block(Duration.ofMinutes(1))
        Assert.assertNotNull(actualUpdatedEducation)
        Assert.assertThat(actualUpdatedEducation, Is.`is`(expectedWorkExperience))
    }

    @Test
    @WithMockUser(username = "user")
    fun `find all work experience in a resume`() {
        val resumeId = UUID.randomUUID().toString()
        workExperienceRepository.save(resumeId = resumeId, workExperience =  WorkExperience(id = UUID.randomUUID().toString(), startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))).block(Duration.ofMinutes(1))
        workExperienceRepository.save(resumeId = resumeId, workExperience =  WorkExperience(id = UUID.randomUUID().toString(), startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))).block(Duration.ofMinutes(1))

        val educationList = this.webClient.get()
                .uri("/resume/${resumeId}/work-experience")
                .exchange()
                .expectStatus().isOk
                .returnResult<WorkExperience>().responseBody
                .collectList().block(Duration.ofMinutes(1))

        Assert.assertNotNull(educationList)
        Assert.assertThat((educationList as MutableList).size, Is.`is`(2))
    }

    @Test
    @WithMockUser(username = "user")
    fun `find one education in a resume`() {
        val anId = UUID.randomUUID().toString()
        val resumeId = UUID.randomUUID().toString()
        workExperienceRepository.save(resumeId = resumeId, workExperience =  WorkExperience(id = anId, startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))).block(Duration.ofMinutes(1))
        workExperienceRepository.save(resumeId = resumeId, workExperience =  WorkExperience(id = UUID.randomUUID().toString(), startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))).block(Duration.ofMinutes(1))


        val actualDducation = this.webClient.get()
                .uri("/resume/${resumeId}/work-experience/$anId")
                .exchange()
                .expectStatus().isOk
                .returnResult<WorkExperience>().responseBody
                .toMono<WorkExperience>()
                .block(Duration.ofMinutes(1))

        Assert.assertNotNull(actualDducation)
        Assert.assertThat(actualDducation, Is.`is`(WorkExperience(id = anId, startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"), jobDescription ="JOB_DESCRIPTION_1", commitments = listOf("COMMITMENT_1", "COMMITMENT_2"))))
    }

}