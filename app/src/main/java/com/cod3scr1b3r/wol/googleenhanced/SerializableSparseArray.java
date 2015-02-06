package com.cod3scr1b3r.wol.googleenhanced;

import android.util.SparseArray;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableSparseArray<E> extends SparseArray<E> implements Serializable {


    public SerializableSparseArray(int capacity){
        super(capacity);
    }

    public SerializableSparseArray(){
        super();
    }


    private void writeObject(ObjectOutputStream oo) throws IOException {
        SerializationPair<E>[] sp;
        int len = size();
        oo.writeInt(size());
        for(int i = 0; i < len; i++) {
            int key = keyAt(i);
            E data = get(key);
            oo.writeObject(new SerializationPair(key, data));
        }
    }


    private void readObject(ObjectInputStream oi) throws IOException, ClassNotFoundException {
        SerializationPair<E> sp;
        int len = oi.readInt();
        for(int i = 0; i < len; i++){
            sp = (SerializationPair<E>)oi.readObject();
            put(sp.getKey(), sp.getData());
        }
    }

    private class SerializationPair<E> implements  Serializable{
        private Integer mKey;
        private E mData;

        public SerializationPair(int key, E data){
            mKey = Integer.valueOf(key);
            mData = data;
        }

        private void writeObject(ObjectOutputStream os) throws IOException{
            os.write(mKey.intValue());
            os.writeObject(mData);
        }

        private void readObject(ObjectInputStream oi) throws IOException, ClassNotFoundException {
            mKey = oi.readInt();
            mData = (E)oi.readObject();
        }

        public int getKey(){
            return mKey;
        }

        public E getData(){
            return mData;
        }
    }


}