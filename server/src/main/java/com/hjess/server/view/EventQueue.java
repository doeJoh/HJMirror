package com.hjess.server.view;


import java.util.LinkedList;

/**
 * EventQueue
 * Created by HalfmanG2 on 2018/10/25.
 */
class EventQueue {

    private volatile LinkedList<Event> events = new LinkedList<>();

    private float density;

    EventQueue(float density) {
        this.density = density;
    }

    synchronized void addEvent(int action, int x, int y) {
        x = (int)(x * density);
        y = (int)(y * density);
        if (action == 1 && events.size() > 0 && events.getLast().action == 1) {
            events.getLast().setValue(action, x, y);
        } else {
            events.addLast(new Event(action, x, y));
        }
    }

    synchronized Event getEvent() {
        if (events.size() == 0) {
            return null;
        }
        return events.removeFirst();
    }

    static class Event {
        int action;
        int x;
        int y;
        Event(int a, int x, int y) {
            setValue(a, x, y);
        }
        void setValue(int a, int x, int y) {
            this.action = a;
            this.x = x;
            this.y = y;
        }
    }
}
