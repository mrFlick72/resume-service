package it.valeriovaudi.resume.resumeservice.web.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

@Configuration
class AwsConfiguration {

    @Bean
    fun awsCredentialsProvider(@Value("\${aws.s3.access-key}") accessKey: String,
                               @Value("\${aws.s3.secret-key}") awsSecretKey: String): AwsCredentialsProvider =
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, awsSecretKey))


    @Bean
    fun s3Client(@Value("\${aws.s3.region}") awsRegion: String,
                 awsCredentialsProvider: AwsCredentialsProvider) = S3AsyncClient.builder()
            .credentialsProvider(awsCredentialsProvider)
            .region(Region.of(awsRegion))
            .build()
}