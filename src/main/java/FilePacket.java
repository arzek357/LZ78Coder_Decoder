
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePacket {
    private Path fileName;
    private final long fileLength;
    private final File fullPath;
    private StringBuffer fullTextInBr = new StringBuffer();

    public FilePacket(File file){
        this.fullPath = file;
        fileLength=file.length();
        findFileName();
        findTextAndWriteInBr();
    }
    public FilePacket(File file,StringBuffer fullTextInBr) {
        this.fullPath = file;
        fileLength=file.length();
        findFileName();
        this.fullTextInBr = fullTextInBr;
    }
    public File getFile() {
        return fullPath;
    }

    public long getFileLength() {
        return fileLength;
    }

    public Path getFileName() {
        return fileName;
    }
    private void findFileName(){
        Path path = Paths.get(fullPath.toURI());
        fileName=path.getFileName();
    }

    public void setFullTextInBr(StringBuffer fullTextInBr) {
        this.fullTextInBr = fullTextInBr;
    }

    public StringBuffer getFullTextInBr() {
        return fullTextInBr;
    }
    void findTextAndWriteInBr() {
        try {
            FileInputStream fis = new FileInputStream(this.getFile());
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            while (line != null) {
                fullTextInBr.append(line).append("\n");
                line = br.readLine();
            }
            fis.close();
            br.close();
        } catch (FileNotFoundException ex){
            System.out.println("Ошибка в пути файла!");
            ex.printStackTrace();
        } catch (IOException iox) {
            System.out.println("Ошибка при чтении файла!");
            iox.printStackTrace();
        }
    }
}
