package chen.linfeng;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MainApp {
    public static void main(String[] args) throws InterruptedException {
        Path monitorPath = Paths.get("C:\\Users\\kaven\\Desktop\\workdir\\upload-files");

        FileWatcherSource.create(monitorPath)
            // 过滤掉文件夹，只处理文件
            .filter(path -> !path.toFile().isDirectory())
            // flatMap 并行处理上传，确保一个失败不会导致整个流中断
            .flatMap(path -> Observable.fromCallable(() -> HttpUploader.uploadFile(path))
                    .subscribeOn(Schedulers.io())
                    .onErrorReturn(e -> "Error uploading " + path + ": " + e.getMessage())
            )
            .subscribe(
                result -> System.out.println("[SUCCESS] " + result),
                error -> System.err.println("[CRITICAL ERROR] " + error.getMessage())
            );

        // 防止主线程退出
        Thread.currentThread().join();
    }
}
