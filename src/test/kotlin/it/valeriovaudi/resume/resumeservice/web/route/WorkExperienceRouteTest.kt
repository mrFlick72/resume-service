package it.valeriovaudi.resume.resumeservice.web.route

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.resume.resumeservice.adapter.repository.MongoWorkExperienceRepository
import it.valeriovaudi.resume.resumeservice.extractId
import it.valeriovaudi.resume.resumeservice.web.representation.WorkExperienceRepresentation
import it.valeriovaudi.todolist.TestContextInitializer
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
        val workExperienceRepresentation = WorkExperienceRepresentation(startDate = LocalDate.of(2018,1,1), endDate = null, company = "A_COMPANY", technologies = listOf("TECH_1","TECH_2"))
        val resumeId = UUID.randomUUID().toString()

        val location = this.webClient.post()
                .uri("/resume/${resumeId}/work-experience")
                .body(BodyInserters.fromObject(workExperienceRepresentation))
                .exchange()
                .expectStatus().isCreated
                .returnResult<Any>().responseHeaders.location

        println(location!!.extractId())
        Assert.assertNotNull(location.extractId())
    }

}