package ru.ifmo.ctddev.agapov.task1.trie;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 19.02.14
 * Time: 0:09
 * To change this template use File | Settings | File Templates.
 */
public class MultiCharsetTrie extends AhoCorasicTrie<MultiCharsetTrie.VertexData> {
    public MultiCharsetTrie() {
        super();
    }

    public class VertexData{
        String word;
        Charset charset;

        public String getWord() {
            return word;
        }

        public Charset getCharset() {
            return charset;
        }

        public VertexData(String word, Charset charset) {
            this.word = word;
            this.charset = charset;
        }
    }

    public void addWord(String word, Charset charset){
        byte [] bytes;
        ByteBuffer byteBuffer =   charset.encode(word);
        bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
//        System.out.print("  Adding word (charset "+charset+"): ");
//        System.out.println(Arrays.toString(bytes));
        addWord(bytes, new VertexData(word, charset));
    }

    public void addWord(String word){
//        System.out.print("Adding word: ");
//        byte [] bytes = word.getBytes();
//        System.out.println(Arrays.toString(bytes));
        addWord(word, Charset.forName("UTF-8"));
        addWord(word, Charset.forName("KOI8-R"));
        addWord(word, Charset.forName("CP1251"));
        addWord(word, Charset.forName("CP866"));
    }
}
