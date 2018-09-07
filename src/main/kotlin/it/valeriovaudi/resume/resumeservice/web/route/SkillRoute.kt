package it.valeriovaudi.resume.resumeservice.web.route

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import it.valeriovaudi.resume.resumeservice.domain.repository.SkillsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.toMono

@Configuration
class SkillRoute {

    @Bean
    fun resumeRoutes(skillsRepository: SkillsRepository) = router {
        GET("/resume/{resumeId}/skill")
        {
            val resumeId = it.pathVariable("resumeId")
            skillsRepository.findOne(resumeId).toMono()
                    .flatMap { ok().body(BodyInserters.fromObject(it)) }
        }
        PUT("/resume/{resumeId}/skill")
        {
            val resumeId = it.pathVariable("resumeId")
            it.bodyToMono(Skill::class.java)
                    .flatMap { skillsRepository.save(resumeId, it).toMono() }
                    .flatMap { ServerResponse.noContent().build() }
        }
        DELETE("/resume/{resumeId}/skill/{skillFamily}")
        {
            val resumeId = it.pathVariable("resumeId")
            val skillFamily = it.pathVariable("skillFamily")
            skillsRepository.delete(resumeId, skillFamily).toMono()
                    .flatMap { ServerResponse.status(HttpStatus.NO_CONTENT).build() }
        }
    }
}