import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SumThreads {

    static void main(String[] args) throws InterruptedException {
        final int N = 1000;
        final int THREADS = 4;

        Random rnd = new Random();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            numbers.add(rnd.nextInt(100) + 1);
        }

        long startSeq = System.nanoTime();
        long seqSum = 0;
        for (int x : numbers) {
            seqSum += x;
        }
        long seqTime = System.nanoTime() - startSeq;

        long[] partSums = new long[THREADS];
        Thread[] threads = new Thread[THREADS];
        int chunk = N / THREADS;

        long startPar = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            final int id = t;
            final int from = id * chunk;
            final int to = (id == THREADS - 1) ? N : from + chunk;

            threads[id] = new Thread(() -> {
                long s = 0;
                for (int i = from; i < to; i++) {
                    s += numbers.get(i);
                }
                partSums[id] = s;
            });
            threads[id].start();
        }

        for (Thread th : threads) {
            th.join();
        }

        long parSum = 0;
        for (long s : partSums) {
            parSum += s;
        }
        long parTime = System.nanoTime() - startPar;

        System.out.println("Последовательно: сумма = " + seqSum + ", время = " + seqTime + " нс (" + seqTime / 1_000_000.0 + " мс)");
        System.out.println("Параллельно (4 потока): сумма = " + parSum + ", время = " + parTime + " нс (" + parTime / 1_000_000.0 + " мс)");
        System.out.println(seqSum == parSum ? "Суммы совпадают" : "ОШИБКА: суммы не совпадают!");
        System.out.println(parTime < seqTime ? "Параллельный вариант быстрее" : "Последовательный вариант быстрее");
    }
}