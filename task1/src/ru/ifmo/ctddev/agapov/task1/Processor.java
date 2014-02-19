package ru.ifmo.ctddev.agapov.task1;

import ru.ifmo.ctddev.agapov.task1.trie.AhoCorasicTrie;
import ru.ifmo.ctddev.agapov.task1.trie.MultiCharsetTrie;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 19.02.14
 * Time: 0:53
 * To change this template use File | Settings | File Templates.
 */
public class Processor {
    private final MultiCharsetTrie trie;
    private final Path currentDir;

    public Processor(String[] words) {
        trie = new MultiCharsetTrie();
        for (String word : words) trie.addWord(word);
        currentDir = Paths.get(System.getProperty("user.dir"));
    }

    public void process() {
        try {
            Files.walkFileTree(currentDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        new FastFileProcessor(file, trie).process();
                    } catch (IOException e) {
                        System.err.println("Error occured while processing file " + currentDir.relativize(file));
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error occured while traversing file tree");
            e.printStackTrace();
        }
    }

    private class FastFileProcessor extends FileProcessor{
        private FastFileProcessor(Path file, MultiCharsetTrie trie) throws FileNotFoundException {
            super(file, trie);
        }

        boolean matched = false;

        @Override
        protected void processLineEnd(int i) throws IOException {
            if (matched) {
                System.out.print(currentDir.relativize(file) + " (" + lineNumber + "): ");
                printLine(i);
                System.out.println();
            }
        }

        @Override
        protected void triggerNewLine() {
            matched = false;
        }

        @Override
        protected void triggerProcessByte(MultiCharsetTrie.TrieNode node, byte _byte) {
            if(!matched) matched = node.getLongestMatchingPair() != null;
        }
    }

    private class ExtendedFileProcessor extends FileProcessor{
        Set<MultiCharsetTrie.Pair> pairs;

        ExtendedFileProcessor(Path file, MultiCharsetTrie trie) throws FileNotFoundException {
            super(file, trie);
            pairs = new HashSet<MultiCharsetTrie.Pair>();
        }

        protected void processLineEnd(int i) throws IOException {
            if (!pairs.isEmpty()) {
                System.out.print(currentDir.relativize(file) + " (" + lineNumber + "): ");
                printLine(i);

                System.out.print("  //words: {");
                for (MultiCharsetTrie.Pair pair : pairs) {
                    System.out.print(pair.data.getWord() + ", ");
                }
                System.out.print("}");

                System.out.println();
            }
        }

        @Override
        protected void triggerNewLine() {
            pairs.clear();
        }

        @Override
        protected void triggerProcessByte(MultiCharsetTrie.TrieNode node, byte _byte) {
            Enumeration<MultiCharsetTrie.Pair> matchedPairs = node.getMatchingWords();
            while (matchedPairs.hasMoreElements()) {
                pairs.add(matchedPairs.nextElement());
            }
        }
    }


}
