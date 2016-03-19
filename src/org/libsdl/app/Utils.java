package org.libsdl.app;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static java.lang.System.exit;

/**
 * Created by anti88 on 19.10.15.
 */
public class Utils
{

    public static void tinf(String msg)
    {
        Log.v("HSNAKE", msg);
    }

    public static void terr(String msg, Exception e)
    {
        Log.e("HSNAKE", msg, e);
    }
    public static void terr(String msg)
    {
        Log.e("HSNAKE", msg);
    }

    public static void removeRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                removeRecursive(child);

        fileOrDirectory.delete();
    }


    public static boolean isConnected(Context ct)
    {
        ConnectivityManager connMgr = (ConnectivityManager) ct.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static void writeFile(File path, String file, String data)
    {
        FileOutputStream outputStream;

        try {
            File f = new File(path, file);
            OutputStream out = new FileOutputStream(f);
            data += '\n';
            out.write(data.getBytes());
            out.close();
        } catch (Exception e) {
            terr("Fehler beim erstellen von \"" + file + "\"", e);
        }
    }

    public static void writeDir(File path, String dir)
    {
        File d = new File(path, dir);
        if (!d.exists())
            d.mkdirs();
    }


    public static void removeFile(File path, String file)
    {
        FileOutputStream outputStream;

        try {
            File f = new File(path, file);
            if(f.exists())
                removeRecursive(f);
            else
            	terr(file + " existiert nicht in " + path.toString());
        } catch (Exception e) {
            terr("Fehler beim löschen von \"" + file + "\"", e);
        }
    }

    public static JSONObject strToJsonO(String in)
    {
        try
        {
            JSONTokener tokener = new JSONTokener(in);
            JSONObject jsonData = new JSONObject(tokener);
            return jsonData;
        }
        catch(Exception e)
        {
            terr("Fehler beim JSON-konvertieren:\r\n"+in,e);
            exit(0);
        }
        return null;
    }

    public static JSONArray strToJsonA(String in)
    {
        try
        {
            JSONTokener tokener = new JSONTokener(in);
            JSONArray jsonData = new JSONArray(tokener);
            return jsonData;
        }
        catch(Exception e)
        {
            terr("Fehler beim JSON-konvertieren:\r\n"+in,e);
            exit(0);
        }
        return null;
    }

    public static String readFile(File dir, String file)
    {
        File f = new File(dir,file);

        StringBuilder ret = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;

            while ((line = br.readLine()) != null)
            {
                ret.append(line);
                //ret.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            return null;
        }
        return ret.toString();
    }

    public static void mvDir(File dir, String from, String to)
    {
        File f = new File(dir+from);
        File t = new File(dir+to);
        tinf("INFO: " + dir+from + " " + dir+to);
        f.renameTo(t);
    }

    public static void mergeDirs(File tmp, File tar)
    {
        tinf("processing new version of " + tar.toString());

        for (final File i : tmp.listFiles())
        {
            String sp[] = i.toString().split("/");
            if (i.isDirectory())
            {
                mergeDirs(i, new File(tar + "/" + sp[sp.length-1]));
            }
            else
            {
                moveFile(tmp.toString(), "/" + sp[sp.length-1], tar.toString());
            }

        }
        //String f[] = tmp.toString().split("/");
        //removeFile(tmp, f[f.length-1]);
    }


    private static void moveFile(String inputPath, String inputFile, String outputPath)
    {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            terr(fnfe1.getMessage());
        }
        catch (Exception e) {
            terr(e.getMessage());
        }

    }

    public void listFilesForFolder(final File folder)
{
        for (final File i : folder.listFiles())
        {
            if (i.isDirectory())
            {
                listFilesForFolder(i);
            }
            else
            {
                System.out.println(i.getName());
            }
        }
    }
}
