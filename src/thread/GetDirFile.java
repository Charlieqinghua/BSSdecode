package thread;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import all.DicTion;

/**
 * 类 GetDirFile 从目录下面读取文件列表，并将文件名加入队列 path 读入的源文件路径， specialword 要查的特殊词汇
 * 
 * @throws Throwable
 */
public class GetDirFile extends Thread {
	String path;
	static Queue<File> queque;

	public GetDirFile(String path) {
		super();
		this.path = path;
		queque = new LinkedList<File>();
	}

	public Queue getQueque() {
		File fileDir = new File(path);
		if (fileDir.isDirectory()) {
			File[] textFiles = fileDir.listFiles();
			for (File f : textFiles) {
				if (f.isFile() && f.getName().endsWith(DicTion.suffix)) {
					if (!queque.contains(f))
					{	
						queque.add(f);
						System.out.println("reading thread id is "+this.getId()+"fname+"+f.getName());
					}
				}
			}
		}

		return queque;
	}

	public void setQueque(Queue queque) {
		this.queque = queque;
	}

	static public void visit() {
		System.out.println(queque.size());

	}

	public void run() {

		while (true) 
		{
			
			System.out.println("thread id is "+this.getId()+"reading file");
			synchronized (GetDirFile.queque) 
			{
				getQueque();
			}
			try 
			{
				this.sleep(1000);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		
	}

	public static void main(String args[]) {
		long before = System.currentTimeMillis();
		String path = "E:\\SMSCDR_20130901\\SMSCDR_20130901\\";
		GetDirFile dir = new GetDirFile(path);
		dir.start();
		
	}

}
