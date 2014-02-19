package ru.ifmo.ctddev.agapov.task1.trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 05.10.13
 * Time: 5:38
 * To change this template use File | Settings | File Templates.
 */
public class AhoCorasicTrie<T> {
    private final TrieNode root;
    private final ArrayList<TrieNode> trie = new ArrayList<TrieNode>();

    AhoCorasicTrie() {
        root = new TrieNode();
        trie.add(root);
        root.link = root;
    }

    public TrieNode getRoot() {
        return root;
    }

    public void addWord(byte[] word, T terminalVertexData) {
        TrieNode node = root;
        for (int i = 0; i < word.length; ++i)
            node = node.getOrCreateNode(word[i]);
        node.word = word;
        node.data = terminalVertexData;
    }

    public class Pair {
        public final byte[] word;
        public final T data;

        public Pair(byte[] word, T data) {
            this.word = word;
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            Pair pair;
            try {
                pair = (Pair) o;
            } catch (ClassCastException ex) {
                return false;
            }

            if (!Arrays.equals(word, pair.word)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return 0x6AB8F29 ^ Arrays.hashCode(word);
        }
    }

    public class TrieNode {
        protected final TrieNode parent;
        protected final byte parentByte;
        protected T data;
        protected byte[] word = null;
        protected TrieNode closestTerminalLinkage = null;
        protected HashMap<Byte, TrieNode> goCache = null;
        protected HashMap<Byte, TrieNode> childs = null;
        protected TrieNode link = null;

        //For root
        public TrieNode() {
            parent = null;
            parentByte = '\0'; //just for order, won't be used
        }

        public TrieNode(TrieNode parent, byte parentByte) {
            this.parent = parent;
            this.parentByte = parentByte;
        }

        public boolean hasChild(byte ch) {
            return childs != null && childs.containsKey(ch);
        }

        public TrieNode getOrCreateNode(byte ch) {
            if (hasChild(ch)) return childs.get(ch);
            if (childs == null) childs = new HashMap<Byte, TrieNode>();
            TrieNode node = new TrieNode(this, ch);
            trie.add(node);
            childs.put(ch, node);
            return node;
        }

        public TrieNode getLink() {
            if (link != null) return link;
            if (parent == root || this == root) return (link = root);
            return (link = parent.getLink().step(parentByte));
        }

        public TrieNode step(byte ch) {
            if (goCache != null && goCache.containsKey(ch)) return goCache.get(ch);
            if (goCache == null) goCache = new HashMap<Byte, TrieNode>();
            TrieNode result;
            if (hasChild(ch)) {
                result = childs.get(ch);
            } else {
                result = this == root ? root : getLink().step(ch);
            }
            goCache.put(ch, result);
            return result;
        }

        protected TrieNode getClosestTerminalLinkage() {
            if (closestTerminalLinkage != null) return closestTerminalLinkage;
            closestTerminalLinkage = getLink();
            while (closestTerminalLinkage != root && !closestTerminalLinkage.isTerminal())
                closestTerminalLinkage = closestTerminalLinkage.getLink();
            return closestTerminalLinkage;
        }

        protected Pair getNextPair(TrieNode node) {
            if (node.word != null) return new Pair(node.word, node.data);
            TrieNode _node = node.getClosestTerminalLinkage();
            if (_node.word == null) return null;
            return new Pair(_node.word, _node.data);
        }

        /**
         * Enumeration of matching words, in length decreasing order
         *
         * @return
         */
        public Enumeration<Pair> getMatchingWords() {
            final TrieNode firstNode = word == null ? getClosestTerminalLinkage() : this;
            return new Enumeration<Pair>() {
                TrieNode node = firstNode;

                @Override
                public boolean hasMoreElements() {
                    return node != root && getNextPair(node) != null;
                }

                @Override
                public Pair nextElement() {
                    Pair result = getNextPair(node);
                    node = node.getClosestTerminalLinkage();
                    return result;
                }
            };
        }

        public Pair getLongestMatchingPair() {
            return getNextPair(this);
        }

        public boolean isTerminal() {
            return word != null;
        }
    }


}
