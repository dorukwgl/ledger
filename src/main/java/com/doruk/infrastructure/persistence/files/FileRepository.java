package com.doruk.infrastructure.persistence.files;

import com.doruk.application.dto.StoredObject;
import com.doruk.infrastructure.persistence.entity.MediaStore;
import com.doruk.infrastructure.persistence.entity.MediaStoreDraft;
import com.doruk.infrastructure.persistence.entity.MediaStoreTable;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;

@Singleton
@RequiredArgsConstructor
public class FileRepository {
    private final JSqlClient client;

    public long save(StoredObject fileObject) {
        var draft = MediaStoreDraft.$.produce(m -> m
                .setSize(fileObject.size())
                .setMimeType(fileObject.mimeType())
                .setObjectKey(fileObject.objectKey())
                .setVisibility(fileObject.visibility())
        );

        return client.saveCommand(draft).execute().getModifiedEntity().id();
    }

    public String deleteReturningObjectKey(long id) {
        var t = MediaStoreTable.$;
        var objectId = client.createQuery(t)
                .select(t.objectKey())
                .execute();

        client.deleteById(MediaStore.class, id);

        return objectId.getFirst();
    }
}
