package templateBean;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import all.DicTion;

import unicom.CFile;
import unicom.WordUnit;


/**
 * @author Administrator
 * 没有标签的输出
 *
 */
public class FileOutResultAllFieldBean  {
	List<RowBean> wordlist=null;
	String header=DicTion.scdrhead;
	public static int listsize=DicTion.fieldsize;//输出文件的列数
	//protected static final Log log = LogFactory.getLog(FileOutResultAllFieldBean.class);
	//protected Runtime rt = Runtime.getRuntime();//查看内存情况
	public FileOutResultAllFieldBean(String header) {
		super();
		this.header = header;
	}
	
	public FileOutResultAllFieldBean() {
		super();
	}

	public static void setListSize(int size)
	{
		listsize=size;
	}
	public void FileOutput(String fout,String pname)
	{
		
		CFile cf=new CFile(fout);
		try {
			cf.writeHead(this.header);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer sb=new StringBuffer();
		Iterator<RowBean> it=null;
		//log.info("start:"+rt.freeMemory());
		for(it=wordlist.iterator();it.hasNext();)
		{
			RowBean bean=(RowBean)it.next();
			for(int i=0;i<listsize;i++)
			{
				WordUnit w=(WordUnit)bean.getWordlist()[i];
//				if(i==0)
//				{
//					if(pname!=null) sb.append(pname);
//				}
				if(w==null) 
				{
					if((i==13)||(i==14))
					{
						sb.append("0");
					}
					else
					{
						sb.append("null");
					}
					sb.append(",");
				}
				else
				{
					String tag=null;
					try {
						tag=w.getID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sb.append(w.getValue());
					sb.append(",");
				}
			}
			if(pname!=null) sb.append(pname);
			sb.append("\r\n");//一行结束加换行
			it.remove();
				
		}
		
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//wordlist.clear();
		//log.info("end:"+rt.freeMemory());
		
		
	}
	public void FileOutput(String fout)
	{
		
		CFile cf=new CFile(fout);
		try {
			cf.writeHead(this.header);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer sb=new StringBuffer();
		Iterator<RowBean> it=null;
		//log.info("start:"+rt.freeMemory());
		for(it=wordlist.iterator();it.hasNext();)
		{
			RowBean bean=(RowBean)it.next();
			for(int i=0;i<listsize;i++)
			{
				WordUnit w=(WordUnit)bean.getWordlist()[i];
//				if(i==0)
//				{
//					if(fout!=null) sb.append(fout);
//				}
				if(w==null) 
				{
					if((i==13)||(i==14))
					{
						sb.append("0");
					}
					else
					{
						sb.append("null");
					}
					sb.append(",");
				}
				else
				{
					String tag=null;
					try {
						tag=w.getID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sb.append(w.getValue());
					sb.append(",");
				}
			}
			if(fout!=null) sb.append(fout);
			sb.append("\r\n");//一行结束加换行
			it.remove();
				
		}
		
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//wordlist.clear();
//		/log.info("end:"+rt.freeMemory());
		
		
	}
	public List<RowBean> getWordlist() {
		return wordlist;
	}
	public void setWordlist(List<RowBean> wordlist) {
		this.wordlist = wordlist;
	}

}
