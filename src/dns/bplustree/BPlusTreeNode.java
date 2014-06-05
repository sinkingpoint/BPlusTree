package dns.bplustree;

import java.util.List;
import java.util.Observable;

import dns.Block;

public abstract class BPlusTreeNode<K extends Comparable<K>, V> extends Observable{
	public abstract List<V> find(K key);
	public abstract KeyValuePair<K, Integer> add(K key, V value);
	public abstract Integer getMinimumChild();
	public abstract int getID();
	public abstract Block write();
}
