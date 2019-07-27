package com.haojg.id.buffer;

import com.haojg.id.generator.DefaultUidGenerator;
import com.haojg.id.generator.UidGenerator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class IdQueueBuffer implements IdBuffer, DisposableBean {
    private int QUEUE_SIZE = 1 << 16; //65536
    private ArrayBlockingQueue<Long> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    private ExecutorService appendExecutor = Executors.newSingleThreadExecutor();

    // 添加id到队列的标志
    private AtomicBoolean isAppend = new AtomicBoolean(false);

    @Autowired
    private UidGenerator generator = new DefaultUidGenerator();


    private int paddingFactor = 50;
    private int paddingThreshold;

    public IdQueueBuffer(){
        paddingThreshold = QUEUE_SIZE * paddingFactor / 100;
    }

    @Override
    public void destroy() throws Exception {
        appendExecutor.shutdownNow();
    }

    /**
     * 获取一个 uid
     * @return
     */
    public Long generatorId() {
        if (queue.remainingCapacity() >= paddingThreshold) {
            paddingId2Queue();
        }

        return queue.remove();
    }

    /**
     * 添加id到队列，当前只能有一个线程工作
     */
    private void paddingId2Queue() {
        if (isAppend.get()) {
            return;
        }

        synchronized (isAppend) {
            if (isAppend.get()) {
                return;
            }
            isAppend.set(true);
        }

        appendExecutor.execute(() -> {
            for (int i = 0, total = QUEUE_SIZE / 2; i < total; i++) {
                queue.offer(generator.getUID());
            }
            isAppend.set(false);
        });
    }
}
