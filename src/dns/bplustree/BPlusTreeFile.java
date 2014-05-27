package dns.bplustree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dns.Block;
import dns.BlockFile;

public class BPlusTreeFile<K extends Comparable<K>, V> extends BlockFile{
  private Map<Integer, BPlusTreeNode<K, V>> toBeWritten;
  private BPlusTreeType<K> keyType;
  private BPlusTreeType<V> valueType;
  private int blockNum;
  
  public static final int BLOCK_SIZE = 1024;
  
  public BPlusTreeFile(String name, int blockSize, BPlusTreeType<K> _keyType, BPlusTreeType<V> _valueType) throws IOException {
    super(name, blockSize);
    toBeWritten = new HashMap<Integer, BPlusTreeNode<K, V>>();
    keyType = _keyType;
    valueType = _valueType;
    blockNum = size + 1;
  }
  
  public int getNextBlockID(BPlusTreeNode<K, V> node){
    toBeWritten.put(blockNum, node);
    System.out.println("New Node: " + this + ": " + blockNum);
    try {
      write(node.write().getBytes());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return blockNum ++;
  }
  
  public void touch(BPlusTreeNode<K, V> node){
    toBeWritten.put(node.getID(), node);
  }
  
  public void commit() throws IOException{
    System.out.println("Commiting changes: " + toBeWritten.size() + " values");
    for(BPlusTreeNode<K, V> node : toBeWritten.values()){
      Block block = node.write();
      write(block.getBytes(), node.getID());
    }
    
    toBeWritten.clear();
  }
  
  public void rollback(){
    toBeWritten.clear();
  }
  
  public BPlusTreeNode<K, V> getNode(int id){
    //If the node is in memory
    if(toBeWritten.containsKey(id)){
      return toBeWritten.get(id);
    }
    else{
      //BPlusTreeLeafNode node = new BPlusTreeLeafNode();
      //Load into memory
      //Return
      try {
        byte[] data = this.read(id);
        if(data[0] == 0){
          BPlusTreeNode<K, V> node = new BPlusTreeInternalNode<K, V>(this, id, keyType, valueType);
          toBeWritten.put(node.getID(), node);
          return node;
        }
        else if(data[0] == 1){
          BPlusTreeNode<K, V> node = new BPlusTreeLeafNode<K, V>(this, id, keyType, valueType);
          toBeWritten.put(node.getID(), node);
          return node;
        }
        else{
          throw new IllegalStateException("Invalid Block Type: " + data[0]);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    return null;
  }

}
