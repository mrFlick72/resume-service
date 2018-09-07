package it.valeriovaudi.resume.resumeservice.domain.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Skill
import org.reactivestreams.Publisher

interface SkillsRepository {

    fun findOne(resumeId: String): Publisher<Skill>

    fun save(resumeId: String, skill: List<Skill>): Publisher<Skill>

    fun delete(resumeId: String, skillFamily: String): Publisher<Unit>

}