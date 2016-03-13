package imaginamos.prueba.leosap.activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.File;

import imaginamos.prueba.leosap.R;
import imaginamos.prueba.leosap.common.constantes;
import imaginamos.prueba.leosap.common.funciones;
import imaginamos.prueba.leosap.objects.ob_app;

public class detalles extends Activity {
    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
    ImageView iv1;

    SlidingMenu menu;
    ListView lv;
    ob_app app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        funciones.set_orientation(this);
                setContentView(R.layout.activity_detalles);

        Bundle bundle = getIntent().getExtras();
        app = (ob_app) bundle.getSerializable("app");

        funciones.instancia_actionbar(this, R.id.ly_detalles_ll, R.layout.action_bar, app.getNombre());//Creamos el action bar, indicando el layout y el titulo que debe llevar
        menu = funciones.instanciar_menu(this, R.layout.menu_detalles);//Creamos una instancia del menú según el layout dado

        //Instanciamos y cargamos el contenido
        tv1 = (TextView) findViewById(R.id.ly_detalles_tv_autor);
        tv1.setText(app.getAutor());
        tv1.setTypeface(funciones.get_font(this));

        tv2 = (TextView) findViewById(R.id.ly_detalles_tv_fecha);
        tv2.setText(getString(R.string.detalles_released) + " " + app.getFecha());
        tv2.setTypeface(funciones.get_font(this));

        tv3 = (TextView) findViewById(R.id.ly_detalles_tv_resumen);
        tv3.setText(app.getResumen());
        tv3.setTypeface(funciones.get_font(this));

        tv4 = (TextView) findViewById(R.id.ly_detalles_tv_categoria);
        tv4.setText(app.getCategoria());
        tv4.setTypeface(funciones.get_font(this));

        tv5 = (TextView) findViewById(R.id.ly_detalles_tv_precio);
        tv5.setText(app.getPrecio());
        tv5.setTypeface(funciones.get_font(this));

        iv1 = (ImageView) findViewById(R.id.ly_detalles_iv_icon);
        Bitmap myBitmap = BitmapFactory.decodeFile(new File(app.getIcon()).getAbsolutePath());//Dibujamos el ícono con efecto blur
        iv1.setImageBitmap(funciones.blurRenderScript(this, myBitmap, 5));

        tv6 = (TextView) findViewById(R.id.menu_detalles_item1);
        tv6.setTypeface(funciones.get_font(this));

        tv7 = (TextView) findViewById(R.id.menu_detalles_item3);
        tv7.setTypeface(funciones.get_font(this));

        tv8 = (TextView) findViewById(R.id.menu_detalles_item2);
        tv8.setTypeface(funciones.get_font(this));


    }

    @Override
    public void onBackPressed() {//En caso de presionar la tecla atrás, cerrar el menú si está abierto, finalziar la aplicación si no lo está

        if (menu.isMenuShowing())
            menu.toggle();
        else {
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//Si el dispositivo tiene botón físico de menú (Ejemplo samsung), mostrar o cerrar el menú
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            menu.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClickAB(View v) {//CLick del actionbar

        if (constantes.debug)
            Log.d("LeoSap onClick", "View Id: " + getResources().getResourceEntryName(v.getId()));

        switch (v.getId()) {
            case R.id.actionbar_back://En caso de presionar botón atrás, emular el comportamiento de la tecla física
                onBackPressed();
                break;

            case R.id.actionbar_menu://Mostrar o cerrar el menú de opciones
                menu.toggle();
                break;
        }
    }

    public void onClickMenu(View v) {//Click de los elementos del menú

        if (constantes.debug)
            Log.d("LeoSap onClick", "View Id: " + getResources().getResourceEntryName(v.getId()));
        menu.toggle();
        Intent browserIntent;
        switch (v.getId()) {
            case R.id.menu_detalles_item1://Click item 1, abrir página del desarrollador
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getLink_d()));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                break;
            case R.id.menu_detalles_item2://Item 2, abrir página de la aplicación
                funciones.acerca_de(this);

                break;
            case R.id.menu_detalles_item3://Acerca de
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getLink()));
                startActivity(browserIntent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                break;
        }
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

}