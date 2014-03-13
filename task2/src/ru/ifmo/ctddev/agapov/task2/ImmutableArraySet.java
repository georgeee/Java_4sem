package ru.ifmo.ctddev.agapov.task2;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 06.03.14
 * Time: 8:38
 * To change this template use File | Settings | File Templates.
 */
public class ImmutableArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final AscendingArraySubSet<E> ascSubSet = new AscendingArraySubSet<E>(this, null, false, null, false);
    private final DescendingArraySubSet<E> descSubSet = new DescendingArraySubSet<E>(this, null, false, null, false);
    private Object[] elements;
    private Comparator<? super E> comparator;
    private Comparator<Object> objectComparator;

    public ImmutableArraySet(Collection<? extends E> c, Comparator<? super E> comparator) {
        elements = new Object[0];
        this.comparator = comparator;
        Object[] objs = c.toArray();
        Arrays.sort(objs, getObjectComparator());
        ArrayList list = new ArrayList();
        for (int i = 0; i < objs.length; ++i) {
            if (i == 0 || compareElements(list.get(list.size() - 1), objs[i]) != 0)
                list.add(objs[i]);
        }
        elements = list.toArray();
    }

    public ImmutableArraySet(Comparator<? super E> comparator) {
        this(Collections.<E>emptyList(), comparator);
    }

    public ImmutableArraySet(Collection<? extends E> c) {
        this(c, null);
    }

    public ImmutableArraySet() {
        this(Collections.EMPTY_LIST);
    }

    public ImmutableArraySet(SortedSet<E> s) {
        this(s, s.comparator());
    }


    private E getElement(int i) {
        return (E) elements[i];
    }

    protected int binSearch(int fromIndex, int toIndex, Object key) {
        return Arrays.binarySearch(elements, fromIndex, toIndex, key, getObjectComparator());
    }

    private Comparator<Object> getObjectComparator() {
        if (comparator == null) return null;
        if (objectComparator == null) objectComparator = new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return comparator.compare((E) o1, (E) o2);
            }
        };
        return objectComparator;
    }

    protected int binSearch(Object key) {
        return binSearch(0, elements.length, key);
    }

    public int compareElements(Object e1, Object e2) {
        if (e1 == null) return e2 == null ? 0 : -1;
        if (comparator == null) return ((Comparable<E>) e1).compareTo((E) e2);
        else return comparator.compare((E) e1, (E) e2);
    }

    @Override
    public boolean contains(Object o) {
        return binSearch(o) >= 0;
    }

    @Override
    public E lower(E e) {
        return ascSubSet.lower(e);
    }

    @Override
    public E floor(E e) {
        return ascSubSet.floor(e);
    }

    private int ceilingIndex(E e) {
        int bs = binSearch(e);
        if (bs >= 0) return bs;
        bs = -bs - 1;
        if (bs < size()) return bs;
        return -1;
    }

    @Override
    public E ceiling(E e) {
        int bs = ceilingIndex(e);
        if (bs == -1)
            return null;
        return (E) getElement(bs);
    }

    @Override
    public E higher(E e) {
        return ascSubSet.higher(e);
    }

    @Override
    public E pollFirst() {
        return ascSubSet.pollFirst();
    }

    @Override
    public E pollLast() {
        return ascSubSet.pollLast();
    }

    @Override
    public Iterator<E> iterator() {
        return ascSubSet.iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return descSubSet;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return ascSubSet.descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return ascSubSet.subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return ascSubSet.headSet(toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return ascSubSet.tailSet(fromElement, inclusive);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, E toElement) {
        return ascSubSet.subSet(fromElement, toElement);
    }

    @Override
    public NavigableSet<E> headSet(E toElement) {
        return ascSubSet.headSet(toElement);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement) {
        return ascSubSet.tailSet(fromElement);
    }

    @Override
    public E first() {
        if (isEmpty()) throw new NoSuchElementException();
        return (E) getElement(0);
    }

    @Override
    public E last() {
        if (isEmpty()) throw new NoSuchElementException();
        return (E) getElement(size() - 1);
    }

    @Override
    public int size() {
        return elements.length;
    }

    private AscendingArraySubSet<E> getArraySubSet(boolean descendingOrder, ImmutableArraySet<E> s, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (descendingOrder) return new DescendingArraySubSet<E>(s, fromElement, fromInclusive, toElement, toInclusive);
        return new AscendingArraySubSet<E>(s, fromElement, fromInclusive, toElement, toInclusive);
    }

    private static class DescendingArraySubSet<E> extends AscendingArraySubSet<E> {
        private final Comparator<? super E> reverseComparator;

        DescendingArraySubSet(ImmutableArraySet<E> s, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            super(s, fromElement, fromInclusive, toElement, toInclusive);
            reverseComparator = Collections.reverseOrder(s.comparator());
        }

        @Override
        public Comparator<? super E> comparator() {
            return reverseComparator;
        }

        @Override
        public E ceiling(E e) {
            return super.floor(e);
        }

        @Override
        public E floor(E e) {
            return super.ceiling(e);
        }

        @Override
        public E lower(E e) {
            return super.higher(e);
        }

        @Override
        public E higher(E e) {
            return super.lower(e);
        }

        @Override
        public E lastImpl() {
            return super.firstImpl();
        }

        @Override
        public E firstImpl() {
            return super.lastImpl();
        }

        @Override
        public boolean isDescending() {
            return true;
        }

        @Override
        public Iterator<E> descendingIterator() {
            return super.iterator();
        }

        @Override
        public Iterator<E> iterator() {
            return super.descendingIterator();
        }
    }

    private static class AscendingArraySubSet<E> extends AbstractSet<E> implements NavigableSet<E> {
        boolean fromInclusive, toInclusive;
        private ImmutableArraySet<E> s;
        private E fromElement, toElement;


        AscendingArraySubSet(ImmutableArraySet<E> s, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            this.s = s;
            this.fromElement = fromElement;
            this.toElement = toElement;
            this.fromInclusive = fromInclusive;
            this.toInclusive = toInclusive;
        }

        public boolean isDescending() {
            return false;
        }

        public Comparator<? super E> comparator() {
            return s.comparator();
        }

        boolean checkInRange(E e) {
            if (e == null) return false;
            boolean result = true;
            if (fromElement != null) {
                int cmpr = s.compareElements(fromElement, e);
                result = fromInclusive ? cmpr <= 0 : cmpr < 0;
            }
            if (result && toElement != null) {
                int cmpr = s.compareElements(e, toElement);
                result = toInclusive ? cmpr <= 0 : cmpr < 0;
            }
            return result;
        }


        int ceilingIndex(E e) {
            int cI = s.ceilingIndex(e);
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (start <= cI && cI < end) return cI;
            if (cI >= 0 && cI < start) return start;
            return -1;
        }

        @Override
        public E ceiling(E e) {
            int ceilingIndex = ceilingIndex(e);
            if (ceilingIndex < 0) return null;
            return (E) s.getElement(ceilingIndex);
        }


        @Override
        public E higher(E e) {
            int cI = ceilingIndex(e);
            if (cI < 0) return null;
            if (s.compareElements(s.getElement(cI), e) == 0)
                ++cI;
            if (cI < getIndexRangeEnd()) return (E) s.getElement(cI);
            return null;
        }

        @Override
        public E floor(E e) {
            int lI = lowerIndex(e);
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (end == start) return null;
            E first = s.getElement(start);
            if (lI < 0) return s.compareElements(e, first) == 0 ? first : null;
            if (start <= lI + 1 && lI + 1 < end && s.compareElements(e, s.getElement(lI + 1)) == 0)
                ++lI;
            return (E) s.getElement(lI);
        }

        private int lowerIndex(E e) {
            int cI = ceilingIndex(e);
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (cI < 0) cI = end - 1;
            else --cI;
            if (start <= cI && cI < end) return cI;
            return -1;
        }

        @Override
        public E lower(E e) {
            int lI = lowerIndex(e);
            if (lI < 0) return null;
            return (E) s.getElement(lI);
        }

        @Override
        public E pollFirst() {
            E el = firstImpl();
            remove(el);
            return el;
        }

        @Override
        public E pollLast() {
            E el = lastImpl();
            remove(el);
            return el;
        }

        protected int getIndexRangeStart() {
            if (fromElement == null) return 0;
            int bsf = s.binSearch(fromElement);
            if (bsf >= 0) return bsf + (fromInclusive ? 0 : 1);
            return -bsf - 1;
        }

        protected int getIndexRangeEnd() {
            if (toElement == null) return s.size();
            int bst = s.binSearch(toElement);
            if (bst >= 0) {
                if (toInclusive)
                    return bst + 1;
                if (!fromInclusive && s.compareElements(fromElement, toElement) == 0) {
                    return bst + 1;
                }
                return bst;
            }
            return -bst - 1;
        }

        @Override
        public int size() {
            return getIndexRangeEnd() - getIndexRangeStart();
        }

        @Override
        public boolean contains(Object o) {
            return checkInRange((E) o) && s.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                int index = getIndexRangeStart();

                @Override
                public boolean hasNext() {
                    return index < getIndexRangeEnd();
                }

                @Override
                public E next() {
                    return s.getElement(index++);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Modification not allowed");
                }
            };
        }


        @Override
        public NavigableSet<E> descendingSet() {
            return s.getArraySubSet(!isDescending(), s, fromElement, fromInclusive, toElement, toInclusive);
        }

        @Override
        public Iterator<E> descendingIterator() {
            return new Iterator<E>() {
                int index = getIndexRangeEnd();

                @Override
                public boolean hasNext() {
                    return index > getIndexRangeStart();
                }

                @Override
                public E next() {
                    return s.getElement(--index);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Modification not allowed");
                }
            };
        }

        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            if (fromElement == null) throw new NullPointerException("fromElement is null");
            if (toElement == null) throw new NullPointerException("toElement is null");
            if (!checkInRange(fromElement))
                throw new IllegalArgumentException("fromKey is not in valid range of current set");
            if (!checkInRange(toElement))
                throw new IllegalArgumentException("toKey is not in valid range of current set");
            if (s.compareElements(fromElement, toElement) > 0)
                throw new IllegalArgumentException("fromKey > toKey");
            return s.getArraySubSet(isDescending(), s, fromElement, fromInclusive, toElement, toInclusive);
        }

        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            if (toElement == null) throw new NullPointerException("toElement is null");
            if (!checkInRange(toElement)) throw new IllegalArgumentException("toElement is not in set's range");
            return s.getArraySubSet(isDescending(), s, null, false, toElement, inclusive);
        }

        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            if (fromElement == null) throw new NullPointerException("fromElement is null");
            if (!checkInRange(fromElement)) throw new IllegalArgumentException("fromElement is not in set's range");
            return s.getArraySubSet(isDescending(), s, fromElement, inclusive, null, false);
        }

        @Override
        public NavigableSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        @Override
        public NavigableSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        @Override
        public NavigableSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        @Override
        public E first() {
            E result = firstImpl();
            if (result == null) throw new NoSuchElementException();
            return result;
        }

        @Override
        public E last() {
            E result = firstImpl();
            if (result == null) throw new NoSuchElementException();
            return result;
        }

        protected E firstImpl() {
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (start != end) return s.getElement(start);
            return null;
        }


        protected E lastImpl() {
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (start != end) return s.getElement(end - 1);
            return null;
        }

    }

}
