package chen.linfeng;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class HttpUploader {
    private static final OkHttpClient client = new OkHttpClient();

    public static String uploadFile(Path path) throws IOException {
        File file = path.toFile();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url("http://your-server-address.com/upload")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return "Uploaded: " + file.getName();
        }
    }
}
