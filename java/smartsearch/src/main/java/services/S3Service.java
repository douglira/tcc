package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;

public class S3Service {
    private final static String BUCKET = "smartsearch-app";

    public String uploadObject(InputStream input, String contentType, String key) throws Exception {
        String url = null;

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            s3Client.putObject(
                    new PutObjectRequest(BUCKET, key, input, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            url = s3Client.getUrl(BUCKET, key).toString();

        } catch (Exception err) {
            throw err;
        } finally {
            input.close();
        }
        return url;
    }

    public void deleteObject(String key) throws Exception {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();
            s3Client.deleteObject(new DeleteObjectRequest(BUCKET, key));
        } catch (Exception e) {
            throw e;
        }
    }
}
