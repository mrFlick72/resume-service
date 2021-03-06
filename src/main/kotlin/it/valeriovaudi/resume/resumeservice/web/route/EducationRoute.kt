package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.repository.EducationRepository
import it.valeriovaudi.resume.resumeservice.web.representation.EducationRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import java.net.URI

@Configuration
class EducationRoute {

    @Bean
    fun educationRoutes(@Value("\${baseServer:http://localhost:8080}") baseServer: String,
                        educationRepository: EducationRepository) = router {

        POST("/resume/{resumeId}/education")
        {
            val resumeId = it.pathVariable("resumeId")
            it.bodyToMono(EducationRepresentation::class.java)
                    .flatMap { educationRepository.save(resumeId, EducationRepresentation.fromRepresentationToDomain(it)).toMono() }
                    .flatMap { ServerResponse.created(URI("$baseServer/resume/$resumeId/education/${it.id}")).build() }
        }

        GET("/resume/{resumeId}/education")
        {
            educationRepository.findAll(it.pathVariable("resumeId")).toFlux()
                    .collectList()
                    .flatMap { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
        }

        GET("/resume/{resumeId}/education/{educationId}")
        {
            educationRepository.findOne(it.pathVariable("resumeId"), it.pathVariable("educationId")).toMono()
                    .flatMap { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
        }

        PUT("/resume/{resumeId}/education/{educationId}")
        {
            val resumeId = it.pathVariable("resumeId")
            val educationId = it.pathVariable("educationId")
            it.bodyToMono(EducationRepresentation::class.java)
                    .flatMap { educationRepository.save(resumeId, EducationRepresentation.fromRepresentationToDomain(it, educationId)).toMono() }
                    .flatMap { ServerResponse.noContent().build() }
        }
    }

}