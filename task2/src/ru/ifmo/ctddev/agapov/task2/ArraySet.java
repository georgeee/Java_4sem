package ru.ifmo.ctddev.agapov.task2;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 06.03.14
 * Time: 8:38
 * To change this template use File | Settings | File Templates.
 */
public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final AscendingArraySubSet<E> ascSubSet = new AscendingArraySubSet<E>(this, null, false, null, false);
    private final DescendingArraySubSet<E> descSubSet = new DescendingArraySubSet<E>(this, null, false, null, false);
    private Object[] elements;
    private Comparator<? super E> comparator;
    private Comparator<Object> objectComparator;

    public ArraySet(Collection<? extends E> c, Comparator<? super E> comparator) {
        elements = new Object[0];
        this.comparator = comparator;
        addAll(c);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this(Collections.EMPTY_LIST, comparator);
    }

    public ArraySet(Collection<? extends E> c) {
        this(c, null);
    }

    public ArraySet() {
        this(Collections.EMPTY_LIST);
    }

    public ArraySet(SortedSet<E> s) {
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

    @Override
    public boolean add(E e) {
        if (e == null) return false;
        int bs = binSearch(e);
        if (bs >= 0) return false;
        bs = -bs - 1;
        Object[] newElements = new Object[size() + 1];
        System.arraycopy(elements, 0, newElements, 0, bs);
        newElements[bs] = e;
        System.arraycopy(elements, bs, newElements, bs + 1, elements.length - bs);
        elements = newElements;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        int bs = binSearch(o);
        if (bs < 0) return false;
        removeInRange(bs, bs + 1);
        return true;
    }

    public int compareElements(Object e1, Object e2) {
        if (e1 == null) return e2 == null ? 0 : -1;
        if (comparator == null) return ((Comparable<E>) e1).compareTo((E) e2);
        else return comparator.compare((E) e1, (E) e2);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return new CollectionAllProcessor() {
            @Override
            int initialCapacity(Object[] addCandidates) {
                return addCandidates.length + elements.length;
            }

            @Override
            boolean processLess() {
                addElementFromElements();
                return false;
            }

            @Override
            boolean processGreat() {
                return true;
            }

            @Override
            boolean processEqual(int i2) {
                addElementFromElements();
                return false;
            }

            @Override
            boolean processCandidatesTail() {
                return addAllFromCandidatesTail();
            }

            @Override
            boolean processElementsTail() {
                return addAllFromElementsTail();
            }
        }.process(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return new CollectionAllProcessor() {
            @Override
            int initialCapacity(Object[] addCandidates) {
                return addCandidates.length;
            }

            @Override
            boolean processLess() {
                i1++;
                return true;
            }

            @Override
            boolean processGreat() {
                i2++;
                return false;
            }

            @Override
            boolean processEqual(int i2) {
                addElementFromElements();
                return false;
            }

            @Override
            boolean processCandidatesTail() {
                return false;
            }

            @Override
            boolean processElementsTail() {
                return false;
            }
        }.process(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return new CollectionAllProcessor() {
            @Override
            int initialCapacity(Object[] addCandidates) {
                return elements.length;
            }

            @Override
            boolean processLess() {
                addElementFromElements();
                return false;
            }

            @Override
            boolean processGreat() {
                i2++;
                return false;
            }

            @Override
            boolean processEqual(int i2) {
                i1++;
                return true;
            }

            @Override
            boolean processCandidatesTail() {
                return false;
            }

            @Override
            boolean processElementsTail() {
                return addAllFromElementsTail();
            }
        }.process(c);
    }

    @Override
    public void clear() {
        elements = new Object[0];
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
        if (isEmpty())
            return null;
        Object obj = getElement(0);
        removeInRange(0, 1);
        return (E) obj;
    }

    @Override
    public E pollLast() {
        if (isEmpty())
            return null;
        Object obj = getElement(size() - 1);
        removeInRange(size() - 1, size());
        return (E) obj;
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

    public Object[] getElements() {
        return elements;
    }

    private void removeInRange(int start, int end) {
        Object[] newElements = new Object[elements.length - (end - start)];
        System.arraycopy(elements, 0, newElements, 0, start);
        System.arraycopy(elements, end, newElements, start, elements.length - end);
        elements = newElements;
    }

    private AscendingArraySubSet<E> getArraySubSet(boolean descendingOrder, ArraySet<E> s, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        if (descendingOrder) return new DescendingArraySubSet<E>(s, fromElement, fromInclusive, toElement, toInclusive);
        return new AscendingArraySubSet<E>(s, fromElement, fromInclusive, toElement, toInclusive);
    }

    private static class DescendingArraySubSet<E> extends AscendingArraySubSet<E> {
        private final Comparator<? super E> reverseComparator;

        DescendingArraySubSet(ArraySet<E> s, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
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
        private ArraySet<E> s;
        private E fromElement, toElement;

        AscendingArraySubSet(ArraySet<E> s, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
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
            if(start <= cI && cI < end) return cI;
            if(cI >= 0 && cI < start) return start;
            return -1;
        }

        @Override
        public E ceiling(E e) {
            int ceilingIndex = ceilingIndex(e);
            if(ceilingIndex < 0) return null;
            return (E) s.getElement(ceilingIndex);
        }


        @Override
        public E higher(E e) {
            int cI = ceilingIndex(e);
            if(cI < 0) return null;
            if(s.compareElements(s.getElement(cI), e) == 0)
                ++cI;
            if(cI < getIndexRangeEnd()) return (E) s.getElement(cI);
            return null;
        }

        @Override
        public E floor(E e) {
            int lI = lowerIndex(e);
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if(end == start) return null;
            E first = s.getElement(start);
            if(lI < 0) return s.compareElements(e, first) == 0 ? first : null;
            if(start <= lI + 1 && lI + 1 < end && s.compareElements(e, s.getElement(lI + 1)) == 0)
                ++lI;
            return (E) s.getElement(lI);
        }

        private int lowerIndex(E e){
            int cI = ceilingIndex(e);
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if(cI < 0) cI = end - 1;
            else --cI;
            if(start <= cI && cI < end) return cI;
            return -1;
        }

        @Override
        public E lower(E e) {
            int lI = lowerIndex(e);
            if(lI < 0) return null;
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
                    s.removeInRange(index, index + 1);
                }
            };
        }

        @Override
        public boolean add(E e) {
            if (!checkInRange(e)) throw new IllegalArgumentException(e + " not in range");
            return s.add(e);
        }

        @Override
        public boolean remove(Object o) {
            if (o == null || !checkInRange((E) o)) return false;
            return s.remove(o);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            for (E e : c) if (!checkInRange(e)) throw new IllegalArgumentException(e + " not in range");
            return s.addAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            ArrayList list = new ArrayList();
            for (Object e : c) if (e != null && checkInRange((E) e)) list.add(e);
            return s.retainAll(list);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            ArrayList list = new ArrayList();
            for (Object e : c) if (e != null && checkInRange((E) e)) list.add(e);
            return s.removeAll(list);
        }

        @Override
        public void clear() {
            s.removeInRange(getIndexRangeStart(), getIndexRangeEnd());
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
                    s.removeInRange(index - 1, index);
                    --index;
                }
            };
        }

        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            if(fromElement == null) throw new NullPointerException("fromElement is null");
            if(toElement == null) throw new NullPointerException("toElement is null");
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
            if(toElement == null) throw new NullPointerException("toElement is null");
            if(!checkInRange(toElement)) throw new IllegalArgumentException("toElement is not in set's range");
            return s.getArraySubSet(isDescending(), s, null, false, toElement, inclusive);
        }

        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            if(fromElement == null) throw new NullPointerException("fromElement is null");
            if(!checkInRange(fromElement)) throw new IllegalArgumentException("fromElement is not in set's range");
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

    private abstract class CollectionAllProcessor {

        Object[] addCandidates;
        Object[] newElements;
        int newElementsLength = 0;
        int i1 = 0, i2 = 0;

        abstract int initialCapacity(Object[] addCandidates);

        //For addAll
        boolean addAllFromElementsTail() {
            if (elements.length == i1) return false;
            System.arraycopy(elements, i1, newElements, newElementsLength, elements.length - i1);
            newElementsLength += elements.length - i1;
            i1 = elements.length;
            return true;
        }

        boolean addAllFromCandidatesTail() {
            boolean changed = false;
            for (; i2 < addCandidates.length; ++i2) {
                if (newElementsLength == 0
                        || compareElements(newElements[newElementsLength - 1], addCandidates[i2]) != 0) {
                    newElements[newElementsLength++] = addCandidates[i2];
                    changed = true;
                }
            }
            return changed;
        }

        void addElementFromElements() {
            newElements[newElementsLength++] = elements[i1++];
        }

        void addElementFromCandidates(int i2) {
            newElements[newElementsLength++] = addCandidates[i2++];
        }

        void addElementFromCandidates() {
            addElementFromCandidates(i2);
        }

        abstract boolean processLess();

        abstract boolean processGreat();

        abstract boolean processEqual(int i2);

        abstract boolean processCandidatesTail();

        abstract boolean processElementsTail();

        boolean process(Collection<?> c) {
            if (c.isEmpty()) return false;
            addCandidates = new Object[c.size()];
            addCandidates = c.toArray(addCandidates);
            if (!(c instanceof SortedSet)) {
                Arrays.sort(addCandidates, 0, addCandidates.length, getObjectComparator());
            }
            boolean changed = false;
            newElements = new Object[initialCapacity(addCandidates)];
            while (i2 < addCandidates.length && addCandidates[i2] == null) ++i2;
            while (i1 < elements.length || i2 < addCandidates.length) {
                if (i1 == elements.length) {
                    changed |= processCandidatesTail();
                } else if (i2 == addCandidates.length) {
                    changed |= processElementsTail();
                } else {
                    int cmpr = compareElements(elements[i1], addCandidates[i2]);
                    if (cmpr < 0) {
                        changed |= processLess();
                    } else if (cmpr > 0) {
                        changed |= processGreat();
                    } else {
                        int _i2 = i2;
                        ++i2;
                        for (; i2 < addCandidates.length && compareElements(elements[i1], addCandidates[i2]) == 0; ++i2)
                            ;
                        changed |= processEqual(_i2);
                    }

                }
            }
            if (changed)
                elements = Arrays.copyOf(newElements, newElementsLength);
            return changed;
        }
    }

}
