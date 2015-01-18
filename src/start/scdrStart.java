package start;

import templateallfield.DefineForest;
import templateallfield.FileOutResult;
import templateallfield.FileOutResultAllField;
import templateallfield.ScdrPaseDecode;
import templateallfield.StandAccess;
import templateallfield.TmplatePaseDecode;
import all.DicTion;

public class scdrStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="D:\\解码\\zjfee\\scdr\\140917111659.r1sf04.b07074937.dat.gz";
		String fout="d:\\data\\hexscdr.txt";
		DefineForest dic=DicTion.scdrdic;
		TmplatePaseDecode scdrdecode=new ScdrPaseDecode();
		FileOutResult out=new FileOutResultAllField();
		StandAccess start=new StandAccess(scdrdecode,out);
		start.startDecode(dic, path);
		start.outPutFile(fout);

	}

}
