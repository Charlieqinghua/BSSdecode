package templateBean;

import java.io.IOException;
import java.util.Iterator;

import unicom.CFile;
import unicom.WordUnit;

public class FileOutResultAllFieldBeanAddName extends FileOutResultAllFieldBean {
	public FileOutResultAllFieldBeanAddName(String header) {
		super(header);
		// TODO Auto-generated constructor stub
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
				if(i==0)
				{
					if(fout!=null) sb.append(pname);
				}
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
			sb.append("\r\n");//一行结束加换行
				
		}
		
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//log.info("start:"+rt.freeMemory());
		wordlist.clear();
		//log.info("end:"+rt.freeMemory());
		
		
	}
}
