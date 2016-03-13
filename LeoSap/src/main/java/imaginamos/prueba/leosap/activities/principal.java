package imaginamos.prueba.leosap.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import imaginamos.prueba.leosap.R;
import imaginamos.prueba.leosap.adapters.principal_list;
import imaginamos.prueba.leosap.common.constantes;
import imaginamos.prueba.leosap.common.funciones;
import imaginamos.prueba.leosap.handlers.Handler_HTTP;
import imaginamos.prueba.leosap.objects.ob_app;

public class principal extends Activity {
    TextView tv1, tv2, tv3;
    SlidingMenu menu;
    AbsListView lv;
    SharedPreferences pref;
    MaterialRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        funciones.set_orientation(this);
        setContentView(R.layout.activity_principal);

        funciones.instancia_actionbar(this, R.id.ly_principal_ll, R.layout.action_bar, getString(R.string.principal_titulo));//In
        menu = funciones.instanciar_menu(this, R.layout.menu_principal);

        lv = (AbsListView) findViewById(R.id.ly_principal_lv);
        tv1 = (TextView) findViewById(R.id.ly_principal_tv_cargando);
        tv1.setTypeface(funciones.get_font(this));
        lv.setVisibility(View.INVISIBLE);

        tv2 = (TextView) findViewById(R.id.menu_principal_item1);
        tv2.setTypeface(funciones.get_font(this));

        tv3 = (TextView) findViewById(R.id.menu_principal_item3);
        tv3.setTypeface(funciones.get_font(this));
        pref = funciones.getShared(this);
        refresh = (MaterialRefreshLayout) findViewById(R.id.refresh);

        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {

                Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
                anim2.setDuration(1000);
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lv.setVisibility(View.INVISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        anim.setDuration(1000);
                        tv1.setVisibility(View.VISIBLE);
                        tv1.startAnimation(anim);//aparecemos el texto "Cargando"
                        carga_inicial();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                lv.startAnimation(anim2);//desaparecemos el listview
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                //load more refreshing...
            }
        });

        carga_inicial();


    }

    private void carga_inicial() {
        //Validar conexion a internet
        if (!funciones.isConnected(this)) {
            Toast.makeText(this, R.string.error_toast_noconnected, Toast.LENGTH_LONG).show();
        }
        enviarGet tarea2 = new enviarGet(constantes.url_pruebas);
        tarea2.execute();//Se encarga de conectarse a internet y descargar el contenido de la url dada


    }


    @Override
    public void onBackPressed() {

        if (menu.isMenuShowing())
            menu.toggle();
        else {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            menu.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClickAB(View v) {

        if (constantes.debug)
            Log.d("LeoSap onClick", "View Id: " + getResources().getResourceEntryName(v.getId()));

        switch (v.getId()) {
            case R.id.actionbar_back:
                onBackPressed();
                break;

            case R.id.actionbar_menu:
                menu.toggle();
                break;
        }
    }

    public void onClickMenu(View v) {

        if (constantes.debug)
            Log.d("LeoSap onClick", "View Id: " + getResources().getResourceEntryName(v.getId()));
        menu.toggle();
        switch (v.getId()) {

            case R.id.menu_principal_item1://acerca de
                funciones.acerca_de(this);
                break;

            case R.id.menu_principal_item3://En caso de recargar, se desaparece el listview y se hace de nuevo el llamado a la función carga_inicial

                refresh.autoRefresh();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private class enviarGet extends AsyncTask<Void, Integer, Boolean> {

        private JSONObject response;
        private String url;
        ArrayList<ob_app> applications;
        BaseAdapter adapter;

        public enviarGet(String url) {
            this.url = url;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if (funciones.isConnected(principal.this)) {
                    response = Handler_HTTP.sendGet(url);//Obtenemos la respuesta usando la clase ManejadorHTTP
                } else {
                    response = new JSONObject(pref.getString(constantes.pref_json, null));//si no hay internet, usar el Json guardado en las shared //offline
                }

                try {
                    if (response == null) {
                        return false;//Si no se tiene respuesta se retorna falso
                    } else if (response.has("feed")) {//si se tiene respuesta instanciamos el objeto JSON y extraemos el contenido
                        applications = new ArrayList<ob_app>();

                        pref.edit().putString(constantes.pref_json, response.toString()).commit();//Guardamos el JSON en las shared preferences

                        JSONArray apps = response.getJSONObject("feed").getJSONArray("entry");
                        for (int i = 0; i <= apps.length() - 1; i++) {//recorremos el array de aplicaciones
                            JSONObject childJSONObject = apps.getJSONObject(i);
                            String name = childJSONObject.getJSONObject("im:name").getString("label");
                            String resumen = childJSONObject.getJSONObject("summary").getString("label");
                            float precio = Float.parseFloat(childJSONObject.getJSONObject("im:price").getJSONObject("attributes").getString("amount"));
                            String moneda = childJSONObject.getJSONObject("im:price").getJSONObject("attributes").getString("currency");
                            String autor = childJSONObject.getJSONObject("im:artist").getString("label");
                            String link_d = childJSONObject.getJSONObject("im:artist").getJSONObject("attributes").getString("href");
                            JSONArray images = childJSONObject.getJSONArray("im:image");
                            String icon = images.getJSONObject(2).getString("label");
                            long id = childJSONObject.getJSONObject("id").getJSONObject("attributes").getLong("im:id");
                            String fecha = childJSONObject.getJSONObject("im:releaseDate").getJSONObject("attributes").getString("label");
                            //String fecha="nada";
                            String categoria = childJSONObject.getJSONObject("category").getJSONObject("attributes").getString("label");
                            String link = childJSONObject.getJSONObject("link").getJSONObject("attributes").getString("href");
                            String pre;
                            if (precio > 0)
                                pre = String.valueOf(precio) + " " + moneda;
                            else
                                pre = getString(R.string.detalles_free);

                            String bmp = funciones.getBitmapFromURL(icon, id);//Guarda el bmp según una url dada y retorna la ruta de almacenamiento
                            ob_app app = new ob_app(bmp, name, resumen, pre, autor, fecha, categoria, link, link_d, id);//creamos un nuevo objeto app
                            applications.add(app);//llenamos el arraylist con el objeto app
                            Log.d("LeoSap App " + i, "Name: " + name + " Precio: " + pre + " Autor: " + autor + " Icon: " + icon + " - " + bmp + " Fecha: " + fecha + " Categoria: " + categoria + " Link: " + link);
                        }
                        adapter = new principal_list(principal.this, applications);//instanciamos el adaptador personalizado con el arraylist de apps

                        return true;
                    }
                } catch (JSONException e) {
                    if (constantes.debug) Log.e("LeoSap JSON", e.getMessage());

                }

            } catch (Exception e) {
                if (constantes.debug) Log.e("LeoSap ManejadorHTTP", e.getMessage());


            }
            return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {//Si la respuesta es correcta, se anima el list
                if (adapter != null) {
                    // adapter.notifyDataSetChanged();
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                            final int duration_anim=250;
                             Animation animn = AnimationUtils.loadAnimation(principal.this, R.anim.fadeout);
                            animn.setDuration(duration_anim);
                            animn.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    Animation anim=AnimationUtils.loadAnimation(principal.this,R.anim.fadein);
                                    anim.setDuration(duration_anim);
                                    anim.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            ob_app ob = applications.get(i);
                                            if (constantes.debug)
                                                Log.d("LeoSap AppLv", ob.getNombre() + " - " + ob.getPrecio() + " - " + ob.getCategoria());
                                            // TODO Auto-generated method stub
                                            //En caso de seleccionar alguna opción, se lanza un nuevo intent, enviando la instancia del objeto serializada como extra a la nueva actividad
                                            //Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(principal.this, detalles.class);
                                            intent.putExtra("app", ob);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });
                                   view.startAnimation(anim);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            view.startAnimation(animn);

                        }
                    });
                    //asignamos el adaptador al listview y animamos la entrada de éste
                    Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
                    anim2.setDuration(1000);
                    anim2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tv1.setVisibility(View.INVISIBLE);
                            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                            anim.setDuration(1000);
                            anim.setFillAfter(true);
                            // lv.setVisibility(View.VISIBLE);
                            lv.startAnimation(anim);//aparecemos el listview
                            refresh.finishRefresh();

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    tv1.startAnimation(anim2);//desaparecemos el textview "cargando"
                } else {//Si hubo respuesta del servidor, pero no se pudo generar el adaptador
                    Toast.makeText(principal.this, R.string.error_toast_errordesconocido, Toast.LENGTH_SHORT).show();
                }
            } else {//si no hubo respuesta del servidor
                Toast.makeText(principal.this, R.string.error_toast_noservidor, Toast.LENGTH_SHORT).show();
            }

        }


    }


}
