package com.example.demo.config.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.config.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.demo.config.BaseResponseStatus.FAIL_FILE_UPLOAD;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public List<String> uploadFileV1(List<MultipartFile> multipartFiles) throws BaseException {
        if (multipartFiles.isEmpty()) {
            //비어있을때?
        }

        List<String> result = new ArrayList<>(); //저장된 s3 upload url을 저장할 변수

        for (int i = 0; i < multipartFiles.size(); i++) {
            MultipartFile current_file = multipartFiles.get(i); //파일하나씩 가져옴.
            //String fileName = CommonUtils.buildFileName(category, multipartFile.getOriginalFilename());
//            String fileName = current_file.getName();
            String fileName = UUID.randomUUID().toString().substring(0,20); //파일이름은 랜덤으로 지정해서 넘긴다.

            ObjectMetadata objectMetadata = new ObjectMetadata(); //오브젝트메타데이터 생성
            objectMetadata.setContentType(current_file.getContentType()); //오브젝트 메타데이터에 파일의 컨텐트타입을 세팅

            try (InputStream inputStream = current_file.getInputStream()) { //파일 업로드
                amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));


            } catch (IOException e) {
                throw new BaseException(FAIL_FILE_UPLOAD);
            }

            result.add(amazonS3Client.getUrl(bucketName, fileName).toString()); //url를 가져와서 저장.

        }


        return result; //url 리스트를 리턴.
    }
}

