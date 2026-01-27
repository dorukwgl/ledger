package com.doruk.infrastructure.messaging.handler;

import com.doruk.application.enums.ImageVariant;
import com.doruk.application.events.ProfileImageUploadEvent;
import com.doruk.application.interfaces.ObjectStorage;
import com.doruk.infrastructure.config.AppExecutors;
import com.doruk.infrastructure.util.ImageVariantKey;
import io.micronaut.nats.annotation.NatsListener;
import io.micronaut.nats.annotation.Subject;
import jakarta.inject.Singleton;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Singleton
@NatsListener
@RequiredArgsConstructor
public class ImageUploadEventHandler {
    private final AppExecutors executors;
    private final ObjectStorage storage;

    private Pair<Integer, InputStream> scaleAndCompress(InputStream stream, ImageVariant variant) {
        var t = Thumbnails.of(stream)
                .outputQuality(variant == ImageVariant.ORIGINAL ? 0.95f : 0.7f);

        if (variant != ImageVariant.ORIGINAL)
            t.size(variant.maxSize(), variant.maxSize());

        var out = new ByteArrayOutputStream(64 * 1024);
        try {
            t.toOutputStream(out);
            var bytes = out.toByteArray();

            return new Pair<>(bytes.length, new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteOldVariantFiles(ProfileImageUploadEvent event) {
        if (event.oldObjectKey().isEmpty())
            return;

        var objectKey = event.oldObjectKey().get();
        for (var variant : ImageVariant.values()) {
            String variantKey = ImageVariantKey.of(objectKey, variant);
            storage.delete(variantKey);
        }
    }

    private void handleScaling(ProfileImageUploadEvent event) {
        for (ImageVariant variant : ImageVariant.values()) {
            try (InputStream imgVariants = storage.open(event.objectKey())) {

                var data = CompletableFuture.supplyAsync(() ->
                                this.scaleAndCompress(imgVariants, variant), executors.CPU())
                        .join();

                String variantKey = ImageVariantKey.of(event.objectKey(), variant);

                storage.put(
                        variantKey,
                        data.getValue(),
                        data.getKey(),
                        event.mimeType()
                );
            } catch (IOException e) {
                throw new RuntimeException("Variant generation failed", e);
            }
        }
    }

    @Subject(value = "profile.image.upload.event", queue = "profile-image-upload-queue")
    public void handle(ProfileImageUploadEvent event) {
        CompletableFuture.runAsync(() -> {
            handleScaling(event);
            // delete files
            deleteOldVariantFiles(event);
        }, executors.VIRTUAL());
    }

//    @Subject(value = "file.image.upload.multi", queue = "image-upload-multi-queue")
//    public void handle(MultiImageUpload event) {
//        // parallel processing
//        event.files().forEach(file -> CompletableFuture.runAsync(() ->
//                this.storeFile(file), executors.CPU()));
//    }
}
