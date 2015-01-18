package thread;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import templateBean.FileOutResultAllFieldBean;
import templateBean.FileOutResultAllFieldBeanTag;
import templateBean.RowBean;
import templateBean.TmplatePaseDecodeBean;
import templateBean.TrafficDataDecodeMergeBean;
import templateBean.pgw.PGWPaseDecodeBean;
import templateBean.pgw.PGWPaseDecodeBeanDebug;
import templateallfield.DefineForest;
import templateallfield.FileOutResult;
import templateallfield.FileOutResultAllField;
import templateallfield.FileOutResultAllFieldOrader;
import templateallfield.ScdrPaseDecode;
import templateallfield.ScdrPaseDecodeAllField;
import templateallfield.StandAccess;
import templateallfield.TmplatePaseDecode;
import templateallfield.TrafficDataDecode;
import templateallfield.TrafficDataDecodeMerge;

import all.DicTion;


public class PGWMutiThreadGetSubFileBean extends Thread {
	
	List<RowBean> RowBeanlist = new LinkedList<RowBean>();//一个文件对应一个wordlist
	TrafficDataDecodeMergeBean afdecode=new TrafficDataDecodeMergeBean();
	TmplatePaseDecodeBean scdrdecode=new PGWPaseDecodeBeanDebug(RowBeanlist,afdecode);
	FileOutResultAllFieldBean out=new FileOutResultAllFieldBeanTag(DicTion.pgwhead);
	String outdir=DicTion.outdir;
		
	public PGWMutiThreadGetSubFileBean(String outputdir ) {
		super();
		outdir=outputdir;
	}
	//使用工厂动态创建后需要修改构造函数
	public PGWMutiThreadGetSubFileBean() {
		super();
	}

	public void run()
	{	
		while(true)
		{
			System.out.println(" write thread id is "+this.getId());
			try {
				this.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			File f=null;
//			synchronized (GetDirFile.queque) 
//			{
//				f=this.getOneFile(GetDirFile.queque);
//			}
			f=this.getOneFile(GetDirFileBlock.queque);
			if(f==null) 
			{
				System.out.println("thread id is "+this.getId()+"file null");
				continue;
			}
			String fname=f.getName();
			String substr=fname.substring(0, fname.lastIndexOf("."));
			String fout=outdir+substr+".gz-decode.csv";
			try 
			{
				scdrdecode.readGzTobytes(f.getAbsolutePath());
				scdrdecode.match(DicTion.pgwdic);
				List<RowBean> wordlist=scdrdecode.getWordlist();
				
				out.setWordlist(wordlist);
				out.FileOutput(fout,fname);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			moveFile(f);//移动读完之后的文件	
			
		}
	}
	public File getOneFile(BlockingQueue<File> queque)
	{
		System.out.println(queque.size());
		File f=null;
		File renamef=null;
		//f=queque.poll();
		try {
			f=queque.poll(20, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(f!=null)
		{
			renamef=renameFile(f);
		}
		return renamef;
	}
	public File renameFile(File f)
	{
		File targetFile1=null;
		if(f.exists())
		{
			try 
			{
				 String path=f.getCanonicalPath();
				 targetFile1 = new File(path+"reading");
				 f.renameTo(targetFile1);
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return targetFile1;
	}
	public boolean moveFile(File f)
	{
		boolean flag=false;
		File targetFile1=null;
		int len=0;
		int sublen=0;
		if(f.exists())
		{
			
				 String name=f.getName();
				 len=name.length();
				 sublen=len-7;
				 if(sublen>0)
				 {
					 name=name.substring(0, sublen);
				 }
				 else
				 {
					 flag=false;
					 return flag;
				 }
				 String tagetname=DicTion.movedirstr+name;
				 targetFile1 = new File(tagetname);
				 if(targetFile1.exists()) 
				 {
					 targetFile1.delete();
				 }
				 if(f.renameTo(targetFile1))
				 {
					 if(targetFile1.isFile())flag=true;
				 }
			
			
		}
		return flag;
	}
	public static  void  main(String args[]) throws Throwable
	{
		
		String dir="D:\\解码\\zjfee\\scdr\\test";
		String outdir="D:\\\u89E3\u7801\\zjfee\\scdr\\move\\";
		GetDirFile read=new GetDirFile(dir);
		read.start();
		for(int i=0;i<3;i++)
		{
			PGWMutiThreadGetSubFileBean onethread=new PGWMutiThreadGetSubFileBean(outdir);
			onethread.start();
		}
	}

 }



