package nutn.ilt.projectfor50;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Mu on 2017/11/18.
 */

public class MySingleleton {
    private static MySingleleton mIstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    private MySingleleton(Context context)
    {
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized MySingleleton getInstancr(Context context)
    {
        if (mIstance==null)
        {
            mIstance = new MySingleleton(context);
        }
        return mIstance;
    }
    public<T> void addToRequestque(Request<T> request)
    {
        requestQueue.add(request);
    }
}
