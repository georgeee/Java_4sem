package ru.ifmo.ctddev.agapov.task1;

import ru.ifmo.ctddev.agapov.task1.trie.AhoCorasicTrie;
import ru.ifmo.ctddev.agapov.task1.trie.MultiCharsetTrie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

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
    private MultiCharsetTrie.TrieNode node;

    FileProcessor(Path file, MultiCharsetTrie trie) throws FileNotFoundException {
        this.file = file;
        lineCache = new byte[lineCacheSize];
        buffer = new byte[bufferSize];
        node = trie.getRoot();
    }


    protected abstract void processLineEnd(int i) throws IOException;

    void printBytesFromFile(long byteCount) throws IOException {
        int byteRead = 0;
        while (byteRead >= 0 && byteCount > 0) {
            byteRead = raf.read(lineCache);
            System.out.write(lineCache, 0, (int) Math.min(byteRead, byteCount));
            byteCount -= byteRead;
        }
    }


    protected void printLine(int i) throws IOException {
        if (lineLength < 0) {
            long fPointer = raf.getFilePointer();
            long currentPosition = getExactCurrentPosition(i);
            raf.seek(lineStartPosition);
            printBytesFromFile(currentPosition - fPointer);
            raf.seek(fPointer);
        } else {
            System.out.write(lineCache, 0, lineLength);
        }
    }

    void startNewLine(int i) throws IOException {
        lineStartPosition = getExactCurrentPosition(i) + 1;
        lineLength = 0;
        ++lineNumber;
        triggerNewLine();
    }

    protected abstract void triggerNewLine();

    void processByte(byte _byte) {
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

    long getExactCurrentPosition(int i) throws IOException {
        return raf.getFilePointer() - bufferSize + i;
    }

    void proccesReadBytes(int byteRead) throws IOException {
        for (int i = 0; i < byteRead; ++i) {
            byte _byte = buffer[i];
            if ((char) _byte == '\n') {
                processLineEnd(i);
                startNewLine(i);
            } else
                cacheByte(_byte);
            processByte(_byte);
        }
    }

    void process() throws IOException {
        raf = new RandomAccessFile(file.toFile(), "r");
        int byteRead = 0;
        while (byteRead >= 0) {
            byteRead = raf.read(buffer);
            if (byteRead < 0) {
                buffer[0] = '\n';
                proccesReadBytes(1);
            } else
                proccesReadBytes(byteRead);
        }
        raf.close();
    }

}
