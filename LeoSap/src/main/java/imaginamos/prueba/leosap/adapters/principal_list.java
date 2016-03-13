package imaginamos.prueba.leosap.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import imaginamos.prueba.leosap.R;
import imaginamos.prueba.leosap.activities.detalles;
import imaginamos.prueba.leosap.common.constantes;
import imaginamos.prueba.leosap.common.funciones;
import imaginamos.prueba.leosap.objects.ob_app;

/**
 * Created by Leonardo on 6/10/15.
 * Adaptador personalizado para el listview que muestra el listado de aplicaciones
 */
public class principal_list extends BaseAdapter {
    ArrayList<imaginamos.prueba.leosap.objects.ob_app> ob_app;
    Context context;
    private LayoutInflater inflater=null;

    public principal_list(Context context, ArrayList<ob_app> ob_app) {
        // TODO Auto-generated constructor stub
        this.ob_app = ob_app;
        this.context=context;
        inflater = ( LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ob_app.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv1,tv2,tv3;
        ImageView img1;
        public Holder(View v) {
            // rowView = inflater.inflate(R.layout.list_apps, null);
            tv1=(TextView) v.findViewById(R.id.lv_historial_tv1);
            tv1.setTypeface(funciones.get_font(context));
            tv2=(TextView) v.findViewById(R.id.lv_historial_tv2);
            tv2.setTypeface(funciones.get_font(context));
            tv3=(TextView) v.findViewById(R.id.lv_historial_tv3);
            tv3.setTypeface(funciones.get_font(context));
            img1=(ImageView) v.findViewById(R.id.lv_historial_iv1);
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder;//Se utiliza holder para evitar lentitud en el scroll del listado
       // View rowView;
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.list_apps, parent, false);
            // get all UI view
            holder = new Holder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (Holder) convertView.getTag();
        }


        ob_app app= ob_app.get(position);//Obtenemos la instancia del objeto app


        //Asignamos los valores a cada fila
        holder.tv1.setText(app.getNombre());
        holder.tv2.setText(app.getCategoria());
        holder.tv3.setText(app.getPrecio());
        Bitmap myBitmap = BitmapFactory.decodeFile(new File(app.getIcon()).getAbsolutePath());//Asignamos la imagen almacenada en almacenamiento interno decodificada
        holder.img1.setImageBitmap(myBitmap);


        return convertView;
    }

}