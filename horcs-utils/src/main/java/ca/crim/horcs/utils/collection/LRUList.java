package ca.crim.horcs.utils.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LRUList<V> implements List<V> {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private int cacheSize;

    private LinkedList<V> linkedList;

    public LRUList(int cacheSize) {
        this.cacheSize = cacheSize;
        this.linkedList = new LinkedList<V>();
    }

    public synchronized int size() {
        return linkedList.size();
    }

    public synchronized boolean isEmpty() {
        return linkedList.isEmpty();
    }

    public synchronized Iterator<V> iterator() {
        return linkedList.iterator();
    }

    public synchronized boolean add(V o) {
        linkedList.addFirst(o);
        if (linkedList.size() > cacheSize) {
            linkedList.removeLast();
        }
        return true;
    }

    public synchronized void clear() {
        linkedList.clear();
    }

    public synchronized void moveFirst(int index) {
        if (index != 0) {
            V o = linkedList.remove(index);
            linkedList.addFirst(o);
        }
    }

    public synchronized V get(int index) {
        return linkedList.get(index);
    }

    public boolean contains(Object o) {
        return linkedList.contains(o);
    }

    public Object[] toArray() {
        return linkedList.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return linkedList.toArray(a);
    }

    public boolean remove(Object o) {
        return linkedList.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return linkedList.containsAll(c);
    }

    public boolean addAll(Collection<? extends V> c) {
        return linkedList.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends V> c) {
        return linkedList.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return linkedList.remove(c);
    }

    public boolean retainAll(Collection<?> c) {
        return linkedList.retainAll(c);
    }

    public V set(int index, V element) {
        return linkedList.set(index, element);
    }

    public void add(int index, V element) {
        linkedList.add(index, element);
    }

    public V remove(int index) {
        return linkedList.remove(index);
    }

    public int indexOf(Object o) {
        return linkedList.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return linkedList.lastIndexOf(o);
    }

    public ListIterator<V> listIterator() {
        return linkedList.listIterator();
    }

    public ListIterator<V> listIterator(int index) {
        return linkedList.listIterator(index);
    }

    public List<V> subList(int fromIndex, int toIndex) {
        return linkedList.subList(fromIndex, toIndex);
    }

}
