package nutn.ilt.projectfor50;

import android.graphics.drawable.Drawable;

/**
 * Created by Mu on 2017/10/28.
 */

public class ApplicationBean {
    private String name;
    private Drawable icon;
    private long rx;
    private long tx;


    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Drawable getIcon(){
        return icon;
    }

    public void setIcon(Drawable icon){
        this.icon = icon;
    }

    public long getTx(){
        return tx;
    }

    public void setTx(long tx){
        this.tx = tx;
    }

    public long getRx(){
        return rx;
    }

    public void setRx(long rx){
        this.rx = rx;
    }

}
