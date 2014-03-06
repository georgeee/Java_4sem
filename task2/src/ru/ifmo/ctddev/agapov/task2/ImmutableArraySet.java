package ru.ifmo.ctddev.agapov.task2;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 06.03.14
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class ImmutableArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final AscendingArraySubSet<E> ascSubSet = new AscendingArraySubSet<E>(this, null, false, null, false);
    private final DescendingArraySubSet<E> descSubSet = new DescendingArraySubSet<E>(this, null, false, null, false);
    private Object[] elements;
    private Comparator<? super E> comparator;
    private Comparator<Object> objectComparator;

    public ImmutableArraySet(Collection<? extends E> c, Comparator<? super E> comparator) {
        elements = c.toArray();
        Arrays.sort(elements, 0, elements.length, getObjectComparator());
        ArrayList al = new ArrayList();
        this.comparator = comparator;
        for(Object o : elements)
            if(al.size() == 0 || compareElements(al.get(al.size()-1), o) != 0) al.add(o);
        elements = new Object[al.size()];
        elements = al.toArray(elements);
    }

    public ImmutableArraySet(Comparator<? super E> comparator) {
        this(Collections.EMPTY_LIST, comparator);
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

    @Override
    public E ceiling(E e) {
        return ascSubSet.ceiling(e);
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
        return new AscendingArraySubSet<E>(this, fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new AscendingArraySubSet<E>(this, null, false, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new AscendingArraySubSet<E>(this, fromElement, inclusive, null, false);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (isEmpty()) return null;
        return (E) getElement(0);
    }

    @Override
    public E last() {
        if (isEmpty()) return null;
        return (E) getElement(size() - 1);
    }

    @Override
    public int size() {
        return elements.length;
    }

    private void removeInRange(int start, int end) {
        Object[] newElements = new Object[elements.length - (end - start)];
        System.arraycopy(elements, 0, newElements, 0, start);
        System.arraycopy(elements, end, newElements, start, elements.length - end);
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
        public E last() {
            return super.first();
        }

        @Override
        public E first() {
            return super.last();
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
            if(e == null) return false;
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

        @Override
        public E floor(E e) {
            return new NonStrictElementFindImpl() {
                @Override
                E insideRange(E e) {
                    return s.floor(e);
                }

                @Override
                E lessThanRange(E e) {
                    return null;
                }

                @Override
                E greaterThanRange(E e) {
                    return last();
                }

            }.get(e);
        }

        @Override
        public E ceiling(E e) {
            return new NonStrictElementFindImpl() {
                @Override
                E insideRange(E e) {
                    return s.ceiling(e);
                }

                @Override
                E lessThanRange(E e) {
                    return first();
                }

                @Override
                E greaterThanRange(E e) {
                    return null;
                }

            }.get(e);
        }

        @Override
        public E lower(E e) {
            return new ElementFindImpl() {
                @Override
                E insideRange(E e) {
                    return s.lower(e);
                }

                @Override
                E lessThanRange(E e) {
                    return null;
                }

                @Override
                E greaterThanRange(E e) {
                    return last();
                }

                @Override
                E equalsToLeftEdge(E e) {
                    return null;
                }

                @Override
                E equalsToRightEdge(E e) {
                    return insideRange(e);
                }
            }.get(e);
        }

        @Override
        public E higher(E e) {
            return new ElementFindImpl() {
                @Override
                E insideRange(E e) {
                    return s.higher(e);
                }

                @Override
                E lessThanRange(E e) {
                    return first();
                }

                @Override
                E greaterThanRange(E e) {
                    return null;
                }

                @Override
                E equalsToLeftEdge(E e) {
                    return insideRange(e);
                }

                @Override
                E equalsToRightEdge(E e) {
                    return null;
                }
            }.get(e);
        }

        @Override
        public E pollFirst() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E pollLast() {
            throw new UnsupportedOperationException();
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
            if (bst >= 0) return bst + (toInclusive ? 1 : 0);
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
                    throw new UnsupportedOperationException();
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
                    s.removeInRange(index - 1, index);
                    --index;
                }
            };
        }

        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            if (!checkInRange(fromElement)) {
                fromElement = this.fromElement;
                fromInclusive = this.fromInclusive;
            }
            if (!checkInRange(toElement)) {
                toElement = this.toElement;
                toInclusive = this.toInclusive;
            }
            return s.getArraySubSet(isDescending(), s, fromElement, fromInclusive, toElement, toInclusive);
        }

        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return subSet(null, false, toElement, inclusive);
        }

        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return subSet(fromElement, inclusive, null, false);
        }

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        @Override
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }

        @Override
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }

        @Override
        public E first() {
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (start != end) return s.getElement(start);
            return null;
        }

        @Override
        public E last() {
            int start = getIndexRangeStart();
            int end = getIndexRangeEnd();
            if (start != end) return s.getElement(end - 1);
            return null;
        }

        private abstract class ElementFindImpl {
            E getImpl(E e) {
                if (fromElement != null) {
                    int fromCmpr = s.compareElements(e, fromElement);
                    if (fromCmpr < 0 || (fromCmpr == 0 && !fromInclusive)) return lessThanRange(e);
                    else if (fromCmpr == 0) return equalsToLeftEdge(e);
                }
                if (toElement != null) {
                    int toCmpr = s.compareElements(e, toElement);
                    if (toCmpr > 0 || (toCmpr == 0 && !toInclusive)) return greaterThanRange(e);
                    else if (toCmpr == 0) return equalsToRightEdge(e);
                }
                return insideRange(e);
            }

            E get(E e) {
                E result = getImpl(e);
                if (result != null && checkInRange(result)) return result;
                return null;
            }

            abstract E insideRange(E e);

            abstract E lessThanRange(E e);

            abstract E greaterThanRange(E e);

            abstract E equalsToLeftEdge(E e);

            abstract E equalsToRightEdge(E e);

        }

        private abstract class NonStrictElementFindImpl extends ElementFindImpl {
            @Override
            E equalsToLeftEdge(E e) {
                return fromElement;
            }

            @Override
            E equalsToRightEdge(E e) {
                return toElement;
            }
        }
    }


}
