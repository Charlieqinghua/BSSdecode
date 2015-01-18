package templateallfield;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
/**
 * @param args
 *  使用 GzNOFileToByte可以直接读取txt 文件，所以有时候在测试时使用
 */

public class GzNOFileToByte extends GzFileToByte {

	
	
	
	public void  readPacket()
	{
		InputStream in=null;
		try {
				in =  new FileInputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		BufferedInputStream  bis=new BufferedInputStream (in);
		int size=maxlen;
		b=new byte[size];
		try {
			filesize=bis.read(b, 0, size);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
