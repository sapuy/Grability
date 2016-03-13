package imaginamos.prueba.leosap.objects;

import java.io.Serializable;

/**
 * Created by Leonard on 6/10/15.
 * Objeto de aplicaciones, que contiene las caracterísitas de cada app descargada
 */
public class ob_app implements Serializable{//Implementamos serializable para poder pasar el objeto serializado a la clase deatalles
    private String icon;
    private String nombre;
    private String resumen;
    private String precio;
    private String fecha;
    private String autor;
    private String link;
    private String link_d;
    private String categoria;
    private long id;


    public ob_app(String icon, String nombre, String resumen, String precio, String autor, String fecha, String categoria, String link, String link_d, long id) {
        super();
        this.icon = icon;
        this.nombre = nombre;
        this.resumen = resumen;
        this.precio = precio;
        this.autor=autor;
        this.fecha=fecha;
        this.categoria=categoria;
        this.link=link;
        this.link_d=link_d;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink_d() {
        return link_d;
    }

    public void setLink_d(String link_d) {
        this.link_d = link_d;
    }
}