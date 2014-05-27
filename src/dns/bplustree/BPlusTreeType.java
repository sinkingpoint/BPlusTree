package dns.bplustree;

import dns.Block;

public interface BPlusTreeType<K> {
  public int getSize();
  public void write(K data, Block block, int offset);
  public K read(byte[] data, int offset);
}
