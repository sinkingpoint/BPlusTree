package dns.bplustree;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FixedSizeList<E> extends AbstractList<E>{
	private E[] elements;
	private int size;
	
	public FixedSizeList(int maxSize){
		elements = (E[])new Object[maxSize];
	}
	
	public boolean isFull(){
		return size == elements.length;
	}
	
	@Override
	public E get(int index) {
		if(index < 0 || index >= size){
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return elements[index];
	}
	
	public List<E> removeSubList(int fromIndex, int toIndex){
		if(fromIndex < 0 || toIndex < 0 || fromIndex > toIndex){
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		}
		
		E[] data = (E[])new Object[toIndex - fromIndex];
		System.arraycopy(elements, fromIndex, data, 0, toIndex - fromIndex);
		System.arraycopy(elements, toIndex, elements, fromIndex, size - toIndex);
		size -= (toIndex - fromIndex);
		return new ArrayList<E>(Arrays.asList(data));
	}
	
	@Override
	public void add(int index, E data){
		if(size < elements.length && index < size){
			for(int i = size - 1;i >= index;i --){
				elements[i + 1] = elements[i];
			}
			elements[index] = data;
			size ++;
		}
		else if(index >= size){
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}
	
	@Override
	public boolean add(E data){
		if(size < elements.length){
			elements[size ++] = data;
			return true;
		}
		
		return false;
	}
	
	@Override
	public int size() {
		return size;
	}
}
