package it.valeriovaudi.resume.resumeservice.adapter.repository

import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.repository.PersonalDetailsRepository
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.time.format.DateTimeFormatter


class MongoPersonalDetailsRepository(private val mongoTemplate: ReactiveMongoTemplate,
                                     private val gridFsTemplate: GridFsTemplate) : PersonalDetailsRepository {

    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    override fun save(resumeId: String, personalDetails: Publisher<PersonalDetails>): Publisher<PersonalDetails> =

            personalDetails.toMono().flatMap {
                val personalDetails = it;
                val savedPersonalDetailsPersistanceModel =
                        mongoTemplate.save(PersonalDetailsMapper.fromDomainToDocument(resumeId, personalDetails), "personalDetails")

                val photoData =
                        if (personalDetails.photo.content.isNotEmpty())
                            Mono.fromCallable { gridFsTemplate.delete(Query.query(Criteria.where("metadata.resumeId").`is`(resumeId))) }
                                    .map {
                                        personalDetails.photo.content.inputStream().use {
                                            gridFsTemplate.store(it,
                                                    resumeId,
                                                    personalDetails.photo.fileExtension,
                                                    mutableMapOf("resumeId" to resumeId))
                                        }
                                    }
                        else Mono.empty()

                Mono.zip(savedPersonalDetailsPersistanceModel, photoData)
                        .map { personalDetails }
                        .onErrorReturn(PersonalDetails.emptyPersonalDetails())
            }


    override fun findOneWithoutPhoto(resumeId: String): Publisher<PersonalDetails> =
            mongoTemplate.findOne(Query.query(Criteria.where("_id").`is`(resumeId)),
                    Document::class.java, "personalDetails")
                    .map { PersonalDetailsMapper.fromDocumentToDomain(document = it) }


    override fun findOne(resumeId: String): Publisher<PersonalDetails> =
            Mono.zip(mongoTemplate.findOne(Query.query(Criteria.where("resumeId").`is`(resumeId)),
                    Document::class.java, "personalDetails"),
                    Mono.fromCallable { gridFsTemplate.getResource(resumeId) })
                    .map {
                        val personalData = it.t1
                        val resource = it.t2

                        val photo = PersonalDetailsPhoto(content = resource.inputStream.readAllBytes(),
                                fileExtension = resource.contentType)
                        PersonalDetailsMapper.fromDocumentToDomain(document = personalData, photo = photo)
                    }
}