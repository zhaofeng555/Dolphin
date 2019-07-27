package com.haojg.id;

import com.haojg.id.generator.BitsAllocator;
import com.haojg.id.generator.DefaultUidGenerator;
import com.haojg.id.generator.UidGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class IdGen {

    private static int QUEUE_SIZE = 1 << 16;

    private static ArrayBlockingQueue<Long> queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static ArrayBlockingQueue<Long> queue2 = new ArrayBlockingQueue<>(2);

    private static int len = 10000000;
    private static AtomicBoolean isAppend = new AtomicBoolean(false);
    private static ExecutorService appendExecutor = Executors.newSingleThreadExecutor();

    private static AtomicLong index = new AtomicLong(1);

    public static void main(String[] args) {
        UidGenerator gen = new DefaultUidGenerator();
        BitsAllocator ba = new BitsAllocator(28, 22,13);
        gen.setBitsAllocator(ba);
        long uid = gen.getUID();
        System.out.println(uid);
        System.out.println(gen.parseUID(uid));

//        for (long i = 0; i < QUEUE_SIZE; i++) {
//            try {
//                queue.put(i);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        System.out.println("first put ... end....");
//
//
//        executor.execute(() -> {
//            long start = System.currentTimeMillis();
//            try {
//                System.out.println("start = " + start);
//                for (int j = 0; j < 10; j++) {
//                    for (int i = 0; i < len / 10; i++) {
//                        consumerUid();
////                        System.out.println("------------consumer -> "+(j+" <-> "+i));
//                    }
//
//                }
//                System.out.println("========end " + (len));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("post time = " + (System.currentTimeMillis() - start));
//        });

//        executor.shutdown();
//        appendExecutor.shutdown();
    }

    public static void consumerUid() {
//        System.out.println(queue.remainingCapacity());
        if (queue.remainingCapacity() >= QUEUE_SIZE / 2) {
            appendUid();
        }
        try {
            Long take = queue.take();
            if (take % 1000 == 0) {
                System.out.println("take = " + take);
            }
//                Thread.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void appendUid() {
        if (isAppend.get()) {
            return;
        }
        isAppend.set(true);

        appendExecutor.execute(() -> {
            for (int i = 0, total = QUEUE_SIZE / 2; i < total; i++) {
                try {
                    boolean offer = queue.offer(index.getAndIncrement());
//                    System.out.println("offer = "+offer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("queue.capacity = " + (queue.remainingCapacity()));
            isAppend.set(false);
        });


    }
}
