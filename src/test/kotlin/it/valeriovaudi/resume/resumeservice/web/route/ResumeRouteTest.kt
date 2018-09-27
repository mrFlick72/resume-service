package it.valeriovaudi.resume.resumeservice.web.route

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.resume.resumeservice.domain.model.*
import it.valeriovaudi.resume.resumeservice.domain.repository.ResumeRepository
import it.valeriovaudi.resume.resumeservice.web.representation.PersonalDetailsRepresentation
import it.valeriovaudi.resume.resumeservice.web.representation.ResumeRepresentation
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
import reactor.core.publisher.toMono
import java.time.Duration
import java.time.LocalDate
import java.util.*

@ContextConfiguration(initializers = [TestContextInitializer::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class ResumeRouteTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var resumeRepository: ResumeRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @WithMockUser(username = "user")
    fun `save reume`() {
        val location = webClient.post()
                .uri("/resume")
                .exchange()
                .expectStatus().isCreated
                .returnResult<Any>().responseHeaders.location

        val locationChunck = location!!.toString().split("/")
        val resumeId = locationChunck[locationChunck.size - 1];
        Assert.assertNotNull(resumeRepository.findOne(resumeId).toMono().block(Duration.ofMinutes(1)))
    }

    @Test
    @WithMockUser(username = "user")
    fun `find resume`() {
        val resumeId = UUID.randomUUID().toString()
        val resume = Resume(resumeId, "USER_NAME", Language.EN, PersonalDetails.emptyPersonalDetails(), skill = listOf(Skill("FAMILY", listOf("SKILL_1"))), educations =  listOf(Education(id="1", dateFrom = LocalDate.of(2018,1,1), title = "A_TITLE", type = EducationType.CERTIFICATION)))
        val resumeRepresentation = ResumeRepresentation(resumeId, "USER_NAME", Language.EN.name, PersonalDetailsRepresentation.fromDomainToRepresentation(PersonalDetails.emptyPersonalDetails()), listOf(Skill("FAMILY", listOf("SKILL_1"))))

        resumeRepository.save(resume).toMono().block(Duration.ofMinutes(1))

        webClient.get()
                .uri("/resume/${resume.id}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(objectMapper.writeValueAsString(resumeRepresentation))
    }

}