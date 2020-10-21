package lesson3;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        //        final T value;
        final T value;
        Node<T> left = null;
        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }


    private Node<T> root = null;

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     * <p>
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * <p>
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     * <p>
     * Пример
     */
    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    /**
     * Удаление элемента из дерева
     * <p>
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     * <p>
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     * <p>
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        //Трудоемкость - O(log(n)) - не уверен, что правильно оценил
        //Ресурсоемкость - О(1)
        if (root == null || !contains(o)) return false;
        remove(root, null, (T) o);

        size--;
        return true;
    }


    private Node<T> findLeaf(boolean rightSubTree, Node<T> start) {
        //rightSubTree - для поиска в левом/правом поддереве
        if (rightSubTree) {
            while (start.left != null)
                start = start.left;

        } else {
            while (start.right != null)
                start = start.right;
        }
        return start;
    }

    private void remove(Node<T> current, Node<T> parent, T toR) {
        if (root.value == toR && parent == null) {
            Node<T> smallest;
            if (root.right != null)
                smallest = findLeaf(true, current.right);
            else
                smallest = findLeaf(false, current.left);

            Node<T> replace = new Node<T>(smallest.value);
            replace.left = current.left;
            replace.right = current.right;
            root = replace;
            if (root.right != null)
                remove(replace.right, root, smallest.value);
            else
                remove(replace.left, root, smallest.value);
            return;
        }
        int comparision = toR.compareTo(current.value);
        if (comparision > 0) {
            remove(current.right, current, toR);
        } else if (comparision < 0) { // выбор поддеревьев
            remove(current.left, current, toR);
        } else {
            if (current.left == null && current.right == null) { //лист
                if (parent.value.compareTo(toR) > 0)
                    parent.left = null;
                else
                    parent.right = null;
            } else if (current.left == null || current.right == null) { // 1 потомок
                if (parent.value.compareTo(toR) <= 0) {
                    parent.right = current.left != null ? current.left : current.right;
                } else if (parent.value.compareTo(toR) > 0)
                    parent.left = current.left != null ? current.left : current.right;
            } else { // 2 потомка
                Node<T> smallest = findLeaf(true, current.right);
                Node<T> replace = new Node<T>(smallest.value);
                replace.left = current.left; //присваиваем child элементу-замене
                replace.right = current.right;
                // установка нового child
                if (parent.value.compareTo(toR) > 0)
                    parent.left = replace;
                else
                    parent.right = replace;
                remove(replace.right, replace, smallest.value);
            }
        }
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
    }

    public class BinarySearchTreeIterator implements Iterator<T> {
        Node<T> current;
        private Stack<T> stackNodes = new Stack<>();
        private T returned;

        private BinarySearchTreeIterator() {
            if (root != null) {
                fillStack(root);
                stackNodes.remove(0); //дублируется первый элемент - нужно удалить
            } //создать стек
        }

        private void fillStack(Node<T> cur) {
            //Трудоемкость - O(n)
            //Ресурсоемкость - О(1)
            if (cur.right != null) {
                fillStack(cur.right);
            } else {
                stackNodes.push(cur.value);
            }

            if (cur.left != null)
                fillStack(cur.left);
            else {
                stackNodes.push(cur.value);
                current = cur;
            }
        }

        /**
         * Проверка наличия следующего элемента
         * <p>
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         * <p>
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         * <p>
         * Средняя
         */
        @Override
        public boolean hasNext() {
            //Трудоемкость - O(1)
            //Ресурсоемкость - О(1)
            return !stackNodes.isEmpty();
        }

        /**
         * Получение следующего элемента
         * <p>
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         * <p>
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         * <p>
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         * <p>
         * Средняя
         */
        @Override
        public T next() {
            //Трудоемкость - O(1)
            //Ресурсоемкость - О(1)
            if (hasNext()) {
                return returned = stackNodes.pop();
            } else throw new NoSuchElementException();
        }

        /**
         * Удаление предыдущего элемента
         * <p>
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         * <p>
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         * <p>
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         * <p>
         * Сложная
         */
        @Override
        public void remove() {
            //Трудоемкость - O(log(n))
            //Ресурсоемкость - О(1)
            if (size == stackNodes.size() || returned == null)
                throw new IllegalStateException();
            else {
                BinarySearchTree.this.remove(returned);
                returned = null;
            }
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

}