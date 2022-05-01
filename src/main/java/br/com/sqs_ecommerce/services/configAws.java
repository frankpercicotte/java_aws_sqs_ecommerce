package br.com.sqs_ecommerce.services;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.regions.Region;

public class configAws {
    
    static AwsCredentialsProvider credentialsProvider = new AwsCredentialsProvider() {
        @Override
        public AwsCredentials resolveCredentials() {
            return new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return System.getenv("AWS_ACCESS_KEY");
                }
    
                @Override
                public String secretAccessKey() {
                    return System.getenv("AWS_SECRET_KEY");
                }
            };
        }
    };

    public static SqsClient sqsClient(){
        return SqsClient.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build();
     
    }   

    public static GetQueueUrlRequest getUrl(){
        String awsId = System.getenv("AWS_ACCOUNT_ID");       
        return GetQueueUrlRequest.builder()               
            .queueName("queue_poc_ecommerce")  
            .queueOwnerAWSAccountId(awsId).build();            
    }
}


