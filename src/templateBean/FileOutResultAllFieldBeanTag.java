package templateBean;

import java.io.IOException;
import java.util.Iterator;

import unicom.CFile;
import unicom.WordUnit;
import all.DicTion;
/**
 * @author Administrator
 * 包含标签的输出
 *
 */
public class FileOutResultAllFieldBeanTag extends FileOutResultAllFieldBean{
	public FileOutResultAllFieldBeanTag(String header) {
		super(header);
		// TODO Auto-generated constructor stub
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
		
		for(it=wordlist.iterator();it.hasNext();)
		{
			RowBean bean=(RowBean)it.next();
			for(int i=0;i<listsize;i++)
			{
				WordUnit w=(WordUnit)bean.getWordlist()[i];
				if(w==null) 
				{
					sb.append("null");
					sb.append("-");
					sb.append("null");
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
					sb.append(w.getID());
					sb.append("-");
					sb.append(w.getValue());
					sb.append(",");
				}
			}
			if(fout!=null) sb.append(fout);
			sb.append("\r\n");
			it.remove();
				
		}
		
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//log.info(rt.freeMemory());
		//wordlist.clear();
		//log.info(rt.freeMemory());
		
		
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
		
		for(it=wordlist.iterator();it.hasNext();)
		{
			RowBean bean=(RowBean)it.next();
			for(int i=0;i<listsize;i++)
			{
				WordUnit w=(WordUnit)bean.getWordlist()[i];
				if(w==null) 
				{
					sb.append("null");
					sb.append("-");
					sb.append("null");
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
					sb.append(w.getID());
					sb.append("-");
					sb.append(w.getValue());
					sb.append(",");
				}
			}
			if(pname!=null) sb.append(pname);
			sb.append("\r\n");
			it.remove();
				
		}
		
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//log.info(rt.freeMemory());
		//wordlist.clear();
		//log.info(rt.freeMemory());
		
		
	}

}
