package blockchain;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Blockchain implements Serializable {

    private static final long serialVersionUID = 1L;

    private LinkedList<Block> validatedBlocks = new LinkedList<>();
    private LinkedList<String> pendingMessages = new LinkedList<>();
    private AtomicInteger numberOfZeros = new AtomicInteger();
    private static final String increaseN = "N was increased to ";
    private static final String decreaseN = "N was decreased to ";
    private static final String sameN = "N stays the same";

    public Blockchain(){
        try{
            Blockchain deserializedBlockchain = (Blockchain) SerializationUtils.deserialize("\\Blockchain.txt");
            for (Block block : deserializedBlockchain.validatedBlocks) {
                if(validate(block)){
                    validatedBlocks.add(block);
                } else {
                    throw new Exception("Blockchain is not valid\nBlocks hash don't match\n");
                }
            }
            pendingMessages = deserializedBlockchain.pendingMessages;
            numberOfZeros = deserializedBlockchain.numberOfZeros;
        } catch(Exception e) {
            addMessage("no messages");
        }
    }

    public synchronized boolean addBlock(Block block){
        if (validate(block)) {
            validatedBlocks.add(block);
            pendingMessages.removeAll(Stream.of(block.getData().split("\n")).collect(Collectors.toCollection(LinkedList::new)));
            changeN(block);
            try{
                SerializationUtils.serialize(this,"\\Blockchain.txt");
            } catch(IOException e){
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public void addMessage(String message){
        pendingMessages.add(message);
    }

    public Block newCandidateBlock(String creator){
        String predecessorHash = validatedBlocks.isEmpty() ? "0" : validatedBlocks.getLast().getHash();
        String data = validatedBlocks.isEmpty() ? pendingMessages.getFirst() : (pendingMessages.isEmpty() ? "no messages" : pendingMessages.stream().map(x -> "\n"+x).collect(Collectors.joining()));
        return new Block(
                creator,
                validatedBlocks.size()+1,
                numberOfZeros.get(),
                predecessorHash,
                data);
    }

    private synchronized boolean validate(Block block){

        String zeros = new String(new char[numberOfZeros.get()]).replace("\0", "0");

        if(validatedBlocks.isEmpty() && block.getPredecessorHash().equals("0")){
            return true;
        } else if(validatedBlocks.getLast().getHash().equals(block.getPredecessorHash())
                    && block.getHash().startsWith(zeros)) {
            return true;
        } else {
            return false;
        }
    }

    private synchronized void changeN(Block block){

        long[] duration = block.getDuration();

        if(duration[1] <= 5 && duration[2] == 0 && duration[3] == 0 && duration[4] == 0){
            numberOfZeros.incrementAndGet();
            block.setChangeN(increaseN+numberOfZeros);
        } else if(duration[2] >= 1 || duration[3] > 0 || duration[4] > 0){
            numberOfZeros.decrementAndGet();
            block.setChangeN(decreaseN+numberOfZeros);
        } else{
            block.setChangeN(sameN);
        }
    }

    public void printChain(){
        for (Block b : validatedBlocks) {
            System.out.println(b.toString());
        }
    }

    public LinkedList<String> getPendingMessages() {
        return pendingMessages;
    }

    public int size(){
        return validatedBlocks.size();
    }
}
