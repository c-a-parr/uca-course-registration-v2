package edu.uca.registration.model;

import java.util.*;

// Course aggregate
// Holds roster and waitlist (Queue)
// No UI or I/O code
public final class Course {

    private final String code;

    private final String title;

    private final int capacity;

    private final List<String> roster = new ArrayList<>();

    private final Deque<String> waitlist = new ArrayDeque<>();

    public Course(String code, String title, int capacity, List<String> seedRoster, Deque<String> seedWaitlist) {

        this.code = Objects.requireNonNull(code);

        this.title = Objects.requireNonNull(title);

        if (capacity < 1) throw new IllegalArgumentException("capacity must be >= 1");
        this.capacity = capacity;

        if (seedRoster != null) this.roster.addAll(seedRoster);
        if (seedWaitlist != null) this.waitlist.addAll(seedWaitlist);
    }

    // Accessors
    public String code()      { return code; }
    public String title()     { return title; }
    public int capacity()     { return capacity; }

    // Read only snapshots
    public List<String> rosterView()   { return Collections.unmodifiableList(roster); }
    public Deque<String> waitlistView(){ return new ArrayDeque<>(waitlist); }


    // Mutators used by service and csv reconstruction, public so repo can reconstruct from enrollment
    public void addToRoster(String studentId) {
        if (!roster.contains(studentId)) roster.add(studentId);
    }
    public boolean removeFromRoster(String studentId) {
        return roster.remove(studentId);
    }
    public void addToWaitlist(String studentId) {
        if (!waitlist.contains(studentId)) waitlist.addLast(studentId);
    }
    public boolean removeFromWaitlist(String studentId) {
        return waitlist.remove(studentId);
    }
    /** FIFO promotion helper */
    public String popWaitlisted() {
        return waitlist.pollFirst();
    }

    @Override public String toString() {
        return code + " " + title + " cap=" + capacity +
                " enrolled=" + roster.size() + " wait=" + waitlist.size();
    }
}
