package com.efundzz.servcie;

import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.securitylake.model.S3Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;

@Slf4j
@Service
public class S3Service {

    @Value("${DOCUPLOAD_S3_PATH}")
    private String bucketName;

    @Autowired
    AmazonS3 amazonS3;
    Regions region = Regions.EU_WEST_1;
    String accessKey = "AKIATTG4WIZ243GNZZG5";
    String secretKey = "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";

    public URL getPresignedUrl(String operation, String fileName) {

        Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1 hour from now

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName, (operation.equals("WRITE") ? HttpMethod.PUT : HttpMethod.GET))
                        .withExpiration(expiration);
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    public void uploadFile(String fileName) {
        try {

            File file = new File(fileName);
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);

            amazonS3.putObject(request);

            // if upload is successful, delete the file
            Files.delete(file.toPath());

            // you can log the response details for your own needs
        } catch (Exception e) {
            // handle exception
            System.out.println(e);
        }
    }

    public void uploadToS3(){
        try {

            // Create the connection and use it to upload the new object.
            HttpURLConnection connection = (HttpURLConnection) getPresignedUrl("WRITE", "test.txt").openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestMethod(HttpMethod.PUT.name());
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("This text was uploaded as an object by using a presigned URL.");
            out.close();

            connection.getResponseCode();
            System.out.println("HTTP response code is " + connection.getResponseCode());

        } catch (S3Exception | IOException e) {
            e.getStackTrace();
        }
    }

    public void readFromS3(){
        try {

            // Create the connection and use it to upload the new object.
            HttpURLConnection connection = (HttpURLConnection) getPresignedUrl("READ", "test.txt").openConnection();
            connection.setRequestMethod(HttpMethod.GET.name());
            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the content of the S3 object as an input stream
                InputStream inputStream = connection.getInputStream();

                // Read the input stream using a BufferedReader
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                }

                // Close the input stream and the BufferedReader
                bufferedReader.close();
                inputStream.close();

                // Do something with the content
                System.out.println("Content of the S3 object: " + content.toString());
            } else {
                System.out.println("Failed to read from S3 bucket. Response code: " + responseCode);
            }

            // Disconnect the connection
            connection.disconnect();

        } catch (S3Exception | IOException e) {
            e.getStackTrace();
        }
    }
}
