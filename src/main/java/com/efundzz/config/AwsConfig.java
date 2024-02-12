package com.efundzz.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    String accessKey = "AKIATTG4WIZ243GNZZG5";
    String secretKey = "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";

    Regions region = Regions.EU_WEST_1;

    private AWSStaticCredentialsProvider getCredentials() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(awsCreds);
    }

    /**
     * Build the aws sns client with default configuration
     *
     * @return AmazonSNSClient
     */
    @Bean
    public AmazonSNS amazonSNS() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonSNSClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(region).build();
    }

    /**
     * Build the aws s3 client with default configuration
     *
     * @return AmazonSNSClient
     */
    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(getCredentials())
                .withRegion(region)
                .build();
    }
}