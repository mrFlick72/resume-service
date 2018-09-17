package it.valeriovaudi.resume.resumeservice.adapter.repository

import com.mongodb.client.gridfs.model.GridFSFile
import com.mongodb.client.result.UpdateResult
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetails
import it.valeriovaudi.resume.resumeservice.domain.model.PersonalDetailsPhoto
import it.valeriovaudi.resume.resumeservice.domain.repository.PersonalDetailsRepository
import org.bson.BsonString
import org.bson.Document
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.gridfs.GridFsResource
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import reactor.core.publisher.Mono
import java.util.*


class MongoPersonalDetailsRepository(private val mongoTemplate: ReactiveMongoTemplate,
                                     private val gridFsTemplate: GridFsTemplate) : PersonalDetailsRepository {

    companion object {
        fun collectionName() = "personalDetails"
        fun findOneQuery(resumeId: String) = Query.query(Criteria.where("resumeId").isEqualTo(resumeId))
        fun findOneQueryByMetadata(resumeId: String) = Query.query(Criteria.where("metadata.resumeId").isEqualTo(resumeId))
    }

    override fun delete(resumeId: String) =
            Mono.zip(mongoTemplate.remove(findOneQuery(resumeId), collectionName()),
                    Mono.fromCallable { gridFsTemplate.delete(findOneQueryByMetadata(resumeId)) })
                    .flatMap { Mono.just(Unit) }


    override fun save(resumeId: String, personalDetails: PersonalDetails): Publisher<PersonalDetails> {
        val personalDetailsMono =
                mongoTemplate.upsert(findOneQuery(resumeId),
                        Update.fromDocument(PersonalDetailsMapper.fromDomainToDocument(resumeId, personalDetails)), collectionName())
                        .onErrorResume { println("Error at ${it}"); Mono.just(UpdateResult.unacknowledged()) }

        val photoData =
                if (personalDetails.photo.content.isNotEmpty())
                    Mono.fromCallable { gridFsTemplate.delete(findOneQueryByMetadata(resumeId)) }
                            .map {
                                personalDetails.photo.content.inputStream().use {
                                    gridFsTemplate.store(it,
                                            resumeId,
                                            personalDetails.photo.fileExtension,
                                            mutableMapOf("resumeId" to resumeId))
                                }
                            }
                else Mono.just("");

        return Mono.zip(personalDetailsMono, photoData)
                .map { personalDetails }
                .onErrorReturn(PersonalDetails.emptyPersonalDetails())
    }


    override fun findOneWithoutPhoto(resumeId: String): Publisher<PersonalDetails> =
            mongoTemplate.findOne(findOneQuery(resumeId), Document::class.java, collectionName())
                    .switchIfEmpty(Mono.just(Document(mutableMapOf())))
                    .map { PersonalDetailsMapper.fromDocumentToDomain(document = it) }


    override fun findOne(resumeId: String): Publisher<PersonalDetails> =
            Mono.zip(mongoTemplate.findOne(findOneQuery(resumeId), Document::class.java, collectionName())
                    .switchIfEmpty(Mono.just(Document(mutableMapOf()))),
                    Mono.fromCallable { gridFsTemplate.getResource(resumeId) }
                            .switchIfEmpty(Mono.just(GridFsResource(GridFSFile(BsonString(UUID.randomUUID().toString()), "", 0, 0, Date(), "", Document(), Document(mapOf("contentType" to PersonalDetailsPhoto.emptyPersonalDetailsPhoto().fileExtension)))))))
                    .map {
                        val personalData = it.t1
                        val resource = it.t2

                        val photo = PersonalDetailsPhoto(content = resource.inputStream.readAllBytes(),
                                fileExtension = resource.contentType)
                        PersonalDetailsMapper.fromDocumentToDomain(document = personalData, photo = photo)
                    }
}