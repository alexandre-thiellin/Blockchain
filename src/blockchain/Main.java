package blockchain;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Blockchain blockchain = new Blockchain();

        long start2 = new Date().getTime();

        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < poolSize; i++) {
            executor.submit(new Miner(i+"", blockchain));
        }

        executor.shutdown();

        while(!executor.isTerminated()){
            Thread.sleep(100);
        }

        long end2 = new Date().getTime();
        blockchain.printChain();

        System.out.println((end2-start2)/1000f);
    }
}
