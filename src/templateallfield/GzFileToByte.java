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
 */
public class GzFileToByte {

	public byte[] getB() {
		return b;
	}
	public void setB(byte[] b) {
		this.b = b;
	}
	static int maxlen=9000000;//最大支持文件是 9mb 
	String fname;
	File file;
	byte b[];
	int filesize=0;
	public int getFilesize() {
		return filesize;
	}
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	public String getFname() {
		return fname;
	}
	public File getFile() {
		
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public boolean getFileFromName()
	{
		boolean flag=false;
		file=new File(fname);
		if(file!=null)
		{
			if((file.exists())&&(file.isFile()))
			{
				flag=true;
			}
		}
		return flag;
	}
	public void  readPacket()
	{
		InputStream in=null;
		try {
				if(filter())
				{
					in =  new GZIPInputStream(new FileInputStream(file));
				}
				else
				{
					in=new FileInputStream(file);
				}
			//in =  new GZIPInputStream(new FileInputStream(file));
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
	//自动判断文件的类型是否是压缩文件
	public boolean filter()
	{
		boolean flag=false;
		if(this.fname!=null)
		{
			int p=this.fname.lastIndexOf(".");
			String type=this.fname.substring(p);
			if(type.equals(".gzreading")||type.equals(".gz")) flag=true;  
				
		}
		return flag;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
