package thread;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
//
/**
 * @author Administrator
 * 实现一个简单的阻塞队列
 *
 */
public class OwnQueue <T>  {
	 Queue<T> queque;
	int max=0;
	
	public OwnQueue(int max) {
		super();
		this.max = max;
	}
	public int getMax() {
		//queque=new LinkedList<T>();
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public synchronized void  add(T f)
	{
		while(queque.size()==this.max)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(queque.size()==0)
		{
			queque.add(f);
			notifyAll();
		}
		this.queque.add(f);
	}
	public synchronized boolean contains(T f)
	{
		boolean flag=false;
		if(queque.contains(f)) flag=true;
		return flag;
	}
	public synchronized T poll()
	{
		while(this.queque.size()==0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(this.queque.size()==this.max)
		{
			notifyAll();
		}
		return this.queque.poll();
		
	}
	
	

}
