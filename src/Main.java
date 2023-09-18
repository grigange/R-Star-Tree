import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        String fileName = "src/data/blockfile.dat";
        byte[] data = { 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67}; // binary hex <-> ascii
        BlockFileManager.writeBlockToFile(fileName, data);

        byte[] loadedData = BlockFileManager.readBlockFromFile(fileName);
        System.out.print(new String(loadedData, StandardCharsets.UTF_8));
    }
}