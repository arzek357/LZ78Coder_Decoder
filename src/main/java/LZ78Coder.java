import java.io.IOException;
import java.util.HashMap;

public class LZ78Coder {
    private static String lastEncodePart;
    private static String lastDicDecodeNumberString;
    static FilePacket encode(FilePacket file,Boolean safeModeStatus) {
        HashMap<String,Integer> encodeDic = new HashMap<>();
        StringBuffer sbInput = file.getFullTextInBr();
        StringBuffer sbOutput = new StringBuffer();
        if (safeModeStatus){
            sbOutput.append("~LZ78~");
        } else {
            sbOutput.append("~LZ78");
        }
        for (int i =0;i<sbInput.length();i++){
            lastEncodePart = "";
            String partOfText = getEncodePartOfText(sbInput,i,1,encodeDic);
            if(partOfText.length()>1){
                i+= lastEncodePart.length()-1;
            }
            sbOutput.append(partOfText);
            if (partOfText.startsWith("~")&&safeModeStatus){
               sbOutput.append("~");
            }
        }
        return new FilePacket(file.getFile(),sbOutput);
    }
    static FilePacket decode(FilePacket file,Boolean safeModStatus) {
        HashMap<Integer,String> decodeDic = new HashMap<>();
        StringBuffer sbInput = file.getFullTextInBr();
        StringBuffer sbOutput = new StringBuffer();
        int startPos;
        if (safeModStatus){
            startPos=6;
        } else {
            startPos=5;
        }
        for (int i=startPos;i<sbInput.length();i++){
            lastDicDecodeNumberString ="";
            String partOfText = getDecodePartOfText(sbInput,i,decodeDic,safeModStatus);
            if (partOfText.length()>1){
                i+= lastDicDecodeNumberString.length()+1;
                if (safeModStatus){
                    i+=1;
                }
            }
            sbOutput.append(partOfText);
        }
        return new FilePacket(file.getFile(),sbOutput);
    }
    private static String getEncodePartOfText(StringBuffer sb, int i, int count, HashMap<String,Integer> dic){
        if (sb.substring(i,i+1).equals("\n")){
            return sb.substring(i,i+1);
        }
        if(!dic.containsKey(sb.substring(i,i+count))){
            dic.put(sb.substring(i,i+count),dic.size()+1);
            if(count>1){
                lastEncodePart =sb.substring(i,i+count);
                return "~"+dic.get(sb.substring(i,i+count-1))+sb.substring(i+count-1,i+count);
            }
            else {
                return sb.substring(i,i+count);
            }
        }
        if (sb.length()==(i+count)&&count>1){
            lastEncodePart =sb.substring(i,i+count);
            return "~"+dic.get(sb.substring(i,i+count));
        }
        count++;
            return getEncodePartOfText(sb,i,count,dic);
    }
    private static String getDecodePartOfText(StringBuffer sb,int i,HashMap<Integer,String> dic,Boolean safeModStatus){
        if (sb.substring(i,i+1).equals("\n")){
            return sb.substring(i,i+1);
        }
        if (sb.substring(i,i+1).equals("~")){
            int lastDicDecodeNumberInt = findFullNumber(sb,i+1,1,dic,safeModStatus);
            lastDicDecodeNumberString=Integer.toString(lastDicDecodeNumberInt);
            dic.put(dic.size()+1,dic.get(lastDicDecodeNumberInt)+sb.substring(i+lastDicDecodeNumberString.length()+1,i+lastDicDecodeNumberString.length()+2));
            return dic.get(dic.size());
        }
        if (sb.substring(i,i+1).matches("\\d")){
            if (sb.substring(i,sb.length()).matches("\\d*")){
                return dic.get(Integer.parseInt(sb.substring(i,sb.length())));
            }
        }
        dic.put(dic.size()+1,sb.substring(i,i+1));
        return sb.substring(i,i+1);
    }
    private static int findFullNumber(StringBuffer sb,int i,int count,HashMap<Integer,String> dic,Boolean safeModStatus){
        if (!sb.substring(i,i+count).matches("\\d*")) {
            if (safeModStatus){
            if (sb.substring(i + count - 1, i + count).equals("~")) {
                return Integer.parseInt(sb.substring(i, i + count - 2));
            } else {
                return Integer.parseInt(sb.substring(i, i + count - 1));
                }
            }
              else{
            if (dic.containsKey(Integer.parseInt(sb.substring(i, i + count - 1)))) {
                return Integer.parseInt(sb.substring(i, i + count - 1));
            } else {
                return Integer.parseInt(sb.substring(i, i + count - 2));
                }
            }
        }
        count++;
        return findFullNumber(sb,i,count,dic,safeModStatus);
    }
}
