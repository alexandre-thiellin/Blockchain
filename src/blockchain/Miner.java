package blockchain;

import java.util.Random;

public class Miner implements Runnable{

    private final String name;
    private final Blockchain blockchain;

    public Miner(String name, Blockchain blockchain){
        this.name = name;
        this.blockchain = blockchain;
    }

    @Override
    public void run() {

        Random random = new Random();
        int prob = 0;
        while(!blockchain.getPendingMessages().isEmpty() && blockchain.size() < 5){

            prob = random.nextInt(100);
            if(prob < 75){
                blockchain.addMessage(name+": new message posted !");
            }

            Block candidateBlock = blockchain.newCandidateBlock(name);
            blockchain.addBlock(candidateBlock);
        }
    }
}
