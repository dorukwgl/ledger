package com.doruk.application.interfaces;

import java.io.BufferedInputStream;
import java.io.IOException;

public interface UploadSource {
    BufferedInputStream openStream() throws IOException;
    String originalFilename();
    long size();
    String contentType();
}
