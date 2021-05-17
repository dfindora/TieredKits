package com.shortcircuit.utils.collect;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ConcurrentArrayList<T> extends ArrayList<T>
{
    public ConcurrentArrayList()
    {
    }

    public ConcurrentArrayList(int initial_capacity)
    {
        super(initial_capacity);
    }

    public ConcurrentArrayList(Collection<? extends T> c)
    {
        super(c);
    }

    public ConcurrentArrayList(T[] c)
    {
        super(c.length);
        this.addAll(Arrays.asList(c).subList(0, c.length));

    }

    public synchronized int size()
    {
        synchronized (this)
        {
            return super.size();
        }
    }

    public synchronized boolean isEmpty()
    {
        synchronized (this)
        {
            return super.isEmpty();
        }
    }

    public synchronized boolean contains(Object o)
    {
        synchronized (this)
        {
            return super.contains(o);
        }
    }

    @Nonnull
    public synchronized ConcurrentIterator<T> iterator()
    {
        synchronized (this)
        {
            return new ConcurrentIterator<>(this);
        }
    }

    @Nonnull
    public synchronized Object[] toArray()
    {
        synchronized (this)
        {
            return super.toArray();
        }
    }

    @Nonnull
    public synchronized <T1> T1[] toArray(T1[] a)
    {
        synchronized (this)
        {
            return super.toArray(a);
        }
    }

    public synchronized boolean add(T t)
    {
        synchronized (this)
        {
            return super.add(t);
        }
    }

    public synchronized boolean remove(Object o)
    {
        synchronized (this)
        {
            return super.remove(o);
        }
    }

    public synchronized boolean containsAll(@Nonnull Collection<?> c)
    {
        synchronized (this)
        {
            return super.containsAll(c);
        }
    }

    public synchronized boolean addAll(Collection<? extends T> c)
    {
        synchronized (this)
        {
            return super.addAll(c);
        }
    }

    public synchronized boolean addAll(int index, Collection<? extends T> c)
    {
        synchronized (this)
        {
            return super.addAll(index, c);
        }
    }

    public synchronized boolean removeAll(Collection<?> c)
    {
        synchronized (this)
        {
            return super.removeAll(c);
        }
    }

    public synchronized boolean retainAll(Collection<?> c)
    {
        synchronized (this)
        {
            return super.retainAll(c);
        }
    }

    public synchronized void clear()
    {
        synchronized (this)
        {
            super.clear();
        }
    }

    public synchronized T get(int index)
    {
        synchronized (this)
        {
            return super.get(index);
        }
    }

    public synchronized T set(int index, T element)
    {
        synchronized (this)
        {
            return super.set(index, element);
        }
    }

    public synchronized void add(int index, T element)
    {
        synchronized (this)
        {
            super.add(index, element);
        }
    }

    public synchronized T remove(int index)
    {
        synchronized (this)
        {
            return super.remove(index);
        }
    }

    public synchronized int indexOf(Object o)
    {
        synchronized (this)
        {
            return super.indexOf(o);
        }
    }

    public synchronized int lastIndexOf(Object o)
    {
        synchronized (this)
        {
            return super.lastIndexOf(o);
        }
    }

    @Nonnull
    public synchronized ConcurrentListIterator<T> listIterator()
    {
        synchronized (this)
        {
            return new ConcurrentListIterator<>(this);
        }
    }

    @Nonnull
    public synchronized ConcurrentListIterator<T> listIterator(int index)
    {
        synchronized (this)
        {
            return new ConcurrentListIterator<>(this);
        }
    }

    @Nonnull
    public synchronized ConcurrentArrayList<T> subList(int from_index, int to_index)
    {
        synchronized (this)
        {
            return new ConcurrentArrayList<>(super.subList(from_index, to_index));
        }
    }

    public synchronized void ensureCapacity(int min_capacity)
    {
        synchronized (this)
        {
            super.ensureCapacity(min_capacity);
        }
    }

    public synchronized void trimToSize()
    {
        synchronized (this)
        {
            super.trimToSize();
        }
    }

    public synchronized Object clone()
    {
        synchronized (this)
        {
            return super.clone();
        }
    }
}
