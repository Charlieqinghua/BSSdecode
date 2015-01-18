package test;

import java.io.File;

public class testFileName {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f=new File("D:\\解码\\zjfee\\scdr\\test\\1111scdr.dat.gzreading");
		String fname=f.getName();
		String sub=fname.substring(0, fname.lastIndexOf("."));
		String substr=sub+".gz";

	}

}
