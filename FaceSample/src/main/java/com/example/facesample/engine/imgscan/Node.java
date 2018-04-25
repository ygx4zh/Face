package com.example.facesample.engine.imgscan;



public class Node<V>{
    private V value;
    private Node<V> pre;
    private Node<V> next;

    public Node(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Node<V> getPre() {
        return pre;
    }

    public void setPre(Node<V> pre) {
        this.pre = pre;
    }

    public Node<V> getNext() {
        return next;
    }

    public void setNext(Node<V> next) {
        this.next = next;
    }

    public void append2Link(Node<V> node){

        if(node == null) throw new NullPointerException();

        if (this.next == null) {
            this.next = node;
            node.pre = this;
        }else{
            this.next.append2Link(node);
        }
    }
}
