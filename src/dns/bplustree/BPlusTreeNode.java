package dns.bplustree;

import dns.Block;

public interface BPlusTreeNode<K extends Comparable<K>, V> {
	public V find(K key);
	public KeyValuePair<K, Integer> add(K key, V value);
	public Integer getMinimumChild();
	public int getID();
	public Block write();
}
