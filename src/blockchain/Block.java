package blockchain;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Block implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String creator;
    private final long id;
    private final long timeStamp;
    private long magicNumber;
    private final String predecessorHash;
    private final String hash;
    private final String data;
    private final long[] duration = new long[5];
    private String durationString;
    private String changeN = "";

    public Block(String creator, long id, int numberOfZeros, String predecessorHash, String data) {
        this.creator = creator;
        this.id = id;
        this.timeStamp = new Date().getTime();
        this.predecessorHash = predecessorHash;
        this.data = data;
        hash = calculateHash(numberOfZeros);
    }

    public Block(String data){
        creator = "pending";
        id = 0;
        timeStamp = 0;
        predecessorHash = "0";
        this.data = data;
        hash = "pending";
    }

    private String calculateHash(int numberOfZeros){
        String hashTemp = "";
        Random rd = new Random();
        boolean proved = false;
        String zeros = new String(new char[numberOfZeros]).replace("\0", "0");
        long start = new Date().getTime();
        while(!proved){
            magicNumber = rd.nextInt(100_000_000);
            hashTemp = StringUtil.applySha256(new StringBuilder()
                    .append(creator)
                    .append(id)
                    .append(timeStamp)
                    .append(magicNumber)
                    .append(predecessorHash)
                    .append(data).toString());
            if(hashTemp.startsWith(zeros.substring(0,numberOfZeros))){
                proved = true;
            }
        }
        long end = new Date().getTime();
        long time = end - start;
        long millis = time % 1000;
        duration[0] = millis;
        long seconds = (time / 1000) % 60;
        duration[1] = seconds;
        long minutes = (time / (1000 * 60)) % 60;
        duration[2] = minutes;
        long hours = (time / (1000 * 60 * 60)) % 24;
        duration[3] = hours;
        long days = time / (1000 * 60 * 60 * 24);
        duration[4] = days;
        StringBuilder sb = new StringBuilder();
        if(days > 0){
            sb.append(days).append(days == 1 ? " day " : " days ");
        }
        if(hours > 0){
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
        }
        if(minutes > 0){
            sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }
        if(seconds > 0 || millis > 0){
            sb.append(seconds).append(".");
            if(millis < 10){
                sb.append("00").append(millis).append(seconds == 1 || seconds == 0 ? " second" : " seconds");
            } else if(millis < 100){
                sb.append("0").append(millis).append(seconds == 1 || seconds == 0 ? " second" : " seconds");
            } else{
                sb.append(millis).append(seconds == 1 || seconds == 0 ? " second" : " seconds");
            }
        } else {
            sb.append("0.0 seconds");
        }
        durationString = sb.toString();
        return hashTemp;
    }

    public String getHash() {
        return hash;
    }

    public String getPredecessorHash() {
        return predecessorHash;
    }

    public long[] getDuration() {
        return duration;
    }

    public void setChangeN(String changeN) {
        this.changeN = changeN;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Block:\n")
                .append("Created by miner # ").append(creator).append("\n")
                .append("Id: ").append(id).append("\n")
                .append("Timestamp: ").append(timeStamp).append("\n")
                .append("Magic number: ").append(magicNumber).append("\n")
                .append("Hash of the previous block:\n")
                .append(predecessorHash).append('\n')
                .append("Hash of the block:\n")
                .append(hash).append("\n")
                .append("Block data: ")
                .append(data).append("\n")
                .append("Block was generating for ").append(durationString).append("\n")
                .append(changeN).append("\n").toString();
    }
}
