package tableMapping;

public class FileSignature {

    private long id;
    private String FileName;

    public FileSignature(long id, String fileName) {
        this.id = id;
        FileName = fileName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

}
