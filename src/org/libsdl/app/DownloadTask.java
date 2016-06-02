package org.libsdl.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.libsdl.app.Utils.terr;
import static org.libsdl.app.Utils.tinf;

class DownloadTask extends AsyncTask<String, Void, String> {

    private Download download;
    String func;

    DownloadTask(Download dl)
    {
        download = dl;
    }

    //type			host			complete			queueName			target
    //bin/txt		hostname	paramstring		dlName				dir+filename
    @Override
    protected String doInBackground(String... info) {

        if(info[0] == "txt")
        {
						func = info[3];
            try {
                return loadFromNetwork(info[1]+info[2], func);
            } catch (IOException e) {
                terr("download fehler", e);
                return null;
            }
        }
        else if(info[0] == "bin")
        {
						func = null;
            return loadFile(info[1]+info[2], info[4], info[3]);
        }

        terr("Falscher typ!("+info[0]+")");
        return null;
    }

    private String loadFile(String urls, String dest, String func) {

        int count;
        try {
            URL url = new URL(urls);
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
						tinf("Speichere " + urls + " in " + dest);
            // Output stream
            OutputStream output = new FileOutputStream(dest);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                //for future implementation!
                // publishing the progress....
                // After this onProgressUpdate will be called
                //publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            terr("Download fehlgeschlagen", e);
        }
        return func;
    }
    private String loadFromNetwork(String urlString, String par) throws IOException {
        InputStream stream = null;
        String resp;

        try {
            stream = downloadUrl(urlString);
            resp = fromStream(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return resp;
    }
    private InputStream downloadUrl(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(45000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
    public String fromStream(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        download.actionDone(func, s);
    }
}
