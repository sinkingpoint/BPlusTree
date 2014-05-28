package dns.bplustree;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class BPlusTree<K extends Comparable<K>, V> implements Iterable<KeyValuePair<K, V>>{
    private BPlusTreeHeaderNode<K, V> header;
	private BPlusTreeFile<K, V> file;

	private BPlusTreeType<K> keyType;
    private BPlusTreeType<V> valueType;

	public BPlusTree(String fileName, BPlusTreeType<K> _keyType, BPlusTreeType<V> _valueType){
	  keyType = _keyType;
	  valueType = _valueType;
	  if(!new File(fileName).exists())header = new BPlusTreeHeaderNode<K, V>();
      try {
        file = new BPlusTreeFile<K, V>(fileName, _keyType, _valueType);

        if(header == null){
          header = new BPlusTreeHeaderNode<K, V>(file.read(0));
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      header.addObserver(file);
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
	  file.commit();
	}

	@Override
	public Iterator<KeyValuePair<K, V>> iterator() {
		Integer currID = header.getRoot();
		if(currID == null)return new BPlusTreeIterator(null);

		while(!currID.equals(file.getNode(currID).getMinimumChild())){
			currID = file.getNode(currID).getMinimumChild();
		}

		return new BPlusTreeIterator(currID);
	}

	private class BPlusTreeIterator implements Iterator<KeyValuePair<K, V>>{

		private Queue<KeyValuePair<K, V>> next;
		private int nextID;

		public BPlusTreeIterator(Integer curr){
			next = new LinkedList<KeyValuePair<K, V>>();
			if(curr == null){
				nextID = 0;
			}
			else{
				BPlusTreeLeafNode<K, V> node = (BPlusTreeLeafNode<K, V>)file.getNode(curr);
				for(Iterator<KeyValuePair<K, V>> i = node.iterator();i.hasNext();){
					next.offer(i.next());
				}

				nextID = node.getNextID();
			}
		}

		@Override
		public boolean hasNext() {
			return !next.isEmpty() || nextID != 0;
		}

		@Override
		public KeyValuePair<K, V> next() {
			if(!hasNext())return null;
			KeyValuePair<K, V> value = next.poll();
			if(next.isEmpty()){
				if(nextID == 0)return value;
				BPlusTreeLeafNode<K, V> node = (BPlusTreeLeafNode<K, V>)file.getNode(nextID);
				for(Iterator<KeyValuePair<K, V>> i = node.iterator();i.hasNext();){
					next.offer(i.next());
				}
				nextID = node.getNextID();
			}

			return value;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
