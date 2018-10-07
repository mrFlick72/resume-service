package it.valeriovaudi.resume.resumeservice.web.route

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoEducationRepository
import it.valeriovaudi.resume.resumeservice.domain.model.Education
import it.valeriovaudi.resume.resumeservice.domain.model.EducationType
import it.valeriovaudi.resume.resumeservice.extractId
import it.valeriovaudi.resume.resumeservice.web.representation.EducationRepresentation
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
import java.time.Duration
import java.time.LocalDate
import java.util.*


@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class EducationRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var educationRepository: MongoEducationRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper;

    @Test
    @WithMockUser(username = "user")
    fun `save a new education`() {
        val educationRepresentation = EducationRepresentation(title = "A_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))
        val resumeId = UUID.randomUUID().toString()

        val location = this.webClient.post()
                .uri("/resume/${resumeId}/education")
                .body(BodyInserters.fromObject(educationRepresentation))
                .exchange()
                .expectStatus().isCreated
                .returnResult<Any>().responseHeaders.location

        println(location!!.extractId())
        Assert.assertNotNull(location.extractId())
    }

    @Test
    fun `update a new education`() {
        val educationRepresentation = EducationRepresentation(title = "A_NEW_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))
        val expectedEducation = Education(id = "AN_ID", title = "A_NEW_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))
        val education = Education(id = "AN_ID", title = "A_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))
        val resumeId = UUID.randomUUID().toString()

        educationRepository.save(resumeId, education).block(Duration.ofMinutes(1))

        this.webClient.put()
                .uri("/resume/${resumeId}/education/AN_ID")
                .body(BodyInserters.fromObject(educationRepresentation))
                .exchange()
                .expectStatus().isNoContent

        val actualUpdatedEducation = educationRepository.findOne(resumeId, "AN_ID").block(Duration.ofMinutes(1))
        Assert.assertNotNull(actualUpdatedEducation)
        Assert.assertThat(actualUpdatedEducation, Is.`is`(expectedEducation))
    }


    @Test
    @WithMockUser(username = "user")
    fun `find all education in a resume`() {
        val resumeId = UUID.randomUUID().toString()
        educationRepository.save(resumeId = resumeId, education = Education(id = UUID.randomUUID().toString(), title = "A_NEW_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))).block(Duration.ofMinutes(1))
        educationRepository.save(resumeId = resumeId, education = Education(id = UUID.randomUUID().toString(), title = "A_NEW_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))).block(Duration.ofMinutes(1))
        educationRepository.save(resumeId = resumeId, education = Education(id = UUID.randomUUID().toString(), title = "A_NEW_TITLE", type = EducationType.BARCHELOR_DEGREE, dateFrom = LocalDate.of(2018, 1, 1))).block(Duration.ofMinutes(1))


        val educationList  = this.webClient.get()
                .uri("/resume/${resumeId}/education")
                .exchange()
                .expectStatus().isOk
                .returnResult<Any>().responseBody
                .collectList().block(Duration.ofMinutes(1))

        println(educationList)
        Assert.assertNotNull(educationList)
        Assert.assertThat((educationList as MutableList).size, Is.`is`(3))
    }

}