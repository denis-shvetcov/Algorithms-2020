package lesson4;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Префиксное дерево для строк
 */
public class Trie extends AbstractSet<String> implements Set<String> {

    private static class Node {
        Map<Character, Node> children = new LinkedHashMap<>();
    }

    private Node root = new Node();

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.children.clear();
        size = 0;
    }

    private String withZero(String initial) {
        return initial + (char) 0;
    }

    @Nullable
    private Node findNode(String element) {
        Node current = root;
        for (char character : element.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(character);
        }
        return current;
    }

    @Override
    public boolean contains(Object o) {
        String element = (String) o;
        return findNode(withZero(element)) != null;
    }

    @Override
    public boolean add(String element) {
        Node current = root;
        boolean modified = false;
        for (char character : withZero(element).toCharArray()) {
            Node child = current.children.get(character);
            if (child != null) {
                current = child;
            } else {
                modified = true;
                Node newChild = new Node();
                current.children.put(character, newChild);
                current = newChild;
            }
        }
        if (modified) {
            size++;
        }
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        String element = (String) o;
        Node current = findNode(element);
        if (current == null) return false;
        if (current.children.remove((char) 0) != null) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * Итератор для префиксного дерева
     * <p>
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     * <p>
     * Сложная
     *
     * @return
     */
    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new TrieIterator<String>();
    }

    public class TrieIterator<T extends String> implements Iterator<T> {
        Deque<String> nodes = new ArrayDeque<>();
        String returned = null;


        public TrieIterator() {
            if (root != null && root.children != null && !root.children.isEmpty()) fillStack(root, "");
        }

        private void fillStack(Node current, String sequence) {
            //Трудоемкость - O(n), n-количество элементов в дереве
            //Ресурсоемкость - О(1)
            if (current.children != null && !current.children.isEmpty()) {
                for (Map.Entry<Character, Node> keyVal : current.children.entrySet()) {
                    char key = keyVal.getKey();

                        fillStack(keyVal.getValue(), sequence + key);
                }
            }
            else {
                if (sequence.endsWith("\u0000")) nodes.addLast(sequence.substring(0, sequence.length() - 1));
            }
        }


        @Override
        public boolean hasNext() {
            //Трудоемкость - O(1)
            //Ресурсоемкость - О(1)
            return !nodes.isEmpty();
        }

        @Override
        public T next() {
            //Трудоемкость - O(1)
            //Ресурсоемкость - О(1)
            if (hasNext()) {
                returned = nodes.pollLast();
                return (T) returned;
            }
            else throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            //Трудоемкость - O(n) , n - длина returned
            //Ресурсоемкость - О(1)
            if (returned == null)
                throw new IllegalStateException();
            else {
                Trie.this.remove(returned);
                returned = null;
            }
        }
    }

}