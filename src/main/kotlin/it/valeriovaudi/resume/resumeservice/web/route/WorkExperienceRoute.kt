package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.repository.WorkExperienceRepository
import it.valeriovaudi.resume.resumeservice.web.representation.WorkExperienceRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono
import java.net.URI


@Configuration
class WorkExperienceRoute {

    @Bean
    fun workExperienceRoutes(@Value("\${baseServer:http://localhost:8080}") baseServer: String,
                             workExperienceRepository: WorkExperienceRepository) = router {
        POST("/resume/{resumeId}/work-experience")
        {
            val resumeId = it.pathVariable("resumeId")
            it.bodyToMono(WorkExperienceRepresentation::class.java)
                    .flatMap { workExperienceRepository.save(resumeId, WorkExperienceRepresentation.fromRepresentationToDomain(it, resumeId)).toMono() }
                    .flatMap { ServerResponse.created(URI("$baseServer/resume/$resumeId/education/${it.id}")).build() }

        }

        GET("/resume/{resumeId}/education")
        {
            TODO()
        }

        GET("/resume/{resumeId}/education/{educationId}")
        {
            TODO()
        }

        PUT("/resume/{resumeId}/education/{educationId}")
        {
            TODO()
        }
    }

}