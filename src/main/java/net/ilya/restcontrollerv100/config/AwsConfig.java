package net.ilya.restcontrollerv100.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;


@Configuration
public class AwsConfig {
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(@Value("${spring.cloud.aws.credentials.access-key}") String accessKey, @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey) {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
    }

    @Bean
    public AwsCredentialsProvider awsAsyncCredentialsProvider(@Value("${spring.cloud.aws.credentials.access-key}") String accessKey, @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }
    @Bean
    public AmazonS3 amazonS3(AWSCredentialsProvider awsCredentialsProvider, @Value("${spring.cloud.aws.cloudwatch.endpoint}") String serviceEndpoint, @Value("${spring.cloud.aws.region.static}") String region) {

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(
                                serviceEndpoint,
                                region
                        )
                )
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider.getCredentials()))
                .build();
    }
}

//    public AmazonSQSAsync amazonSQSAsync(AWSCredentialsProvider awsCredentialsProvider, @Value("${spring.cloud.aws.cloudwatch.endpoint}") String serviceEndpoint,
//                                         @Value("${spring.cloud.aws.region.static}") String workRegion) {
//        return AmazonSQSAsyncClientBuilder.standard()
//                .withCredentials(awsCredentialsProvider)
//                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(serviceEndpoint, workRegion))
//                .build();
//    }