package dns.bplustree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dns.Block;
import dns.Bytes;

public class BPlusTreeLeafNode<K extends Comparable<K>, V> implements BPlusTreeNode<K, V>, Iterable<KeyValuePair<K, V>>{

	private List<KeyValuePair<K, V>> values;
	private int nextID;
	private int id;
	private BPlusTreeFile<K, V> file;
	private BPlusTreeType<K> keyType;
	private BPlusTreeType<V> valueType;
	
	private int MAX_LEAF_SIZE;
	
	public BPlusTreeLeafNode(BPlusTreeFile<K, V> _file, BPlusTreeType<K> _keyType, BPlusTreeType<V> _valueType){
      init(_file, _file.getNextBlockID(this), _keyType, _valueType);
	}
	
	public BPlusTreeLeafNode(BPlusTreeFile<K, V> _file, int _id, BPlusTreeType<K> _keyType, BPlusTreeType<V> _valueType) throws IOException{
	  init(_file, _id, _keyType, _valueType);
	  byte[] data = _file.read(_id);
      int size = Bytes.bytesToInt(data, 1);
      for(int i = 0;i < size;i ++){
        K key = keyType.read(data, 5 + i * (keyType.getSize() + valueType.getSize()));
        V value = valueType.read(data, 5 + keyType.getSize() + i * (keyType.getSize() + valueType.getSize()));
        values.add(new KeyValuePair<K, V>(key, value));
      }
	}
	
	private void init(BPlusTreeFile<K, V> _file, int _id, BPlusTreeType<K> _keyType, BPlusTreeType<V> _valueType){
	  file = _file;
      id = _id;
      keyType = _keyType;
      valueType = _valueType;
      values = new ArrayList<KeyValuePair<K, V>>(MAX_LEAF_SIZE + 1);
      MAX_LEAF_SIZE = (BPlusTreeFile.BLOCK_SIZE - 5) / (keyType.getSize() + valueType.getSize());
	}
	
	public int getNextID(){
		return nextID;
	}
	
	@Override
	public V find(K key) {
		for(int i = 0;i < values.size();i ++){
			if(values.get(i).getKey().equals(key)){
				return values.get(i).getValue();
			}
		}
		
		return null;
	}
	
	@Override
	public KeyValuePair<K, Integer> add(K key, V value) {
		if(values.size() < MAX_LEAF_SIZE){
			insertValue(key, value);
			return null;
		}
		
		return splitNode(key, value);
	}
	
	private void insertValue(K key, V value){
		for(int i = 0;i < values.size();i ++){
		    int cmp = key.compareTo(values.get(i).getKey());
		    if(cmp == 0){
		      values.remove(i);
		    }
		    
		    if(cmp <= 0){
				values.add(i, new KeyValuePair<K, V>(key, value));
				return;
			}
		}
		
		values.add(new KeyValuePair<K, V>(key, value));
	}
	
	private KeyValuePair<K, Integer> splitNode(K key, V value){
		insertValue(key, value);
		if(values.size() <= MAX_LEAF_SIZE)return null;
		BPlusTreeLeafNode<K, V> sibling = new BPlusTreeLeafNode<K, V>(file, keyType, valueType);
		int mid = (values.size() + 1) / 2;
		List<KeyValuePair<K, V>> removed = new ArrayList<KeyValuePair<K, V>>(values.subList(mid, values.size()));
		values.removeAll(removed);
		
		sibling.values.addAll(removed);
		sibling.nextID = nextID;
		nextID = sibling.id;
		return new KeyValuePair<K, Integer>(sibling.values.get(0).getKey(), sibling.id);
	}

	@Override
	public Integer getMinimumChild() {
		return id;
	}

	@Override
	public Iterator<KeyValuePair<K, V>> iterator() {
		return values.iterator();
	}
    
    /**
     * 1 byte for number of nodes
     * (60 bytes for string
     * 4 bytes for key) MAX_LEAF_SIZE Pairs
     * 4 bytes for next block id
     */
    public Block write(){
      Block block = new Block(BPlusTreeFile.BLOCK_SIZE);
      block.setByte((byte)1, 0);
      if(values == null)return block;
      block.setInt(values.size(), 1);
      for(int i = 0;i < values.size();i ++){
        keyType.write(values.get(i).getKey(), block, 5 + i * (keyType.getSize() + valueType.getSize()));
        valueType.write(values.get(i).getValue(), block, 5 + keyType.getSize() + i * (keyType.getSize() + valueType.getSize()));
      }
      
      return block;
    }

    @Override
    public int getID() {
      return id;
    }
}
