package dns.bplustree;

import dns.Block;
import dns.Bytes;

public class BPlusTreeHeaderNode<K extends Comparable<K>, V> extends BPlusTreeNode<K, V> {

	private Integer root;

	public BPlusTreeHeaderNode() {
	}

	public BPlusTreeHeaderNode(byte[] data) {
		int res = Bytes.bytesToInt(data, 0);
		if (res == -1) {
			root = null;
		} else {
			root = res;
		}
	}

	public Integer getRoot() {
		return root;
	}

	public void setRoot(Integer _root) {
		root = _root;
		setChanged();
		notifyObservers("Root Changed");
	}

	@Override
	public V find(K key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public KeyValuePair<K, Integer> add(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getMinimumChild() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public Block write() {
		Block block = new Block(BPlusTreeFile.BLOCK_SIZE);
		if (root == null) {
			block.setInt(-1, 0);
		} else {
			block.setInt(root, 0);
		}

		return block;
	}

}
