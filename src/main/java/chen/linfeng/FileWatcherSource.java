package chen.linfeng;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcherSource {
    public static Observable<Path> create(Path dir) {
        return Observable.<Path>create(emitter -> {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            dir.register(watchService, ENTRY_CREATE); // 只监听新建事件

            try {
                while (!emitter.isDisposed()) {
                    WatchKey key = watchService.take(); // 阻塞等待事件
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == ENTRY_CREATE) {
                            Path fileName = (Path) event.context();
                            emitter.onNext(dir.resolve(fileName));
                        }
                    }
                    if (!key.reset()) break;
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io()); // 在 IO 线程运行监控
    }
}
