package it.valeriovaudi.resume.resumeservice.domain.repository

import it.valeriovaudi.resume.resumeservice.domain.model.WorkExperience
import org.reactivestreams.Publisher

interface WorkExperienceRepository {

    fun findAll(resumeId: String): Publisher<WorkExperience>

    fun findOne(workExperienceId: String): Publisher<WorkExperience>

    fun save(resumeId: String, workExperience: WorkExperience): Publisher<WorkExperience>

    fun delete(workExperienceId: String): Publisher<Unit>

}