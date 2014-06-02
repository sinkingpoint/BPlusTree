package dns.bplustree;

import dns.Block;

public interface BPlusTreeType<E> {
  public int getSize();
  public void write(E data, Block block, int offset);
  public E read(byte[] data, int offset);
}
