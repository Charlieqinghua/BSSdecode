package templateBean;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import all.Area;

import unicom.CFile;
import unicom.GzFile;
import unicom.WordUnit;

public class FileOutResultAllFieldBeanGz extends FileOutResultAllFieldBean {
	public FileOutResultAllFieldBeanGz(String header) {
		super(header);
		// TODO Auto-generated constructor stub
	}
	public void FileOutput(String fout)
	{
		
		
		
		StringBuffer sb=new StringBuffer();
		Iterator<RowBean> it=null;
		//遍历所有行
		int rowcount=0;
		for(it=wordlist.iterator();it.hasNext();)
		{
			RowBean bean=(RowBean)it.next();
			WordUnit lacunit=bean.wordlist[6];
			WordUnit cellunit=bean.wordlist[7];
			String laccell=lacunit.getValue()+cellunit.getValue();
//			if(Area.lacset.contains(laccell))
//			{
				//需要字段 2,3,6,7,13,14,16,18,21,25
				rowcount++;
				//输出字段控制
				for(int i=0;i<listsize;i++)
				{
					if(filterArr(i))
					{
						WordUnit w=(WordUnit)bean.getWordlist()[i];
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
				}
				sb.append("\r\n");//每行输出一个换行
				
			}
				
		//}
		if(rowcount>0)
		{
			CFile cf=new GzFile(fout+".gz");
//			try {
//				//cf.writeHead(this.header);
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			try {
				//cf.writeOneRowToGz(sb.toString());
				cf.writeOneRow(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		wordlist.clear();
		
		
		
	}
	public boolean filterArr(int n)
	{
		boolean flag=false;
		int []a={1,2,6,7,13,14,16,18,21,25};
		//int lengh=a.length;
		//Set isin=new HashSet();
		for(int num:a)
		{
			if(n==num) 
			{
				flag=true;
				break;
			}
		}
		return flag; 
	}

}
