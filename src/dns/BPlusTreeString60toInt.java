package dns;

import dns.bplustree.BPlusTree;
import dns.bplustree.IntType;
import dns.bplustree.StringType;

/**
  Implements a B+ tree in which the keys  are Strings (with
  maximum length 60 characters) and the values are integers 
*/

public class BPlusTreeString60toInt extends BPlusTree<String, Integer>{

  public BPlusTreeString60toInt(String fileName) {
    super(fileName, new StringType(60), new IntType());
  }
	
}
