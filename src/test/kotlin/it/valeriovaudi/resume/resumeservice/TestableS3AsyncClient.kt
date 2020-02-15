package it.valeriovaudi.resume.resumeservice

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient

object TestableS3AsyncClient {
    val accessKey: String = System.getProperty("aws.s3.access-key")
    val awsSecretKey: String = System.getProperty("aws.s3.secret-key")
    val awsRegion: String = System.getProperty("aws.s3.region")
    val bucket: String = System.getProperty("aws.s3.bucket")


    fun s3AsyncClient(): S3AsyncClient = S3AsyncClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, awsSecretKey)))
            .region(Region.of(awsRegion))
            .build()
}