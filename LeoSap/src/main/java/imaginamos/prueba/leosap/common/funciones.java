package imaginamos.prueba.leosap.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import imaginamos.prueba.leosap.R;

/**
 * Created by leonardo on 5/10/15.
 */
public class funciones {

    public static Typeface get_font(Context context){//obtenemos la fuente de la aplicación
        Typeface font = Typeface.createFromAsset(context.getAssets(),
                imaginamos.prueba.leosap.common.constantes.font);
        return font;
    }

    public static SlidingMenu instanciar_menu(Context context,int layout){//Crea un nuevo menú según la activdiad y el layout dado
        SlidingMenu menu = new SlidingMenu(context);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
        menu.setShadowDrawable(R.drawable.sliding_menu_shadow);
        menu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
        menu.attachToActivity((Activity)context, SlidingMenu.SLIDING_CONTENT);
        // menu.setMenu(R.layout.menu_map);
         menu.setMenu(layout);
        return menu;
    }

    public static View instancia_actionbar(Context context, int layout, int actionbar,String titulo){//Crea un actionbar personalizado, según un layout y titulo dado
        LinearLayout item = (LinearLayout )((Activity)context).findViewById(layout);//obtenemos el linearlayout pasado como parámetro
        View action=((Activity)context).getLayoutInflater().inflate(actionbar, null);//inflamos el layout(actionbar) dado sobre el linear layout
        item.addView(action, 0);

        TextView tv1=(TextView)((Activity)context).findViewById(R.id.actionbar_title);
        tv1.setText(titulo);//Seteamos el titulo y la fuente
        tv1.setTypeface(funciones.get_font(context));

        return action;
    }

    public static void acerca_de(Context context){//muestra un alertdialog con la información del desarrollo
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 20, 20, 20);

        TextView myMsg = new TextView(context);
        myMsg.setText(R.string.acercade);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        myMsg.setTypeface(get_font(context));
        myMsg.setTextColor(context.getResources().getColor(R.color.leosap_azul_oscuro));
        layout.addView(myMsg, params);

        dialogo1.setView(layout);
        dialogo1.setCancelable(true);
        dialogo1.show();

    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {//Convierte inputstream a string
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }

    public static boolean isConnected(Context context){//valida si el dispositivo está conectado a internet
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String getBitmapFromURL(String imageUrl, long id) {//Obtiene la imagen según la url dada y se almacena en un archivo
        try {
            File folder=new File(Environment.getExternalStorageDirectory()
                    +File.separator+"LeoSap");
            if(!folder.exists())//creamos la carpeta si no existe
                folder.mkdir();

            File f = new File(Environment.getExternalStorageDirectory()
                    +File.separator+"LeoSap"+File.separator + String.valueOf(id)+".lsbmp");

            if(f.exists())//si el archivo existe, omitimos la descarga
                return f.getAbsolutePath();

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return saveBitMap(myBitmap,f);//Guardamos el bitmap y retornamos su ruta
        } catch (IOException e) {
            if(constantes.debug)Log.e("LeoSap getBitmap",e.getMessage());
            return null;
        }
    }

    public static String saveBitMap(Bitmap bmp, File file){//guardar bitmap
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        try {
            file.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {//Efecto blur de bitmap
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static SharedPreferences getShared(Context context){
        return context.getSharedPreferences(constantes.preferences, context.MODE_PRIVATE);
    }

    public static void set_orientation(Context context){
        if(!context.getResources().getBoolean(R.bool.portrait_only)){
            ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }else{
            ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }
}
