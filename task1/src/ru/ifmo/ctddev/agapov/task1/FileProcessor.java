package ru.ifmo.ctddev.agapov.task1;

import ru.ifmo.ctddev.agapov.task1.trie.MultiCharsetTrie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 19.02.14
 * Time: 4:39
 * To change this template use File | Settings | File Templates.
 */
public abstract class FileProcessor {

    static final int bufferSize = 1024;
    static final int lineCacheSize = 64 * bufferSize;
    long lineStartPosition = 0;
    int lineNumber = 1;
    int lineLength = 0;
    byte[] lineCache;
    byte[] buffer;
    RandomAccessFile raf;
    Path file;
    private MultiCharsetTrie trie;
    private MultiCharsetTrie.TrieNode node;
    private boolean switchTrieState = true;

    FileProcessor(Path file, MultiCharsetTrie trie) throws FileNotFoundException {
        this.trie = trie;
        this.file = file;
        lineCache = new byte[lineCacheSize];
        buffer = new byte[bufferSize];
        resetTrie();
    }

    protected void resetTrie() {
        node = trie.getRoot();
    }

    public boolean isSwitchTrieState() {
        return switchTrieState;
    }

    public void setSwitchTrieState(boolean switchTrieState) {
        this.switchTrieState = switchTrieState;
    }

    protected abstract void processLineEnd(long currentPosition) throws IOException;

    void printBytesFromFile(long byteCount) throws IOException {
        int byteRead = 0;
        while (byteRead >= 0 && byteCount > 0) {
            byteRead = raf.read(lineCache);
            System.out.write(lineCache, 0, (int) Math.min(byteRead, byteCount));
            byteCount -= byteRead;
        }
    }

    protected void printLine(long currentPosition) throws IOException {
        if (lineLength < 0) {
            long fPointer = raf.getFilePointer();
            raf.seek(lineStartPosition);
            printBytesFromFile(currentPosition - lineStartPosition);
            raf.seek(fPointer);
        } else {
            System.out.write(lineCache, 0, lineLength);
        }
    }

    void startNewLine(long currentPosition) throws IOException {
        lineStartPosition = currentPosition + 1;
        lineLength = 0;
        ++lineNumber;
        triggerNewLine();
    }

    protected abstract void triggerNewLine();

    void processByte(byte _byte) {
        if (switchTrieState)
            node = node.step(_byte);
        triggerProcessByte(node, _byte);
    }

    protected abstract void triggerProcessByte(MultiCharsetTrie.TrieNode node, byte _byte);

    void cacheByte(byte _byte) {
        if (lineLength == lineCacheSize) lineLength = -1;
        else if (lineLength >= 0) {
            lineCache[lineLength++] = _byte;
        }
    }

    long getExactCurrentPosition(int i, long byteRead) throws IOException {
        return raf.getFilePointer() - byteRead + i;
    }

    void proccesReadBytes(int byteRead) throws IOException {
        for (int i = 0; i < byteRead; ++i) {
            byte _byte = buffer[i];
            long currentPosition = getExactCurrentPosition(i, byteRead);
            if (_byte == '\n') {
                processLineEnd(currentPosition);
                startNewLine(currentPosition);
            } else
                cacheByte(_byte);
            processByte(_byte);
        }
    }

    void process() throws IOException {
        raf = new RandomAccessFile(file.toFile(), "r");
        try {
            int byteRead = 0;
            while (byteRead >= 0) {
                byteRead = raf.read(buffer);
                if (byteRead < 0) {
                    buffer[0] = '\n';
                    proccesReadBytes(1);
                } else
                    proccesReadBytes(byteRead);
            }
        } catch (IOException ex) {
            raf.close();
            throw ex;
        }
        raf.close();
    }

}
