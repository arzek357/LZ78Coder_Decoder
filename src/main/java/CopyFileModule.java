import java.io.File;

public class CopyFileModule {
    static File checkFileAndBackUniName(File file,String copyFileName){
        int count = 0;
        return checkFile(file,count,copyFileName);
    }
    private static File checkFile(File file,int count,String copyFileName){
        count++;
        if (!file.exists()){
            return file;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(file);
        if (count==1){
            sb.insert(sb.indexOf("."),"_"+copyFileName+count);
        }
        else {
            sb.replace(sb.indexOf(".")-1,sb.indexOf("."),Integer.toString(count));
        }
        file = new File(sb.toString());
        return checkFile(file,count,copyFileName);
    }
}
