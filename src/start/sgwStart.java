package start;

import templateallfield.DefineForest;
import templateallfield.StandAccess;
import all.DicTion;

public class sgwStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="D:\\解码\\zjfee\\sgw\\140914120235.l1sf02.zj2014091400016021.dat.gz";
		
		String fout="d:\\data\\hexsgwgz.txt";
		DefineForest dic=DicTion.sgwdic;
		StandAccess start=new StandAccess();
		
		start.startDecode(dic, path);
		start.outPutFile(fout);

	}

}
