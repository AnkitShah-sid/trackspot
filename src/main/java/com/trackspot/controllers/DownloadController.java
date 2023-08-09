package com.trackspot.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/download")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class DownloadController {

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getFile() {
        ClassPathResource resource = new ClassPathResource("com/tracskpot/produceapk/trackspot.apk");
        try {
            InputStream in = resource.getInputStream();
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/apkLink", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getLink(HttpServletResponse response) {
        ClassPathResource resource = new ClassPathResource("com/tracskpot/produceapk/trackspot.apk");
        try {
            InputStream in = resource.getInputStream();
            byte[] apkBytes = StreamUtils.copyToByteArray(in);
            response.setContentType("application/vnd.android.package-archive");
            response.setHeader("Content-Disposition", "attachment; filename=trackspot.apk");
            response.setContentLength(apkBytes.length);
            return apkBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

