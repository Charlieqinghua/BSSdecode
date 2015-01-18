package test;

import java.io.IOException;

import unicom.CFile;

public class WriteGzFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s="wwwwwh";
		CFile f=new CFile("d:\\data\\outgz.gz");
		try {
			f.writeOneRowToGz(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
