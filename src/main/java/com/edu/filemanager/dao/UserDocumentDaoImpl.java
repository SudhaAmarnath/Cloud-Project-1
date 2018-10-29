package com.edu.filemanager.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.edu.filemanager.awslibrary.AwsClientFactory;
import com.edu.filemanager.model.UserDocument;

@Repository("userDocumentDao")
public class UserDocumentDaoImpl extends AbstractDao<Integer, UserDocument> implements UserDocumentDao{

	@SuppressWarnings("unchecked")
	public List<UserDocument> findAll() {
		Criteria crit = createEntityCriteria();
		return (List<UserDocument>)crit.list();
	}

	public void save(UserDocument document) {
		persist(document);
        String bucketName = "filemanager-22102018";
        //Path filePath = Paths.get("C:\\Users\\Sudha\\test.txt");
        //String key = "test.txt";
        String keyPath = document.getName();
        String[] arrOfStr = keyPath.split("\\\\"); 
        String key = arrOfStr[arrOfStr.length-1];        
        Path filePath = Paths.get(keyPath);        
        
        AmazonS3 amazonS3 = null;
		try {
			amazonS3 = AwsClientFactory.createClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);

        long fileSize=0;
		try {
			fileSize = Files.size(filePath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        long partSize = 5 * 1024 * 1024; // 5MB
        long filePosition = 0;
        List<PartETag> partETags = new ArrayList<>();

        try {
            int part = 1;
            while (filePosition < fileSize) {
                partSize = Math.min(partSize, fileSize - filePosition);

                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(key)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(part) // parts start with 1
                        .withFileOffset(filePosition)
                        .withFile(filePath.toFile())
                        .withPartSize(partSize);
                UploadPartResult uploadPartResult = amazonS3.uploadPart(uploadRequest);
                partETags.add(uploadPartResult.getPartETag());

                filePosition += partSize;
                part++;
            }

            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName,
                    key, initResponse.getUploadId(), partETags);
            amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (SdkClientException e) {
            amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, key, initResponse.getUploadId()));
        }

        
	}

	
	public UserDocument findById(int id) {
		return getByKey(id);
	}

	@SuppressWarnings("unchecked")
	public List<UserDocument> findAllByUserId(int userId){
		Criteria crit = createEntityCriteria();
		Criteria userCriteria = crit.createCriteria("user");
		userCriteria.add(Restrictions.eq("id", userId));
		return (List<UserDocument>)crit.list();
	}

	
	public void deleteById(int id) {
		UserDocument document =  getByKey(id);

        String bucketName = "filemanager-22102018";
        //String key = "2/Eiffel.jpg";
        String keyPath = document.getName();
        String[] arrOfStr = keyPath.split("\\\\"); 
        String key = arrOfStr[arrOfStr.length-1];
        
        AmazonS3 amazonS3 = null;
		try {
			amazonS3 = AwsClientFactory.createClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        amazonS3.deleteObject(deleteObjectRequest);       
		delete(document);
		
	}

}
