package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.repository.LanguageSkillsRepository
import it.valeriovaudi.resume.resumeservice.domain.repository.WorkExperienceRepository
import it.valeriovaudi.resume.resumeservice.web.representation.WorkExperienceRepresentation
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
class LanguageSkillRoute {

    @Bean
    fun languageSkillRoutes(@Value("\${baseServer:http://localhost:8080}") baseServer: String,
                            languageSkillsRepository: LanguageSkillsRepository) = router {
        POST("/resume/{resumeId}/language-skill")
        {
            //            val resumeId = it.pathVariable("resumeId")
//            it.bodyToMono(WorkExperienceRepresentation::class.java)
//                    .flatMap { workExperienceRepository.save(resumeId, WorkExperienceRepresentation.fromRepresentationToDomain(it, resumeId)).toMono() }
//                    .flatMap { ServerResponse.created(URI("$baseServer/resume/$resumeId/work-experience/${it.id}")).build() }
            TODO()
        }

        GET("/resume/{resumeId}/language-skill")
        {
            //            workExperienceRepository.findAll(it.pathVariable("resumeId")).toFlux()
//                    .collectList()
//                    .flatMap { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
            TODO()
        }

        PUT("/resume/{resumeId}/language-skill")
        {
            //            val resumeId = it.pathVariable("resumeId")
//            val educationId = it.pathVariable("educationId")
//            it.bodyToMono(WorkExperienceRepresentation::class.java)
//                    .flatMap { workExperienceRepository.save(resumeId, WorkExperienceRepresentation.fromRepresentationToDomain(it, educationId)).toMono() }
//                    .flatMap { ServerResponse.noContent().build() }
            TODO()
        }
    }

}