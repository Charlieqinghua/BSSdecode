package unicom;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;


//输出为压缩文件
public class GzFile extends CFile {
	
	public GzFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GzFile(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public void writeHead(String str) throws IOException
	{
		writeOneRow(str+","+"\r\n");
	}
	public void writeOneRow(String str) throws IOException
	{

		byte[] b=null;
		if(str!=null)
		{
			b=str.getBytes();
		}
		else
		{
			return;
		}
		BufferedOutputStream bf = new BufferedOutputStream( new GZIPOutputStream( new FileOutputStream(f,true)));
		bf.write(b);
		bf.close();
		
	}

}
