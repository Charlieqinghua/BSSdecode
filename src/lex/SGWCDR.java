package lex;

import java.util.ArrayList;
import java.util.List;

/**
 * @param args
 */
public class SGWCDR {
	List<Byte> segment ;
	

	
	
	
	public SGWCDR() {
		super();
		this.segment = new ArrayList<Byte>();
	}
	public void addByte(byte one)
	{
		segment.add(one);
	}




	public List<Byte> getSegment() {
		return segment;
	}
	public void setSegment(List<Byte> segment) {
		this.segment = segment;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
