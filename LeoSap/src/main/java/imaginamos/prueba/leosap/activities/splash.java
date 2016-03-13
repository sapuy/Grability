package imaginamos.prueba.leosap.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import imaginamos.prueba.leosap.R;
import imaginamos.prueba.leosap.common.funciones;

public class splash extends Activity {
TextView tv1,tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        funciones.set_orientation(this);
        setContentView(R.layout.activity_splash);
        tv1=(TextView)findViewById(R.id.ly_splash_tv1);
        tv2=(TextView)findViewById(R.id.ly_splash_tv2);

        tv1.setTypeface(funciones.get_font(this));
        tv2.setTypeface(funciones.get_font(this));

        animacion();
    }
    public void animacion(){
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        anim.setDuration(2000);
        tv1.startAnimation(anim);//aparecemos el primer titulo

        Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        anim2.setDuration(4000);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {//Cuando acabe la segunda animación, se abre el activity principal
                Intent intent = new Intent(splash.this, principal.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tv2.startAnimation(anim2);//aparecemos el segundo titulo

    }
    @Override
    public void onBackPressed() {//Finaliza la actividad sobreescribiendo la animación de salida

            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }

    @Override
    protected void onDestroy() {//Recolector de basura al destruir la actividad
        System.gc();
        super.onDestroy();
    }
}
