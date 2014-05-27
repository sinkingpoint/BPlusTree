package dns.bplustree;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class BPlusTree<K extends Comparable<K>, V>{
    private BPlusTreeHeaderNode<K, V> header;
	private BPlusTreeFile<K, V> file;
	
	private BPlusTreeType<K> keyType;
    private BPlusTreeType<V> valueType;
	
	public BPlusTree(String fileName, BPlusTreeType<K> _keyType, BPlusTreeType<V> _valueType){
	  keyType = _keyType;
	  valueType = _valueType;
	  if(!new File(fileName).exists())header = new BPlusTreeHeaderNode<K, V>();
      try {
        file = new BPlusTreeFile<K, V>(fileName, 1024, _keyType, _valueType);
        
        if(header == null){
          header = new BPlusTreeHeaderNode<K, V>(file.read(0));
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
	}
	
	public V find(K key){
		if(header.getRoot() == null)return null;
		return file.getNode(header.getRoot()).find(key);
	}
	
	public boolean put(K key, V value){
		if(header.getRoot() == null){
			BPlusTreeNode<K, V> newRoot = new BPlusTreeLeafNode<K, V>(file, keyType, valueType);
			newRoot.add(key, value);
			header.setRoot(newRoot.getID());
		}
		else{
			KeyValuePair<K, Integer> krc = file.getNode(header.getRoot()).add(key, value);
			if(krc != null){
				BPlusTreeNode<K, V> newRoot = new BPlusTreeInternalNode<K, V>(file, header.getRoot(), krc, keyType, valueType);
				header.setRoot(newRoot.getID());
			}
		}
		
		return true;
	}
	
	public void flush() throws IOException{
	  file.touch(header);
	  file.commit();
	}
	
	public List<KeyValuePair<K, V>> entryList(){
		/*BPlusTreeNode<K, V> curr = root;
		while(curr.getMinimumChild() != curr.getID()){
			curr = curr.getMinimumChild();
		}
		
		List<KeyValuePair<K, V>> values = new ArrayList<KeyValuePair<K, V>>();
		BPlusTreeLeafNode<K, V> currLeaf = (BPlusTreeLeafNode<K, V>)curr;
		while(currLeaf != null){
			for(KeyValuePair<K, V> pair : currLeaf){
				values.add(pair);
			}
			currLeaf = currLeaf.getNext();
		}
		
		return values;*/
	  return null;
	}
}
