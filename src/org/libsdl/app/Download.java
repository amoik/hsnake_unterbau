package org.libsdl.app;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static org.libsdl.app.Utils.mergeDirs;
import static org.libsdl.app.Utils.readFile;
import static org.libsdl.app.Utils.removeFile;
import static org.libsdl.app.Utils.strToJsonA;
import static org.libsdl.app.Utils.strToJsonO;
import static org.libsdl.app.Utils.terr;
import static org.libsdl.app.Utils.tinf;
import static org.libsdl.app.Utils.writeDir;
import static org.libsdl.app.Utils.writeFile;

/**
 * Created by anti88 on 25.10.15.
 */
public class Download
{

    private String host;
    private ArrayList<String> pending = new ArrayList<String>();
    private SDLActivity c;
    private DownloadTask dt;
    private File tmpDir;
    private File targetDir;
    private String baseJsonString;
    private String lvlJsonString;

    //filestrings
    String brStr = "baseRepo";
    String lrStr = "lvlRepo";
    String foStr = "anti.ttf";

    Download(String host, SDLActivity c, File dir)
    {
        this.targetDir = dir;
        this.tmpDir = new File(dir.toString()+"/.tmp");
        this.c = c;
        this.host = host;

    }

    public void run()
    {
        if(tmpDir.exists())
        {
				   tinf("Der letzte Download wurde nicht korrekt abgeschlossen!");
				   removeFile(this.tmpDir, ".tmp");
        }

        writeDir(this.tmpDir, ".tmp");
        writeDir(this.tmpDir, "base");
        writeDir(this.tmpDir, "lvl");

        File d = new File(targetDir, foStr);
        if (!d.exists())
            addToQueue("bin", "dlFont", "anti.ttf");

        addToQueue("txt", "getLevels", "getLevels");
        addToQueue("txt", "getBases", "getBases");
    }

    public Boolean checkAll()
    {
        File font = new File(targetDir, brStr);
        File base = new File(targetDir, foStr);
        File lvl = new File(targetDir, lrStr);

        if (font.exists() && base.exists() && lvl.exists())
            return true;
        else if(!font.exists())
            tinf("Schrift noch nicht vorhanden!");
        else if(!base.exists())
            tinf("Base noch nicht vorhanden!");
        else if(!lvl.exists())
            tinf("Levels noch nicht vorhanden!");

        return false;
    }

    public void addToQueue(String type, String func, String targetOrQueueName)
    {
        pending.add(targetOrQueueName);

        if(type == "txt")
        {
            dt = new DownloadTask(this);
            dt.execute(type, host, "?function="+func, targetOrQueueName);
        }
        else if(type == "bin")
        {
            dt = new DownloadTask(this);
            dt.execute(type, host, "?function="+func, targetOrQueueName, tmpDir.toString() + "/" + targetOrQueueName);
        }
    }

    public void actionDone(String par, String resp)
    {
        if(par != null) {
            //txt
            Boolean ret = false;
            String msg = "";
            String data = "";
            String type = "";

            JSONObject j = strToJsonO(resp);
            try {
                ret = j.getBoolean("return");
                msg = j.getString("msg");
                data = j.getString("data");
                type = j.getString("type");
            } catch (Exception e) {
                terr("JSON fehler", e);
            }

            pending.remove(par);

            if (resp != null && ret) {
                tinf("JSON " + par + " wurde abgeschlossen");
                if (type.equals("levelrepo"))
                    calcLevelRepo(data);
                else if (type.equals("baserepo"))
                    calcBaseRepo(data);
            } else {
                tinf(par + " ist fehlgeschlagen\"" + msg + "\"");
            }
        }
        else
        {
            tinf("Datei " + resp + " wurde abgeschlossen");
            pending.remove(resp);
        }
        if (pending.isEmpty()) {
            finished();
        }
    }

    private void calcBaseRepo(String data)
    {
        Bases oldBases = new Bases();

        String old = readFile(targetDir, brStr);

        if(old != null)
        {
            //die alten daten aus der datei laden
            JSONArray jo = strToJsonA(old);
            for(int i = 0; i < jo.length(); i++)
            {
                try {
                    int v = jo.getJSONObject(i).getInt("version");
                    String n = jo.getJSONObject(i).getString("name");
                    int id = jo.getJSONObject(i).getInt("id");
                    oldBases.addBase(v, id, n);
                }catch(Exception e){
                    terr("JSON fehler", e);
                }
            }
        }

        //die neuen daten lesen

        JSONArray j = strToJsonA(data);
        for(int i = 0; i < j.length(); i++)
        {
            try {
		              int v = j.getJSONObject(i).getInt("version");
		              String n = j.getJSONObject(i).getString("name");
		              int id = j.getJSONObject(i).getInt("id");
                	Bases.Base b = oldBases.getBase(id);
		              int dl = 0;

									//base schon vorhanden
		              if(old != null && b != null)
		              {
		              		//versionen untersuchen
		                  if(v > b.getVersion())
		                      dl = 1;
		              }
		              //level noch nicht vorhanden
		              else if(b == null)
		              {
				              dl = 1;
		              }

		              if(dl == 1)
		              {
		                  String ids = ""+id;
		                  addToQueue("bin", "dlBase&id=" + ids, "base/" + ids + ".adat");
		              }
            }catch(Exception e){
                terr("JSON fehler", e);
            }
        }
        writeFile(tmpDir, brStr, data);
    }
    private void calcLevelRepo(String d)
    {
        Levels oldLevels = new Levels();

        String old = readFile(targetDir, lrStr);

        if(old != null)
        {
            //die alten daten aus der datei laden
            JSONArray jo = strToJsonA(old);
            for(int i = 0; i < jo.length(); i++)
            {
                try {
                    int v = jo.getJSONObject(i).getInt("version");
                    String n = jo.getJSONObject(i).getString("name");
                    int id = jo.getJSONObject(i).getInt("id");
                    oldLevels.addLevel(v, id, n);
                }catch(Exception e){
                    terr("JSON fehler", e);
                }
            }
        }

        //die neuen daten lesen

        JSONArray j = strToJsonA(d);
        for(int i = 0; i < j.length(); i++)
        {
            try {
                int v = j.getJSONObject(i).getInt("version");
                String n = j.getJSONObject(i).getString("name");
                int id = j.getJSONObject(i).getInt("id");
                Levels.Level l = oldLevels.getLevel(id);
                int dl = 0;

								//level schon vorhanden
                if(old != null && l != null)
                {
                		//versionen untersuchen
                    if(v > l.getVersion())
                        dl = 1;
                }
                //level noch nicht vorhanden
                else if(l == null)
                {
		                dl = 1;
                }

                if(dl == 1)
                {
                    String ids = ""+id;
	                  addToQueue("bin", "dlLevel&id=" + ids, "lvl/" + ids + ".alvl");
                }

            }catch(Exception e){
                terr("JSON fehler", e);
            }
        }


        writeFile(tmpDir, lrStr, d);
    }

    private void finished()
    {
        tinf("Downloads wurden abgeschlossen!");
        deleteObsoletes(lrStr);
        mergeDirs(tmpDir, targetDir);
        removeFile(this.targetDir, ".tmp");

        c.startApp();
    }

    public void close()
    {
        if(!dt.isCancelled())
            dt.cancel(true);
    }

    private void deleteObsoletes(String fStr)
    {
        String oldJson = readFile(targetDir, fStr);
        String newJson = readFile(tmpDir, fStr);

        if(oldJson != null && newJson != null)
        {
            //die alten daten aus der datei laden
            JSONArray jo = strToJsonA(oldJson);
            JSONArray j = strToJsonA(newJson);

            for(int i = 0; i < jo.length(); i++)
            {
                try {
                    Boolean found = false;

                    for(int k = 0; k < j.length(); k++)
                    {
                        if(j.getJSONObject(k).getInt("id") == jo.getJSONObject(i).getInt("id"))
                            found = true;
                    }
                    if(!found)
                    {
                    		 File lvlPath = new File(targetDir.toString() + "/lvl/");
                    		 String lvlStr = jo.getJSONObject(i).getInt("id") + ".alvl";
                         tinf("lvl " + lvlPath.toString() + "/" + lvlStr + " ist veraltet und wird gelöscht!");
                         removeFile(lvlPath, lvlStr);
                   	}
                }catch(Exception e){
                    terr("JSON fehler", e);
                }
            }
        }
    }
}
