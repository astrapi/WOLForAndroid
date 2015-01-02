package com.cod3scr1b3r.wol.expcetions;

/**
 * Created by Eyal on 02-1-15.
 */
public class WOLException extends Exception {

    public WOLException(){
        super("Error sending WO packet!");
    }

    public WOLException(Exception e){
        super(e);
    }
}
