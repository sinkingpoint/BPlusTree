package dns;

import dns.bplustree.BPlusTree;
import dns.bplustree.IntType;
import dns.bplustree.StringType;

/**
  Implements a B+ tree in which the keys are integers and the
  values are Strings (with maximum length 60 characters)
*/

public class BPlusTreeIntToString60 extends BPlusTree<Integer, String> {
	
  public BPlusTreeIntToString60(String fileName) {
    super(fileName, new IntType(), new StringType(60));
  }

}
