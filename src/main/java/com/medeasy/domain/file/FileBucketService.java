package com.medeasy.domain.file;

import java.io.File;

public interface FileBucketService {

    String saveAudioFile(byte[] audioBytes, String bucketName, String fileName);

    String getAudioFileUrl(String bucketName, String fileName);
}
