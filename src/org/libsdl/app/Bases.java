package org.libsdl.app;


import java.util.ArrayList;

/**
 * Created by anti88 on 26.10.15.
 */
public class Bases
{

    public class Base
    {
        private int version;
        private int id;
        private String name;

        Base(int id, String name, int version)
        {
            this.version = version;
            this.name = name;
            this.id = id;
        }

        public int getVersion() {
            return version;
        }
    }
    private ArrayList<Base> bases = new ArrayList<Base>();

    public void addBase(int v, int i, String n)
    {
        bases.add(new Base(i,n,v));
    }

    public Base getBase( int id )
    {
        for(Base l : bases)
        {
            if(l.id == id)
                return l;
        }
        return null;
    }
}
