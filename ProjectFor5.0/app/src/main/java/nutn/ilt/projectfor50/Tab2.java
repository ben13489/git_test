package nutn.ilt.projectfor50;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mu on 2017/8/11.
 */

public class Tab2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2,container,false);
        return view;
    }
    TextView textView;
    String callstat;
    String opname;
    String ptype;
    String pDetails;
    String sstate;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //引用getActivity()才可以使用Activity的參數
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        int callstate = tm.getCallState();  //通話狀態
        switch (callstate){
            case TelephonyManager.CALL_STATE_IDLE: //通話狀態閒置
                callstat="\n\n通話狀態: 閒置";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: //通話狀態接起
                callstat="\n\n通話狀態: 接聽中";
                break;
            case TelephonyManager.CALL_STATE_RINGING: //通話狀態響起
                callstat="\n\n通話狀態: 響鈴中";
                break;
        }

        opname="\n電信公司ID: "+tm.getNetworkOperator();//NetworkOperator 電信公司ID
        opname+="\n電信公司名稱: "+tm.getNetworkOperatorName();//NetworkOperatorName電信公司名稱
        opname+="\n電信網路國別: "+tm.getNetworkCountryIso();//NetworkCountryIso電信網路國別

        int phoneType=tm.getPhoneType(); //phoneType 行動通訊類型
        switch (phoneType){
            case TelephonyManager.PHONE_TYPE_CDMA:
                ptype="\n行動通訊類型: CDMA\n";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                ptype="\n行動通訊類型: GSM\n";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                ptype="\n行動通訊類型: SIP\n";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                ptype="\n行動通訊類型: None\n";
                break;
        }

        boolean isRoaming=tm.isNetworkRoaming(); //isNetworkRoaming 漫遊狀態

        if (isRoaming){
            pDetails="漫遊狀態 : Yes\n";
        }
        else {
            pDetails="漫遊狀態 : No\n";
        }

        int sim=tm.getSimState(); //SimState SIM卡狀態
        switch (sim){
            case TelephonyManager.SIM_STATE_ABSENT:
                sstate="SIM卡狀態 : Absent\n";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                sstate="SIM卡狀態 : Newtworked Locked\n";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                sstate="SIM卡狀態 : Pin Required\n";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                sstate="SIM卡狀態 : Puk Required\n";
                break;
            case TelephonyManager.SIM_STATE_READY:
                sstate="SIM卡狀態 : Ready\n";
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                sstate="SIM卡狀態 : Uknown\n";
                break;
        }

        /*tm.getNetworkType() 獲得的參數為數字,即第幾型,所以需從陣列來挑選*/
       /* String[] networkTypeArray = {"UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO 0", "EVDO A", "1xRTT", "HSDPA", "HSUPA", "HSPA"};
        String networkType ="\n行動網路類型: "+networkTypeArray[tm.getNetworkType()];*/


        textView=getActivity().findViewById(R.id.phoneTV);
        textView.setText(callstat+opname+ptype+pDetails+sstate); //print出全部資訊

    }
}
