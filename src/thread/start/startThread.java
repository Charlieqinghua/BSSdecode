package thread.start;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import thread.GetDirFile;
import thread.SCDRMutiThreadGetSubFileBean;
import tools.Dir;

import all.DicTion;

public class startThread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stubInputStream in = new BufferedInputStream(new FileInputStream(path));
		String inputdir=null;
		String outputdir=null;
		String movedir=null;
		String threadnum=null;
		if (args.length == 0) 
		{
			System.out.println("no configue file");
		}
		else
		{
			InputStream in=null;
			ResourceBundle rb=null;
			try {
				in = new BufferedInputStream(new FileInputStream(args[0]));
				rb = new PropertyResourceBundle(in);
			}
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inputdir = rb.getString("inputdir");
			outputdir = rb.getString("outputdir");
			threadnum=rb.getString("threadnum");
			movedir=rb.getString("movedir");
			System.out.println("out"+outputdir+" "+"input"+" "+inputdir+" "+threadnum+" "+movedir);
		}
//		inputdir = "D:\\解码\\zjfee\\scdr\\test\\";
//		outputdir = "D:\\解码\\zjfee\\scdr\\move\\";
//		movedir="d:\\data\\move\\";//reading file需要移动的目录
		//threadnum="3";
		DicTion.movedirstr=movedir;
		DicTion.movedir=new Dir(movedir);//初始化reading之后移动的目录
		
		GetDirFile read=new GetDirFile(inputdir);
		
		//read.start();
		int writethreadnum=Integer.parseInt(threadnum);
		ExecutorService ServiceExecutors=Executors.newFixedThreadPool(writethreadnum);
		ServiceExecutors.submit(read);
		for(int i=0;i<writethreadnum;i++)
		{
			SCDRMutiThreadGetSubFileBean onethread=new SCDRMutiThreadGetSubFileBean(outputdir);
			ServiceExecutors.submit(onethread);
			//onethread.start();
		}
		ServiceExecutors.submit(read);

	}

}
