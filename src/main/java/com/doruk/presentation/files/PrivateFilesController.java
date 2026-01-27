package com.doruk.presentation.files;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Accessing Private Files")
@Controller("/resp")
public class PrivateFilesController {

    @Get("/{filename:.*}")
    public HttpResponse<?> stream(String filename) {

        // 1. Authenticate user, check database permissions, etc.
//        boolean isUserAllowed = true; // Replace with actual logic
//
//        if (!isUserAllowed) {
//            return HttpResponse.unauthorized();
//        }

        // 2. Instead of reading the file in Java, send the instruction to Nginx
        // The path must match the Nginx 'location' block (without the alias)
        String nginxPath = "/protected/" + filename;

        return HttpResponse.ok()
                .header("X-Accel-Redirect", nginxPath)
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"");
    }
}