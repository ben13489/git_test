package nutn.ilt.projectfor50;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mu on 2017/8/11.
 */

public class Tab3 extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3,container,false);

        return view;
    }
    private List apps = new ArrayList();
    private ListView mLitView;
    private String[] Postdata = new String[4];
    Calendar calendar = Calendar.getInstance();
    private Button buttonRegister;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLitView = getActivity().findViewById(R.id.lv_app);
        getAppTrafficStatsList();
        MyAdapter adapter = new MyAdapter(apps,getContext());
        mLitView.setAdapter(adapter);
        TextView CD = getActivity().findViewById(R.id.CD);
        CD.setText(getMonthCallDetails());

        buttonRegister = getActivity().findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

    }

    private void getAppTrafficStatsList() {
        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> pinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);

        int total = 0;
        for (PackageInfo info :pinfos){
            String[] pers =info.requestedPermissions;
            if (pers != null&& pers.length>0){
                for (String per : pers){
                    if ("android.permission.INTERNET".equals(per)){
                        int uid = info.applicationInfo.uid;
                        String lable = (String) info.applicationInfo.loadLabel(pm);

                        Drawable icon = null;
                        try {
                            icon = pm.getApplicationIcon(info.packageName);
                        }catch (PackageManager.NameNotFoundException e){
                            e.printStackTrace();
                        }

                        long rx = TrafficStats.getUidRxBytes(uid);
                        total += rx;
                        long tx = TrafficStats.getUidTxBytes(uid);
                        total +=tx;
                        ApplicationBean app = new ApplicationBean();
                        app.setName(lable);
                        app.setIcon(icon);
                        app.setRx(rx);
                        app.setTx(tx);
                        apps.add(app);
                    }
                }
            }
        }
        TextView Total = getActivity().findViewById(R.id.Total);
        Total.setText("數據使用量:"+getFileSizeForPost(total));
        Postdata[3] = getFileSizeForPost(total);
    }


    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    public static String getFileSizeForPost(long size) {
        if (size <= 0)
            return "0";
        return new DecimalFormat("#,##0").format(size / Math.pow(1024, 2));
    }

    private String getMonthCallDetails(){
        StringBuffer sb = new StringBuffer();
        String strOrder = CallLog.Calls.DATE + " DESC"; //設定資料呈現的時間順序(此為新到舊),若為null則會從舊到新
        Uri callUri = Uri.parse("content://call_log/calls"); //CallLog.Calls.CONTENT_URI 透過系統URI權限獲取CallLog的內容
        Cursor managedCursor = getActivity().getContentResolver().query(callUri,null,null,null,strOrder);
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int total = 0;
        int outgoing =0;
        int OTforinner =0;
        int OTforoutter =0;
        int incoming = 0;
        int missed = 0;
        int outgoingduration = 0;
        int ODforinner =0;
        int ODforoutter = 0;
        int incomingduration = 0;
        sb.append("今年度:\n\n");
        sb.append("共有");
        while (managedCursor.moveToNext()){
            String phNumber = managedCursor.getString(number); //電話號碼轉成字串
            String telcom = null;
            String CHT = "(0905|0910|0911|0912|0919|0921|0928|0932|0933|09340|09341|09342|09343|09344|0937|0963|09650|09651|09652|09653|09654|09655|09656|09657|09658|09664|09665|09666|09667|0972|09740|09741|0975|0978|09842|09843|0988|0905).*";
            String FET = "(09000|09001|09002|09003|09004|09005|0903|0913|0915|0916|0917|0925|0926|0927|0930|09310|09311|09312|09313|0936|0938|09540|09541|0955|09605|09606|09607|09608|09609|09620|09668|09669|09670|09671|09672|09673|0980|0981|09742|09743|0976|0981|09840|09841|0989|0900|0903).*";
            String MYFONE = "(09010|09011|09012|09013|09014|0909|09140|09141|09142|0918|0920|0922|0923|09240|09241|0929|09314|09315|09316|09317|09318|09319|0935|0939|0952|0953|0956|0958|09600|09601|09602|09603|09604|09610|09611|09612|09613|09614|09660|09661|09662|09663|0970|09710|09711|09744|09745|0979|0983|09844|09845|0987|0901|0909).*";
            String GT ="(0906|0907|09682|09683|09684|09685|09770|09771|09772|09773|09774|09775|09776|09777|0980|0982|0985|0906|0907).*";
            String TSTAR ="(0908|09686|09687|09688|09689|09712|09713|09714|09715|09716|09717|09718|0973|09846|09847|0986|0908).*";
            if(phNumber != null){
                if (phNumber.startsWith("09")){
                    if(phNumber.matches(CHT)){
                        telcom = "中華電信";
                    }
                    else if (phNumber.matches(FET)){
                        telcom = "遠傳電信";
                    }
                    else if (phNumber.matches(MYFONE)){
                        telcom = "台灣大哥大";
                    }
                    else if (phNumber.matches(GT)){
                        telcom = "亞太電信";
                    }
                    else if (phNumber.matches(TSTAR)){
                        telcom = "台灣之星";
                    }
                    else {
                        telcom = "未知";
                    }
                }else {
                    telcom ="市話";
                }
            }
            String opname = tm.getNetworkOperatorName();
            int valIO;
            if (opname.matches(telcom)){
                valIO = 1;
            }else {
                valIO = 0;
            }
            String callType = managedCursor.getString(type); //電話類型(撥號/接聽/未接)轉成字串
            String callDate = managedCursor.getString(date); //將日期轉成字串
            Date callDaytime = new Date(Long.valueOf(callDate));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm"); //日期時間格式化
            String dateString = formatter.format(callDaytime); //時間
            String callDuration = managedCursor.getString(duration); //通話時間
            int Duration = managedCursor.getInt(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode){
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "打出去";
                    total +=1;
                    outgoing +=1;
                    outgoingduration +=Duration;
                    if (valIO ==1){
                        OTforinner +=1;
                        ODforinner +=Duration;
                    }else {
                        OTforoutter +=1;
                        ODforoutter +=Duration;
                    }
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "接聽";
                    total +=1;
                    incoming +=1;
                    incomingduration += Duration;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "未接";
                    total +=1;
                    missed +=1;
                    break;
            }

            /*sb.append("\n號碼: " + phNumber +
                    "\n電信公司: "+telcom +" ["+ IO +"]"+
                    "\n類型: " + dir +
                    "\n日期: "+ dateString+
                    "\n通話時間(秒): " + callDuration + "秒");
            sb.append("\n---------------------------------");*/
        }
        Calendar rightNow = Calendar.getInstance();
        sb.insert(8,""+total+"筆通話紀錄\n撥出次數: "+outgoing
                +"筆\n網內撥出次數: "+OTforinner
                +"筆\n網外撥出次數: "+OTforoutter
                +"筆\n接聽次數: "+incoming
                +"筆\n未接次數: "+missed
                +"筆\n撥出通話時間: "+outgoingduration
                +"秒\n網內撥出通話時間: "+ODforinner/(rightNow.get(Calendar.MONTH)+1)
                +"秒\n網外撥出通話時間: "+ODforoutter/(rightNow.get(Calendar.MONTH)+1)
                +"秒\n接聽通話時間: "+incomingduration
                +"秒");

        Postdata[0] = String.valueOf(ODforinner/(rightNow.get(Calendar.MONTH)+1));
        Postdata[1] = String.valueOf(ODforoutter/(rightNow.get(Calendar.MONTH)+1));
        Postdata[2] = String.valueOf(outgoingduration/outgoing);
        managedCursor.close();
        return sb.toString();
    }


    TextView fee,project,url;
    String json_url = "http://192.168.1.135/emono/test.php";


    @Override
    public void onClick(View view) {
        if (view == buttonRegister){
            final String fourg,innet,outnet,phonecall;
            innet = Postdata[0];
            outnet = Postdata[1];
            phonecall = Postdata[2];
            fourg = Postdata[3];
            final StringBuffer stringBuffer =new StringBuffer();
            StringRequest stringRequest =new StringRequest(Request.Method.POST, json_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject json = new JSONObject(response);
                        stringBuffer.append(json.getString("資費")+"\n"+json.getString("方案")+"\n"+json.getString("完整資費內容"));
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                        View mView = getActivity().getLayoutInflater().inflate(R.layout.turnbackdata,null);
                        mBuilder.setView(mView)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        TextView textView = mView.findViewById(R.id.back);
                        textView.setText(stringBuffer);
                        AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        //fee.setText(json.getString("資費"));
                        //project.setText(json.getString("方案"));
                        //url.setText(json.getString("完整資費內容"));
                        //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), error.getMessage(),Toast.LENGTH_LONG).show();
                    error.printStackTrace();

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("fourg",fourg);
                    params.put("innet",innet);
                    params.put("outnet",outnet);
                    params.put("phonecall",phonecall);
                    return  params;
                }
            };

            MySingleleton.getInstancr(getActivity()).addToRequestque(stringRequest);
        }
    }
}
