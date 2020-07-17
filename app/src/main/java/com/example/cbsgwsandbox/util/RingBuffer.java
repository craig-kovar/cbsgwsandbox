package com.example.cbsgwsandbox.util;

public class RingBuffer<T> {

    private T[] data;
    private int capacity;
    private int writeloc;
    private int selectedSize = 0;

    public RingBuffer(int capacity) {
        this.data = (T[]) new Object[capacity];
        this.capacity = capacity;
        this.writeloc = -1;
    }

    public void setSelectedSize(int newSize) {
        this.selectedSize = newSize;
    }

    public int getSelectedSize() {
        return this.selectedSize;
    }

    public int getSize() {
        if (writeloc < capacity) {
            return writeloc+1;
        }

        return capacity;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getWriteloc() {
        if (this.writeloc < 0) { return 0; }

        return this.writeloc % this.capacity;
    }

//    public void resize(int newcapacity) {
//        T[] newData = (T[]) new Object[newcapacity];
//
//        int initSize = this.getSize();
//        int nwriteloc = 0;
//
//        for (nwriteloc = 0; nwriteloc < initSize; nwriteloc++) {
//            newData[nwriteloc%newcapacity] = data[writeloc%capacity];
//            writeloc--;
//            if (writeloc < 0) { writeloc = this.capacity - 1; }
//        }
//
//        this.capacity = newcapacity;
//        this.data = newData;
//        this.writeloc = --nwriteloc;
//    }

    public void offer(T element) {
        this.data[++writeloc%capacity] = element;
    }

    public T read(int loc) {
        return this.data[loc];
    }

    public String toString() {
        //String retVal = "{%s:%s/%s - [ ";
        String retVal = "{"+this.writeloc+":"
                + this.getSize()+"/"+this.getCapacity() + " - [ ";

        for (int i = 0; i < this.capacity; i++) {
            if (this.data[i] != null) {
                retVal = retVal + this.data[i];
                if (i < (this.capacity - 1)) {
                    retVal = retVal + "->";
                }
            }
        }

        retVal = retVal + " ]";

        return retVal;
    }

}
