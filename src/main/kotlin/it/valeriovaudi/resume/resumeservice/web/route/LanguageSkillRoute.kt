package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.model.LanguageSkills
import it.valeriovaudi.resume.resumeservice.domain.repository.LanguageSkillsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono

@Configuration
class LanguageSkillRoute {

    @Bean
    fun languageSkillRoutes(@Value("\${baseServer:http://localhost:8080}") baseServer: String,
                            languageSkillsRepository: LanguageSkillsRepository) = router {

        GET("/resume/{resumeId}/language-skills")
        {
            languageSkillsRepository.findOne(it.pathVariable("resumeId")).toMono()
                    .flatMap { ServerResponse.ok().body(BodyInserters.fromObject(it)) }
        }

        PUT("/resume/{resumeId}/language-skills")
        {
            val resumeId = it.pathVariable("resumeId")
            it.bodyToMono(LanguageSkills::class.java)
                    .flatMap { languageSkillsRepository.save(resumeId, it).toMono() }
                    .flatMap { ServerResponse.noContent().build() }
        }

        DELETE("/resume/{resumeId}/language-skills") {
            val resumeId = it.pathVariable("resumeId")
            languageSkillsRepository.delete(resumeId).toMono()
                    .flatMap { ServerResponse.noContent().build() }
        }
    }

}