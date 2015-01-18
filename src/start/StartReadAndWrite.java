package start;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.PaseDecode;
import com.SGWLexObject;

public class StartReadAndWrite {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		String fname="D:\\解码\\zjfee\\sgw\\140914120235.l1sf02.zj2014091400016021.dat";
		PaseDecode read1=new PaseDecode();
		read1.setFname(fname);
		read1.getFileFromName();
		//UrlMutiReadThread read2=new UrlMutiReadThread("18611701625");
		
		threadPool.submit(read1);
		//threadPool.submit(read2);
		
		for(int i=0;i<10;i++)
		{
			SGWLexObject down=new SGWLexObject();
			threadPool.submit(down);
		}
		

	}

}
