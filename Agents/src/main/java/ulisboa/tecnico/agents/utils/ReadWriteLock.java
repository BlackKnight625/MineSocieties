package ulisboa.tecnico.agents.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {
    private final java.util.concurrent.locks.ReadWriteLock lock = new ReentrantReadWriteLock();

    // Getters and setters

    public Lock getReadLock() {
        return lock.readLock();
    }

    public Lock getWriteLock() {
        return lock.writeLock();
    }

    // Other methods

    public void readLock() {
        lock.readLock().lock();
    }

    public boolean tryReadLock() {
        return lock.readLock().tryLock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public boolean tryWriteLock() {
        return lock.writeLock().tryLock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void write(Runnable runnable) {
        writeLock();

        try {
            runnable.run();
        } finally {
            writeUnlock();
        }
    }

    public boolean tryWrite(Runnable runnable) {
        if (tryWriteLock()) {
            try {
                runnable.run();
            } finally {
                writeUnlock();
            }

            return true;
        }

        return false;
    }

    public void read(Runnable runnable) {
        readLock();

        try {
            runnable.run();
        } finally {
            readUnlock();
        }
    }

    public boolean tryRead(Runnable runnable) {
        if (tryReadLock()) {
            try {
                runnable.run();
            } finally {
                readUnlock();
            }

            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return "ReadWriteLock{" +
                "lock=" + lock +
                '}';
    }
}
