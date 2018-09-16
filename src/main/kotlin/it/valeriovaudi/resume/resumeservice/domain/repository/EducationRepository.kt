package it.valeriovaudi.resume.resumeservice.domain.repository

import it.valeriovaudi.resume.resumeservice.domain.model.Education
import org.reactivestreams.Publisher

interface EducationRepository {

    fun findAll(resumeId: String): Publisher<Education>

    fun save(resumeId: String, education: Education): Publisher<Education>

    fun delete(resumeId: String, educationTitle: String): Publisher<Unit>

}