package org.libsdl.app;


import java.util.ArrayList;
import static org.libsdl.app.Utils.tinf;

/**
 * Created by anti88 on 26.10.15.
 */
public class Levels
{

    public class Level
    {
        private int version;
        private int id;
        private String name;

        Level(int id, String name, int version)
        {
            this.version = version;
            this.name = name;
            this.id = id;
        }

        public int getVersion() {
            return version;
        }
    }
    private ArrayList<Level> levels = new ArrayList<Level>();

    public void addLevel(int v, int i, String n)
    {
        levels.add(new Level(i,n,v));
    }

    public Level getLevel( int id )
    {
        for(Level l : levels)
        {
            if(l.id == id)
            {
                return l;
            }
        }
        return null;
    }
}
