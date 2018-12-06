package nutn.ilt.projectfor50;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mu on 2017/8/11.
 */

public class Menu2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu2,container,false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("流量數據");
    }
    BarChart barChart;  //宣告MPAndroid的BarChart 長條圖
    Calendar calendar = Calendar.getInstance(); //宣告日曆,取得目前時間
    NumberPicker numPicker;
    NumberPicker Mpicker;
    Button btn;
    Button btn_preview;
    String TV_Detail;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        barChart = getActivity().findViewById(R.id.bargraph); //因為不是在MainActivity進行宣告,所以要取得Activity
        numPicker = getActivity().findViewById(R.id.picker);
        Mpicker = getActivity().findViewById(R.id.monhtpicker);
        btn = getActivity().findViewById(R.id.button);
        btn_preview = getActivity().findViewById(R.id.button_preview);
        btn.setOnClickListener(btnClickListener); //設定按鈕監聽(監聽指令在下方有設定)
        btn_preview.setOnClickListener(btn_previewClickListener);



        numPicker.setMaxValue(2100);//號碼選擇最大值
        numPicker.setMinValue(1900);//最小值

        numPicker.setWrapSelectorWheel(false);//選擇器外框
        /*設定值變監聽*/
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                numPicker.setValue(newValue);
                barChart.invalidate();
                TV_Detail= getYearCallDetails();
                setData();
            }
        });
        Mpicker.setMaxValue(12);
        Mpicker.setMinValue(1);

        Mpicker.setWrapSelectorWheel(false);
        Mpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                Mpicker.setValue(newValue);
                barChart.invalidate();
                TV_Detail = getMonthCallDetails();
                setMData();
            }
        });
        Mpicker.setValue(8);
        numPicker.setValue(2017);

        XAxis xAxis = barChart.getXAxis(); //取得長條圖的X軸
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        /*設定X軸單位值--月(日)*/
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            protected String[] mMonths = new String[]{
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int days = (int) value;
                int year = determineYear(days);
                int month = determineMonth(days);
                String monthName = mMonths[Mpicker.getValue()-1 % mMonths.length]; //month修改為Mpicker.getValue()-1取得對應的月份
                String yearName = String.valueOf(year);

                if (barChart.getVisibleXRange() > 30 * 6) {

                    return monthName + " " + yearName;
                } else {

                    int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));

                    String appendix = "th";

                    switch (dayOfMonth) {
                        case 1:
                            appendix = "st";
                            break;
                        case 2:
                            appendix = "nd";
                            break;
                        case 3:
                            appendix = "rd";
                            break;
                        case 21:
                            appendix = "st";
                            break;
                        case 22:
                            appendix = "nd";
                            break;
                        case 23:
                            appendix = "rd";
                            break;
                        case 31:
                            appendix = "st";
                            break;
                    }

                    return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
                }
            }

            private int getDaysForMonth(int month, int year) {

                // month is 0-based

                if (month == 1) {
                    boolean is29Feb = false;

                    if (year < 1582)
                        is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
                    else if (year > 1582)
                        is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);

                    return is29Feb ? 29 : 28;
                }

                if (month == 3 || month == 5 || month == 8 || month == 10)
                    return 30;
                else
                    return 31;
            }

            private int determineMonth(int dayOfYear) {

                int month = -1;
                int days = 0;

                while (days < dayOfYear) {
                    month = month + 1;

                    if (month >= 12)
                        month = 0;

                    int year = determineYear(days);
                    days += getDaysForMonth(month, year);
                }

                return Math.max(month, 0);
            }

            private int determineDayOfMonth(int days, int month) {

                int count = 0;
                int daysForMonths = 0;

                while (count < month) {

                    int year = determineYear(daysForMonths);
                    daysForMonths += getDaysForMonth(count % 12, year);
                    count++;
                }

                return days - daysForMonths;
            }

            private int determineYear(int days) {

                if (days <= 366)
                    return 2016;
                else if (days <= 730)
                    return 2017;
                else if (days <= 1094)
                    return 2018;
                else if (days <= 1458)
                    return 2019;
                else
                    return 2020;

            }
        });

        YAxis yRight = barChart.getAxisRight();
        yRight.setDrawLabels(false);
        setMData();
        TV_Detail = getMonthCallDetails(); //預設電話紀錄(單月)成字串
    }


    private Button.OnClickListener btnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(final View view) {
            /*AlertDialog設定*/
            final String[] test = {"月","年"};
            AlertDialog.Builder dialog_list = new AlertDialog.Builder(getActivity());
            dialog_list.setTitle("檢視");
            dialog_list.setItems(test, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    Toast.makeText(getActivity(),"your choose is"+test[which],Toast.LENGTH_SHORT).show();
                    /*1=年 X軸單位--年(月)*/
                    if (which==1){
                        TV_Detail = getYearCallDetails();
                        Mpicker.setVisibility(View.INVISIBLE); //按鈕消失
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                String[] mMonths = new String[]{
                                        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                                };
                                return mMonths[(int) value % mMonths.length];
                            }
                        });
                        setData();
                    }
                    else {
                        TV_Detail = getMonthCallDetails();//設定電話紀錄呈現單月傳至"總覽"的dialog
                        Mpicker.setVisibility(View.VISIBLE); //按鈕顯示
                        numPicker.setVisibility(View.VISIBLE);
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setValueFormatter(new IAxisValueFormatter() {
                            protected String[] mMonths = new String[]{
                                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                            };
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {

                                int days = (int) value;

                                int year = determineYear(days);

                                int month = determineMonth(days);
                                String monthName = mMonths[Mpicker.getValue()-1 % mMonths.length]; //month修改為calendar.get(Calendar.MONTH)取得對應的月份
                                String yearName = String.valueOf(year);

                                if (barChart.getVisibleXRange() > 30 * 6) {

                                    return monthName + " " + yearName;
                                } else {

                                    int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));

                                    String appendix = "th";

                                    switch (dayOfMonth) {
                                        case 1:
                                            appendix = "st";
                                            break;
                                        case 2:
                                            appendix = "nd";
                                            break;
                                        case 3:
                                            appendix = "rd";
                                            break;
                                        case 21:
                                            appendix = "st";
                                            break;
                                        case 22:
                                            appendix = "nd";
                                            break;
                                        case 23:
                                            appendix = "rd";
                                            break;
                                        case 31:
                                            appendix = "st";
                                            break;
                                    }

                                    return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
                                }
                            }

                            private int getDaysForMonth(int month, int year) {

                                // month is 0-based

                                if (month == 1) {
                                    boolean is29Feb = false;

                                    if (year < 1582)
                                        is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
                                    else if (year > 1582)
                                        is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);

                                    return is29Feb ? 29 : 28;
                                }

                                if (month == 3 || month == 5 || month == 8 || month == 10)
                                    return 30;
                                else
                                    return 31;
                            }

                            private int determineMonth(int dayOfYear) {

                                int month = -1;
                                int days = 0;

                                while (days < dayOfYear) {
                                    month = month + 1;

                                    if (month >= 12)
                                        month = 0;

                                    int year = determineYear(days);
                                    days += getDaysForMonth(month, year);
                                }

                                return Math.max(month, 0);
                            }

                            private int determineDayOfMonth(int days, int month) {

                                int count = 0;
                                int daysForMonths = 0;

                                while (count < month) {

                                    int year = determineYear(daysForMonths);
                                    daysForMonths += getDaysForMonth(count % 12, year);
                                    count++;
                                }

                                return days - daysForMonths;
                            }

                            private int determineYear(int days) {

                                if (days <= 366)
                                    return 2016;
                                else if (days <= 730)
                                    return 2017;
                                else if (days <= 1094)
                                    return 2018;
                                else if (days <= 1458)
                                    return 2019;
                                else
                                    return 2020;

                            }
                        });
                        setMData();
                    }
                }
            });
            dialog_list.show();
        }
    };

    private Button.OnClickListener btn_previewClickListener =new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.mydetails,null);
            mBuilder.setView(mView)
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            TextView textView = mView.findViewById(R.id.text_detail);
            textView.setText(TV_Detail);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
        }
    };

    private String getMonthCallDetails(){
        int yearpick = numPicker.getValue();
        int monthpick = Mpicker.getValue();
        StringBuffer sb = new StringBuffer();
        String strOrder = CallLog.Calls.DATE + " DESC"; //設定資料呈現的時間順序(此為新到舊),若為null則會從舊到新
        Uri callUri = Uri.parse("content://call_log/calls"); //CallLog.Calls.CONTENT_URI 透過系統URI權限獲取CallLog的內容

        calendar.set(yearpick,monthpick-1,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        /*calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));//抓取當月第一天*/
        calendar.set(Calendar.HOUR_OF_DAY,0);//設定為當日 00:00
        String fromDate = String.valueOf(calendar.getTimeInMillis()); //設定字串:開始日期,getTimeInMillis()轉換為毫秒數
        calendar.set(yearpick,monthpick-1,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        /*calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));//抓取當月最後一天*/
        calendar.set(Calendar.HOUR_OF_DAY,24);//設定為當日 24:00

        String toDate = String.valueOf(calendar.getTimeInMillis());//設定字串:截止日期
        String[] whereValue = {fromDate,toDate};//設定字串陣列,以提供給資料庫

        //獲取資料庫內容
        Cursor managedCursor = getActivity().getContentResolver().query(callUri,null,CallLog.Calls.DATE+" BETWEEN ? AND ?",whereValue,strOrder);

        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int total = 0;
        int outgoing =0;
        int incoming = 0;
        int missed = 0;
        int outgoingduration = 0;
        int incomingduration = 0;
        int ODforinner = 0;
        int OTinner = 0;
        int ODforoutter = 0;
        int OToutter = 0;
        sb.append("這個月:\n\n");
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
            String IO;
            int valO;
            if (opname.matches(telcom)){
                IO ="網內";
                valO = 1;
            }else {
                IO="網外";
                valO = 0;
            }
            String callType = managedCursor.getString(type); //電話類型(撥號/接聽/未接)轉成字串
            String callDate = managedCursor.getString(date); //將日期轉成字串
            Date callDaytime = new Date(Long.valueOf(callDate));
            calendar.setTime(callDaytime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm"); //日期時間格式化
            String dateString = formatter.format(callDaytime); //時間
            String callDuration = managedCursor.getString(duration); //通話時間
            int Duration = managedCursor.getInt(duration);

            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode){
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "打出去";
                    switch (calendar.get(Calendar.MONTH)){
                        case Calendar.JANUARY:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.FEBRUARY:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.MARCH:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.APRIL:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.MAY:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.JUNE:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.JULY:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.AUGUST:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.SEPTEMBER:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.OCTOBER:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                ODforinner +=Duration;
                            }else {
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.NOVEMBER:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
                        case Calendar.DECEMBER:
                            total +=1;
                            outgoing +=1;
                            outgoingduration += Duration;
                            if(valO == 1){
                                OTinner +=1;
                                ODforinner +=Duration;
                            }else {
                                OToutter +=1;
                                ODforoutter +=Duration;
                            }
                            break;
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
            sb.append("\n號碼: " + phNumber +
                    "\n電信公司: "+telcom +" ["+ IO +"]"+
                    "\n類型: " + dir +
                    "\n日期: "+ dateString+
                    "\n通話時間(秒): " + callDuration + "秒");
            sb.append("\n---------------------------------");
        }
        sb.insert(8,""+total+"筆通話紀錄\n撥出次數: "+outgoing
                +"筆\n網內撥打次數: "+OTinner
                +"筆\n網外撥打次數: "+OToutter
                +"筆\n接聽次數: "+incoming
                +"筆\n未接次數: "+missed
                +"筆\n撥出通話時間: "+outgoingduration
                +"秒\n網內撥出通話時間: "+ODforinner
                +"秒\n網外撥出通話時間: "+ODforoutter
                +"秒\n");
        managedCursor.close();
        return sb.toString();
    }
    private String getYearCallDetails(){
        StringBuffer sb = new StringBuffer();
        String strOrder = CallLog.Calls.DATE + " DESC"; //設定資料呈現的時間順序(此為新到舊),若為null則會從舊到新
        Uri callUri = Uri.parse("content://call_log/calls"); //CallLog.Calls.CONTENT_URI 透過系統URI權限獲取CallLog的內容
        int yearpick = numPicker.getValue();

        //calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));//抓取當月第一天
        calendar.set(yearpick,0,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,0);//設定為當日 00:00
        String fromDate = String.valueOf(calendar.getTimeInMillis()); //設定字串:開始日期,getTimeInMillis()轉換為毫秒數
        //calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));//抓取當月最後一天
        calendar.set(yearpick,11,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,24);//設定為當日 24:00
        String toDate = String.valueOf(calendar.getTimeInMillis());//設定字串:截止日期
        String[] whereValue = {fromDate,toDate};//設定字串陣列,以提供給資料庫

        Cursor managedCursor = getActivity().getContentResolver().query(callUri,null,CallLog.Calls.DATE+" BETWEEN ? AND ?",whereValue,strOrder);

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
            String IO;
            int valIO;
            if (opname.matches(telcom)){
                IO ="網內";
                valIO = 1;
            }else {
                IO="網外";
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

            sb.append("\n號碼: " + phNumber +
                    "\n電信公司: "+telcom +" ["+ IO +"]"+
                    "\n類型: " + dir +
                    "\n日期: "+ dateString+
                    "\n通話時間(秒): " + callDuration + "秒");
            sb.append("\n---------------------------------");
        }
        sb.insert(8,""+total+"筆通話紀錄\n撥出次數: "+outgoing
                +"筆\n網內撥出次數: "+OTforinner
                +"筆\n網外撥出次數: "+OTforoutter
                +"筆\n接聽次數: "+incoming
                +"筆\n未接次數: "+missed
                +"筆\n撥出通話時間: "+outgoingduration
                +"秒\n網內撥出通話時間: "+ODforinner
                +"秒\n網外撥出通話時間: "+ODforoutter
                +"秒\n接聽通話時間: "+incomingduration
                +"秒\n");
        managedCursor.close();
        return sb.toString();
    }

    private void setData(){
        ArrayList<BarEntry> barEntries = new ArrayList<>(); //設定長條圖資料源
        for (int i = 0; i<getYCallDetails().length;i++ ){
            barEntries.add(new BarEntry(i,new float[]{getYCallDetails()[i][1],getYCallDetails()[i][2]}));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setStackLabels(new String[]{"撥出(通)","通話時間(秒)"});
        barDataSet.setColors(new int[]{Color.rgb(247, 171,72), Color.rgb(23, 197, 255)});
        barDataSet.setValueTextSize(10f);


        BarData theData = new BarData(barDataSet);
        theData.setBarWidth(0.9f);

        barChart.setData(theData);
        barChart.setFitBars(true);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);


        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
    }
    private int[][] getYCallDetails(){
        int yearpick = numPicker.getValue();

        String strOrder = CallLog.Calls.DATE + " DESC"; //設定資料呈現的時間順序(此為新到舊),若為null則會從舊到新
        Uri callUri = Uri.parse("content://call_log/calls"); //CallLog.Calls.CONTENT_URI 透過系統URI權限獲取CallLog的內容


        //calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));//抓取當月第一天
        calendar.set(yearpick,0,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,0);//設定為當日 00:00
        String fromDate = String.valueOf(calendar.getTimeInMillis()); //設定字串:開始日期,getTimeInMillis()轉換為毫秒數
        //calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));//抓取當月最後一天
        calendar.set(yearpick,11,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,24);//設定為當日 24:00
        String toDate = String.valueOf(calendar.getTimeInMillis());//設定字串:截止日期
        String[] whereValue = {fromDate,toDate};//設定字串陣列,以提供給資料庫

        //獲取資料庫內容
        Cursor managedCursor = getActivity().getContentResolver().query(callUri,null,CallLog.Calls.DATE+" BETWEEN ? AND ?",whereValue,strOrder);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int total = 0;
        int outgoing =0;
        int jan_og = 0;
        int feb_og = 0;
        int mar_og = 0;
        int apr_og = 0;
        int may_og = 0;
        int jun_og = 0;
        int jul_og = 0;
        int aug_og = 0;
        int sep_og = 0;
        int oct_og = 0;
        int nov_og = 0;
        int dec_og = 0;
        int incoming = 0;
        int missed = 0;

        int[][] eachmonthduration;
        eachmonthduration = new int[12][3];
        int outgoingduration = 0;
        int jan_ogd = 0;
        int feb_ogd = 0;
        int mar_ogd = 0;
        int apr_ogd = 0;
        int may_ogd = 0;
        int jun_ogd = 0;
        int jul_ogd = 0;
        int aug_ogd = 0;
        int sep_ogd = 0;
        int oct_ogd = 0;
        int nov_ogd = 0;
        int dec_ogd = 0;
        int incomingduration = 0;

        while (managedCursor.moveToNext()){
            String phNumber = managedCursor.getString(number); //電話號碼轉成字串
            String callType = managedCursor.getString(type); //電話類型(撥號/接聽/未接)轉成字串
            String callDate = managedCursor.getString(date); //將日期轉成字串
            Date callDaytime = new Date(Long.valueOf(callDate));
            calendar.setTime(callDaytime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm"); //日期時間格式化
            String dateString = formatter.format(callDaytime); //時間
            String callDuration = managedCursor.getString(duration); //通話時間
            int Duration = managedCursor.getInt(duration);

            int dircode = Integer.parseInt(callType);
            switch (dircode){
                case CallLog.Calls.OUTGOING_TYPE:
                    switch (calendar.get(Calendar.MONTH)){
                        case Calendar.JANUARY:
                            eachmonthduration[0][0] = 0; //[x][0]放入月份 /*若無資料,則所有值皆回傳0*/
                            total +=1; //總通話數
                            outgoing +=1; //總撥出數
                            jan_og +=1; //1月份撥出數
                            eachmonthduration[0][1] =jan_og; //[x][1]放入撥出數
                            outgoingduration += Duration; //總撥出時間
                            jan_ogd += Duration;
                            eachmonthduration[0][2] = jan_ogd; //[x][2]放入撥出時間
                            break;
                        case Calendar.FEBRUARY:
                            eachmonthduration[1][0] = 1;
                            total +=1;
                            outgoing +=1;
                            feb_og +=1;
                            eachmonthduration[1][1] = feb_og;
                            outgoingduration += Duration;
                            feb_ogd += Duration;
                            eachmonthduration[1][2] = feb_ogd;
                            break;
                        case Calendar.MARCH:
                            eachmonthduration[2][0] = 2;
                            total +=1;
                            outgoing +=1;
                            mar_og +=1;
                            eachmonthduration[2][1] = mar_og;
                            outgoingduration += Duration;
                            mar_ogd += Duration;
                            eachmonthduration [2][2] = mar_ogd;
                            break;
                        case Calendar.APRIL:
                            eachmonthduration[3][0]=3;
                            total +=1;
                            outgoing +=1;
                            apr_og+=1;
                            eachmonthduration[3][1]=apr_og;
                            outgoingduration += Duration;
                            apr_ogd += Duration;
                            eachmonthduration [3][2] = apr_ogd;
                            break;
                        case Calendar.MAY:
                            eachmonthduration[4][0]=4;
                            total +=1;
                            outgoing +=1;
                            may_og +=1;
                            eachmonthduration[4][1]+=may_og;
                            outgoingduration += Duration;
                            may_ogd += Duration;
                            eachmonthduration[4][2] = may_ogd;
                            break;
                        case Calendar.JUNE:
                            eachmonthduration[5][0]=5;
                            total +=1;
                            outgoing +=1;
                            jun_og+=1;
                            eachmonthduration[5][1]=jun_og;
                            outgoingduration += Duration;
                            jun_ogd += Duration;
                            eachmonthduration[5][2]=jun_ogd;
                            break;
                        case Calendar.JULY:
                            eachmonthduration[6][0]=6;
                            total +=1;
                            outgoing +=1;
                            jul_og+=1;
                            eachmonthduration[6][1]=jul_og;
                            outgoingduration += Duration;
                            jul_ogd += Duration;
                            eachmonthduration[6][2]=jul_ogd;
                            break;
                        case Calendar.AUGUST:
                            eachmonthduration[7][0]=7;
                            total +=1;
                            outgoing +=1;
                            aug_og+=1;
                            eachmonthduration[7][1]=aug_og;
                            outgoingduration += Duration;
                            aug_ogd += Duration;
                            eachmonthduration [7][2]=aug_ogd;
                            break;
                        case Calendar.SEPTEMBER:
                            eachmonthduration[8][0]=8;
                            total +=1;
                            outgoing +=1;
                            sep_og+=1;
                            eachmonthduration[8][1]=sep_og;
                            outgoingduration += Duration;
                            sep_ogd += Duration;
                            eachmonthduration [8][2] = sep_ogd;
                            break;
                        case Calendar.OCTOBER:
                            eachmonthduration[9][0]=9;
                            total +=1;
                            outgoing +=1;
                            oct_og+=1;
                            eachmonthduration[9][1]=oct_og;
                            outgoingduration += Duration;
                            oct_ogd += Duration;
                            eachmonthduration[9][2]=oct_ogd;
                            break;
                        case Calendar.NOVEMBER:
                            eachmonthduration[10][0]=10;
                            total +=1;
                            outgoing +=1;
                            nov_og+=1;
                            eachmonthduration[10][1]=nov_og;
                            outgoingduration += Duration;
                            nov_ogd += Duration;
                            eachmonthduration[10][2]=nov_ogd;
                            break;
                        case Calendar.DECEMBER:
                            eachmonthduration[11][0]=11;
                            total +=1;
                            outgoing +=1;
                            dec_og+=1;
                            eachmonthduration[11][1]=dec_og;
                            outgoingduration += Duration;
                            dec_ogd += Duration;
                            eachmonthduration[11][2]=dec_ogd;
                            break;
                    }

                    break;
                case CallLog.Calls.INCOMING_TYPE:

                    total +=1;
                    incoming +=1;
                    incomingduration += Duration;
                    break;
                case CallLog.Calls.MISSED_TYPE:

                    total +=1;
                    missed +=1;
                    break;
            }

        }
        managedCursor.close();

        return eachmonthduration;
    }

    private void setMData(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 1; i<getMCallDetails().length;i++){
            barEntries.add(new BarEntry(i,new float[]{getMCallDetails()[i][1],getMCallDetails()[i][2]}));
        }
        /*barEntries.add(new BarEntry(0,new float[]{getCallDetails()[0][1],getCallDetails()[0][2]}));
        barEntries.add(new BarEntry(1,new float[]{getCallDetails()[1][1],getCallDetails()[1][2]}));
        barEntries.add(new BarEntry(2,new float[]{getCallDetails()[2][1],getCallDetails()[2][2]}));
        barEntries.add(new BarEntry(3,new float[]{getCallDetails()[3][1],getCallDetails()[3][2]}));
        barEntries.add(new BarEntry(4,new float[]{getCallDetails()[4][1],getCallDetails()[4][2]}));
        barEntries.add(new BarEntry(5,new float[]{getCallDetails()[5][1],getCallDetails()[5][2]}));
        barEntries.add(new BarEntry(6,new float[]{getCallDetails()[6][1],getCallDetails()[6][2]}));
        barEntries.add(new BarEntry(7,new float[]{getCallDetails()[7][1],getCallDetails()[7][2]}));
        barEntries.add(new BarEntry(8,new float[]{getCallDetails()[8][1],getCallDetails()[8][2]}));
        barEntries.add(new BarEntry(9,new float[]{getCallDetails()[9][1],getCallDetails()[9][2]}));
        barEntries.add(new BarEntry(10,new float[]{getCallDetails()[10][1],getCallDetails()[10][2]}));
        barEntries.add(new BarEntry(11,new float[]{getCallDetails()[11][1],getCallDetails()[11][2]}));*/
        //(x座標,y座標)

        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setStackLabels(new String[]{"撥出(通)","通話時間(秒)"});
        barDataSet.setColors(new int[]{Color.rgb(247, 171,72), Color.rgb(23, 197, 255)});
        barDataSet.setValueTextSize(10f);


        BarData theData = new BarData(barDataSet);
        theData.setBarWidth(0.9f);

        barChart.setData(theData);
        barChart.setFitBars(true);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setVisibleXRangeMaximum(10);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
    }


    private int[][] getMCallDetails(){

        int yearpick = numPicker.getValue();
        int monthpick = Mpicker.getValue();

        String strOrder = CallLog.Calls.DATE + " DESC"; //設定資料呈現的時間順序(此為新到舊),若為null則會從舊到新
        Uri callUri = Uri.parse("content://call_log/calls"); //CallLog.Calls.CONTENT_URI 透過系統URI權限獲取CallLog的內容


        //calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));//抓取當月第一天
        calendar.set(yearpick,monthpick-1,calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,0);//設定為當日 00:00
        String fromDate = String.valueOf(calendar.getTimeInMillis()); //設定字串:開始日期,getTimeInMillis()轉換為毫秒數
        //calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));//抓取當月最後一天
        calendar.set(yearpick,monthpick-1,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,24);//設定為當日 24:00
        String toDate = String.valueOf(calendar.getTimeInMillis());//設定字串:截止日期
        String[] whereValue = {fromDate,toDate};//設定字串陣列,以提供給資料庫

        //獲取資料庫內容
        Cursor managedCursor = getActivity().getContentResolver().query(callUri,null,CallLog.Calls.DATE+" BETWEEN ? AND ?",whereValue,strOrder);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        int total = 0;
        int outgoing =0;
        int day1_og = 0;
        int day2_og = 0;
        int day3_og = 0;
        int day4_og = 0;
        int day5_og = 0;
        int day6_og = 0;
        int day7_og = 0;
        int day8_og = 0;
        int day9_og = 0;
        int day10_og = 0;
        int day11_og = 0;
        int day12_og = 0;
        int day13_og = 0;
        int day14_og = 0;
        int day15_og = 0;
        int day16_og = 0;
        int day17_og = 0;
        int day18_og = 0;
        int day19_og = 0;
        int day20_og = 0;
        int day21_og = 0;
        int day22_og = 0;
        int day23_og = 0;
        int day24_og = 0;
        int day25_og = 0;
        int day26_og = 0;
        int day27_og = 0;
        int day28_og = 0;
        int day29_og = 0;
        int day30_og = 0;
        int day31_og = 0;

        int incoming = 0;
        int missed = 0;
        int month = calendar.get(Calendar.MONTH);
        /*用if判斷大月小月*/
        int[][] eachdayofMonthduration;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            eachdayofMonthduration = new int[31][3];
        }
        else if (month == 2){
            eachdayofMonthduration = new int[29][3];
        }
        else {
            eachdayofMonthduration = new int[32][3];
        }

        int outgoingduration = 0;
        int day1_ogd = 0;
        int day2_ogd = 0;
        int day3_ogd = 0;
        int day4_ogd = 0;
        int day5_ogd = 0;
        int day6_ogd = 0;
        int day7_ogd = 0;
        int day8_ogd = 0;
        int day9_ogd = 0;
        int day10_ogd = 0;
        int day11_ogd = 0;
        int day12_ogd = 0;
        int day13_ogd = 0;
        int day14_ogd = 0;
        int day15_ogd = 0;
        int day16_ogd = 0;
        int day17_ogd = 0;
        int day18_ogd = 0;
        int day19_ogd = 0;
        int day20_ogd = 0;
        int day21_ogd = 0;
        int day22_ogd = 0;
        int day23_ogd = 0;
        int day24_ogd = 0;
        int day25_ogd = 0;
        int day26_ogd = 0;
        int day27_ogd = 0;
        int day28_ogd = 0;
        int day29_ogd = 0;
        int day30_ogd = 0;
        int day31_ogd= 0;
        int incomingduration = 0;

        while (managedCursor.moveToNext()){
            String phNumber = managedCursor.getString(number); //電話號碼轉成字串
            String callType = managedCursor.getString(type); //電話類型(撥號/接聽/未接)轉成字串
            String callDate = managedCursor.getString(date); //將日期轉成字串
            Date callDaytime = new Date(Long.valueOf(callDate));
            calendar.setTime(callDaytime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm"); //日期時間格式化
            String dateString = formatter.format(callDaytime); //時間
            String callDuration = managedCursor.getString(duration); //通話時間
            int Duration = managedCursor.getInt(duration);

            int dircode = Integer.parseInt(callType);
            int dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if (month == 4 || month == 6 || month == 9 || month == 11){
                switch (dircode){
                    case CallLog.Calls.OUTGOING_TYPE:
                        switch (dayofMonth){
                            case 1:
                                eachdayofMonthduration[1][0] = 1; //[x][0]放入當月第幾天 /*若無資料,則所有值皆回傳0*/
                                total +=1; //總通話數
                                outgoing +=1; //總撥出數
                                day1_og+=1; //當日撥出數
                                eachdayofMonthduration[1][1] =day1_og; //[x][1]放入撥出數
                                outgoingduration += Duration; //總撥出時間
                                day1_ogd += Duration; //當日撥出時間
                                eachdayofMonthduration[1][2] = day1_ogd; //[x][2]放入撥出時間
                                break;
                            case 2:
                                eachdayofMonthduration[2][0] = 2;
                                day2_og+=1;
                                eachdayofMonthduration[2][1] =day2_og;
                                day2_ogd += Duration;
                                eachdayofMonthduration[2][2] = day2_ogd;
                                break;
                            case 3:
                                eachdayofMonthduration[3][0] = 3;
                                day3_og+=1;
                                eachdayofMonthduration[3][1] =day3_og;
                                day3_ogd += Duration;
                                eachdayofMonthduration[3][2] = day3_ogd;
                                break;
                            case 4:
                                eachdayofMonthduration[4][0] = 4;
                                day4_og+=1;
                                eachdayofMonthduration[4][1] =day4_og;
                                day4_ogd += Duration;
                                eachdayofMonthduration[4][2] = day4_ogd;
                                break;
                            case 5:
                                eachdayofMonthduration[5][0] = 5;
                                day5_og+=1;
                                eachdayofMonthduration[5][1] =day5_og;
                                day5_ogd += Duration;
                                eachdayofMonthduration[5][2] = day5_ogd;
                                break;
                            case 6:
                                eachdayofMonthduration[6][0] = 6;
                                day6_og+=1;
                                eachdayofMonthduration[6][1] =day6_og;
                                day6_ogd += Duration;
                                eachdayofMonthduration[6][2] = day6_ogd;
                                break;
                            case 7:
                                eachdayofMonthduration[7][0] = 7;
                                day7_og+=1;
                                eachdayofMonthduration[7][1] =day7_og;
                                day7_ogd += Duration;
                                eachdayofMonthduration[7][2] = day7_ogd;
                                break;
                            case 8:
                                eachdayofMonthduration[8][0] = 8;
                                day8_og+=1;
                                eachdayofMonthduration[8][1] =day8_og;
                                day8_ogd += Duration;
                                eachdayofMonthduration[8][2] = day8_ogd;
                                break;
                            case 9:
                                eachdayofMonthduration[9][0] = 9;
                                day9_og+=1;
                                eachdayofMonthduration[9][1] =day9_og;
                                day9_ogd += Duration;
                                eachdayofMonthduration[9][2] = day9_ogd;
                                break;
                            case 10:
                                eachdayofMonthduration[10][0] = 10;
                                day10_og+=1;
                                eachdayofMonthduration[10][1] =day10_og;
                                day10_ogd += Duration;
                                eachdayofMonthduration[10][2] = day10_ogd;
                                break;
                            case 11:
                                eachdayofMonthduration[11][0] = 11;
                                day11_og+=1;
                                eachdayofMonthduration[11][1] =day11_og;
                                day11_ogd += Duration;
                                eachdayofMonthduration[11][2] = day11_ogd;
                                break;
                            case 12:
                                eachdayofMonthduration[12][0] = 12;
                                day12_og+=1;
                                eachdayofMonthduration[12][1] =day12_og;
                                day12_ogd += Duration;
                                eachdayofMonthduration[12][2] = day12_ogd;
                                break;
                            case 13:
                                eachdayofMonthduration[13][0] = 13;
                                day13_og+=1;
                                eachdayofMonthduration[13][1] =day13_og;
                                day13_ogd += Duration;
                                eachdayofMonthduration[13][2] = day13_ogd;
                                break;
                            case 14:
                                eachdayofMonthduration[14][0] = 14;
                                day14_og+=1;
                                eachdayofMonthduration[14][1] =day14_og;
                                day14_ogd += Duration;
                                eachdayofMonthduration[14][2] = day14_ogd;
                                break;
                            case 15:
                                eachdayofMonthduration[15][0] = 15;
                                day15_og+=1;
                                eachdayofMonthduration[15][1] =day15_og;
                                day15_ogd += Duration;
                                eachdayofMonthduration[15][2] = day15_ogd;
                                break;
                            case 16:
                                eachdayofMonthduration[16][0] = 16;
                                day16_og+=1;
                                eachdayofMonthduration[16][1] =day16_og;
                                day16_ogd += Duration;
                                eachdayofMonthduration[16][2] = day16_ogd;
                                break;
                            case 17:
                                eachdayofMonthduration[17][0] = 17;
                                day17_og+=1;
                                eachdayofMonthduration[17][1] =day17_og;
                                day17_ogd += Duration;
                                eachdayofMonthduration[17][2] = day17_ogd;
                                break;
                            case 18:
                                eachdayofMonthduration[18][0] = 18;
                                day18_og+=1;
                                eachdayofMonthduration[18][1] =day18_og;
                                day18_ogd += Duration;
                                eachdayofMonthduration[18][2] = day18_ogd;
                                break;
                            case 19:
                                eachdayofMonthduration[19][0] = 19;
                                day19_og+=1;
                                eachdayofMonthduration[19][1] =day19_og;
                                day19_ogd += Duration;
                                eachdayofMonthduration[19][2] = day19_ogd;
                                break;
                            case 20:
                                eachdayofMonthduration[20][0] = 20;
                                day20_og+=1;
                                eachdayofMonthduration[20][1] =day20_og;
                                day20_ogd += Duration;
                                eachdayofMonthduration[20][2] = day20_ogd;
                                break;
                            case 21:
                                eachdayofMonthduration[21][0] = 21;
                                day21_og+=1;
                                eachdayofMonthduration[21][1] =day21_og;
                                day21_ogd += Duration;
                                eachdayofMonthduration[21][2] = day21_ogd;
                                break;
                            case 22:
                                eachdayofMonthduration[22][0] = 22;
                                day22_og+=1;
                                eachdayofMonthduration[22][1] =day22_og;
                                day22_ogd += Duration;
                                eachdayofMonthduration[22][2] = day22_ogd;
                                break;
                            case 23:
                                eachdayofMonthduration[23][0] = 23;
                                day23_og+=1;
                                eachdayofMonthduration[23][1] =day23_og;
                                day23_ogd += Duration;
                                eachdayofMonthduration[23][2] = day23_ogd;
                                break;
                            case 24:
                                eachdayofMonthduration[24][0] = 24;
                                day24_og+=1;
                                eachdayofMonthduration[24][1] =day24_og;
                                day24_ogd += Duration;
                                eachdayofMonthduration[24][2] = day24_ogd;
                                break;
                            case 25:
                                eachdayofMonthduration[25][0] = 25;
                                day25_og+=1;
                                eachdayofMonthduration[25][1] =day25_og;
                                day25_ogd += Duration;
                                eachdayofMonthduration[25][2] = day25_ogd;
                                break;
                            case 26:
                                eachdayofMonthduration[26][0] = 26;
                                day26_og+=1;
                                eachdayofMonthduration[26][1] =day26_og;
                                day26_ogd += Duration;
                                eachdayofMonthduration[26][2] = day26_ogd;
                                break;
                            case 27:
                                eachdayofMonthduration[27][0] = 27;
                                day27_og+=1;
                                eachdayofMonthduration[27][1] =day27_og;
                                day27_ogd += Duration;
                                eachdayofMonthduration[27][2] = day27_ogd;
                                break;
                            case 28:
                                eachdayofMonthduration[28][0] = 28;
                                day28_og+=1;
                                eachdayofMonthduration[28][1] =day28_og;
                                day28_ogd += Duration;
                                eachdayofMonthduration[28][2] = day28_ogd;
                                break;
                            case 29:
                                eachdayofMonthduration[29][0] = 29;
                                day29_og+=1;
                                eachdayofMonthduration[29][1] =day29_og;
                                day29_ogd += Duration;
                                eachdayofMonthduration[29][2] = day29_ogd;
                                break;
                            case 30:
                                eachdayofMonthduration[30][0] = 30;
                                day30_og+=1;
                                eachdayofMonthduration[30][1] =day30_og;
                                day30_ogd += Duration;
                                eachdayofMonthduration[30][2] = day30_ogd;
                                break;
                        }
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        total +=1;
                        incoming +=1;
                        incomingduration += Duration;
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        total +=1;
                        missed +=1;
                        break;
                }
            }
            else if (month==2){
                if (dircode==CallLog.Calls.OUTGOING_TYPE){
                    switch (dayofMonth){
                        case 1:
                            eachdayofMonthduration[1][0] = 1; //[x][0]放入當月第幾天 /*若無資料,則所有值皆回傳0*/
                            total +=1; //總通話數
                            outgoing +=1; //總撥出數
                            day1_og+=1; //當日撥出數
                            eachdayofMonthduration[1][1] =day1_og; //[x][1]放入撥出數
                            outgoingduration += Duration; //總撥出時間
                            day1_ogd += Duration; //當日撥出時間
                            eachdayofMonthduration[1][2] = day1_ogd; //[x][2]放入撥出時間
                            break;
                        case 2:
                            eachdayofMonthduration[2][0] = 2;
                            day2_og+=1;
                            eachdayofMonthduration[2][1] =day2_og;
                            day2_ogd += Duration;
                            eachdayofMonthduration[2][2] = day2_ogd;
                            break;
                        case 3:
                            eachdayofMonthduration[3][0] = 3;
                            day3_og+=1;
                            eachdayofMonthduration[3][1] =day3_og;
                            day3_ogd += Duration;
                            eachdayofMonthduration[3][2] = day3_ogd;
                            break;
                        case 4:
                            eachdayofMonthduration[4][0] = 4;
                            day4_og+=1;
                            eachdayofMonthduration[4][1] =day4_og;
                            day4_ogd += Duration;
                            eachdayofMonthduration[4][2] = day4_ogd;
                            break;
                        case 5:
                            eachdayofMonthduration[5][0] = 5;
                            day5_og+=1;
                            eachdayofMonthduration[5][1] =day5_og;
                            day5_ogd += Duration;
                            eachdayofMonthduration[5][2] = day5_ogd;
                            break;
                        case 6:
                            eachdayofMonthduration[6][0] = 6;
                            day6_og+=1;
                            eachdayofMonthduration[6][1] =day6_og;
                            day6_ogd += Duration;
                            eachdayofMonthduration[6][2] = day6_ogd;
                            break;
                        case 7:
                            eachdayofMonthduration[7][0] = 7;
                            day7_og+=1;
                            eachdayofMonthduration[7][1] =day7_og;
                            day7_ogd += Duration;
                            eachdayofMonthduration[7][2] = day7_ogd;
                            break;
                        case 8:
                            eachdayofMonthduration[8][0] = 8;
                            day8_og+=1;
                            eachdayofMonthduration[8][1] =day8_og;
                            day8_ogd += Duration;
                            eachdayofMonthduration[8][2] = day8_ogd;
                            break;
                        case 9:
                            eachdayofMonthduration[9][0] = 9;
                            day9_og+=1;
                            eachdayofMonthduration[9][1] =day9_og;
                            day9_ogd += Duration;
                            eachdayofMonthduration[9][2] = day9_ogd;
                            break;
                        case 10:
                            eachdayofMonthduration[10][0] = 10;
                            day10_og+=1;
                            eachdayofMonthduration[10][1] =day10_og;
                            day10_ogd += Duration;
                            eachdayofMonthduration[10][2] = day10_ogd;
                            break;
                        case 11:
                            eachdayofMonthduration[11][0] = 11;
                            day11_og+=1;
                            eachdayofMonthduration[11][1] =day11_og;
                            day11_ogd += Duration;
                            eachdayofMonthduration[11][2] = day11_ogd;
                            break;
                        case 12:
                            eachdayofMonthduration[12][0] = 12;
                            day12_og+=1;
                            eachdayofMonthduration[12][1] =day12_og;
                            day12_ogd += Duration;
                            eachdayofMonthduration[12][2] = day12_ogd;
                            break;
                        case 13:
                            eachdayofMonthduration[13][0] = 13;
                            day13_og+=1;
                            eachdayofMonthduration[13][1] =day13_og;
                            day13_ogd += Duration;
                            eachdayofMonthduration[13][2] = day13_ogd;
                            break;
                        case 14:
                            eachdayofMonthduration[14][0] = 14;
                            day14_og+=1;
                            eachdayofMonthduration[14][1] =day14_og;
                            day14_ogd += Duration;
                            eachdayofMonthduration[14][2] = day14_ogd;
                            break;
                        case 15:
                            eachdayofMonthduration[15][0] = 15;
                            day15_og+=1;
                            eachdayofMonthduration[15][1] =day15_og;
                            day15_ogd += Duration;
                            eachdayofMonthduration[15][2] = day15_ogd;
                            break;
                        case 16:
                            eachdayofMonthduration[16][0] = 16;
                            day16_og+=1;
                            eachdayofMonthduration[16][1] =day16_og;
                            day16_ogd += Duration;
                            eachdayofMonthduration[16][2] = day16_ogd;
                            break;
                        case 17:
                            eachdayofMonthduration[17][0] = 17;
                            day17_og+=1;
                            eachdayofMonthduration[17][1] =day17_og;
                            day17_ogd += Duration;
                            eachdayofMonthduration[17][2] = day17_ogd;
                            break;
                        case 18:
                            eachdayofMonthduration[18][0] = 18;
                            day18_og+=1;
                            eachdayofMonthduration[18][1] =day18_og;
                            day18_ogd += Duration;
                            eachdayofMonthduration[18][2] = day18_ogd;
                            break;
                        case 19:
                            eachdayofMonthduration[19][0] = 19;
                            day19_og+=1;
                            eachdayofMonthduration[19][1] =day19_og;
                            day19_ogd += Duration;
                            eachdayofMonthduration[19][2] = day19_ogd;
                            break;
                        case 20:
                            eachdayofMonthduration[20][0] = 20;
                            day20_og+=1;
                            eachdayofMonthduration[20][1] =day20_og;
                            day20_ogd += Duration;
                            eachdayofMonthduration[20][2] = day20_ogd;
                            break;
                        case 21:
                            eachdayofMonthduration[21][0] = 21;
                            day21_og+=1;
                            eachdayofMonthduration[21][1] =day21_og;
                            day21_ogd += Duration;
                            eachdayofMonthduration[21][2] = day21_ogd;
                            break;
                        case 22:
                            eachdayofMonthduration[22][0] = 22;
                            day22_og+=1;
                            eachdayofMonthduration[22][1] =day22_og;
                            day22_ogd += Duration;
                            eachdayofMonthduration[22][2] = day22_ogd;
                            break;
                        case 23:
                            eachdayofMonthduration[23][0] = 23;
                            day23_og+=1;
                            eachdayofMonthduration[23][1] =day23_og;
                            day23_ogd += Duration;
                            eachdayofMonthduration[23][2] = day23_ogd;
                            break;
                        case 24:
                            eachdayofMonthduration[24][0] = 24;
                            day24_og+=1;
                            eachdayofMonthduration[24][1] =day24_og;
                            day24_ogd += Duration;
                            eachdayofMonthduration[24][2] = day24_ogd;
                            break;
                        case 25:
                            eachdayofMonthduration[25][0] = 25;
                            day25_og+=1;
                            eachdayofMonthduration[25][1] =day25_og;
                            day25_ogd += Duration;
                            eachdayofMonthduration[25][2] = day25_ogd;
                            break;
                        case 26:
                            eachdayofMonthduration[26][0] = 26;
                            day26_og+=1;
                            eachdayofMonthduration[26][1] =day26_og;
                            day26_ogd += Duration;
                            eachdayofMonthduration[26][2] = day26_ogd;
                            break;
                        case 27:
                            eachdayofMonthduration[27][0] = 27;
                            day27_og+=1;
                            eachdayofMonthduration[27][1] =day27_og;
                            day27_ogd += Duration;
                            eachdayofMonthduration[27][2] = day27_ogd;
                            break;
                        case 28:
                            eachdayofMonthduration[28][0] = 28;
                            day28_og+=1;
                            eachdayofMonthduration[28][1] =day28_og;
                            day28_ogd += Duration;
                            eachdayofMonthduration[28][2] = day28_ogd;
                            break;
                    }
                }
            }
            else {
                if (dircode==CallLog.Calls.OUTGOING_TYPE){
                    switch (dayofMonth){
                        case 1:
                            eachdayofMonthduration[1][0] = 1; //[x][0]放入當月第幾天 /*若無資料,則所有值皆回傳0*/
                            total +=1; //總通話數
                            outgoing +=1; //總撥出數
                            day1_og+=1; //當日撥出數
                            eachdayofMonthduration[1][1] =day1_og; //[x][1]放入撥出數
                            outgoingduration += Duration; //總撥出時間
                            day1_ogd += Duration; //當日撥出時間
                            eachdayofMonthduration[1][2] = day1_ogd; //[x][2]放入撥出時間
                            break;
                        case 2:
                            eachdayofMonthduration[2][0] = 2;
                            day2_og+=1;
                            eachdayofMonthduration[2][1] =day2_og;
                            day2_ogd += Duration;
                            eachdayofMonthduration[2][2] = day2_ogd;
                            break;
                        case 3:
                            eachdayofMonthduration[3][0] = 3;
                            day3_og+=1;
                            eachdayofMonthduration[3][1] =day3_og;
                            day3_ogd += Duration;
                            eachdayofMonthduration[3][2] = day3_ogd;
                            break;
                        case 4:
                            eachdayofMonthduration[4][0] = 4;
                            day4_og+=1;
                            eachdayofMonthduration[4][1] =day4_og;
                            day4_ogd += Duration;
                            eachdayofMonthduration[4][2] = day4_ogd;
                            break;
                        case 5:
                            eachdayofMonthduration[5][0] = 5;
                            day5_og+=1;
                            eachdayofMonthduration[5][1] =day5_og;
                            day5_ogd += Duration;
                            eachdayofMonthduration[5][2] = day5_ogd;
                            break;
                        case 6:
                            eachdayofMonthduration[6][0] = 6;
                            day6_og+=1;
                            eachdayofMonthduration[6][1] =day6_og;
                            day6_ogd += Duration;
                            eachdayofMonthduration[6][2] = day6_ogd;
                            break;
                        case 7:
                            eachdayofMonthduration[7][0] = 7;
                            day7_og+=1;
                            eachdayofMonthduration[7][1] =day7_og;
                            day7_ogd += Duration;
                            eachdayofMonthduration[7][2] = day7_ogd;
                            break;
                        case 8:
                            eachdayofMonthduration[8][0] = 8;
                            day8_og+=1;
                            eachdayofMonthduration[8][1] =day8_og;
                            day8_ogd += Duration;
                            eachdayofMonthduration[8][2] = day8_ogd;
                            break;
                        case 9:
                            eachdayofMonthduration[9][0] = 9;
                            day9_og+=1;
                            eachdayofMonthduration[9][1] =day9_og;
                            day9_ogd += Duration;
                            eachdayofMonthduration[9][2] = day9_ogd;
                            break;
                        case 10:
                            eachdayofMonthduration[10][0] = 10;
                            day10_og+=1;
                            eachdayofMonthduration[10][1] =day10_og;
                            day10_ogd += Duration;
                            eachdayofMonthduration[10][2] = day10_ogd;
                            break;
                        case 11:
                            eachdayofMonthduration[11][0] = 11;
                            day11_og+=1;
                            eachdayofMonthduration[11][1] =day11_og;
                            day11_ogd += Duration;
                            eachdayofMonthduration[11][2] = day11_ogd;
                            break;
                        case 12:
                            eachdayofMonthduration[12][0] = 12;
                            day12_og+=1;
                            eachdayofMonthduration[12][1] =day12_og;
                            day12_ogd += Duration;
                            eachdayofMonthduration[12][2] = day12_ogd;
                            break;
                        case 13:
                            eachdayofMonthduration[13][0] = 13;
                            day13_og+=1;
                            eachdayofMonthduration[13][1] =day13_og;
                            day13_ogd += Duration;
                            eachdayofMonthduration[13][2] = day13_ogd;
                            break;
                        case 14:
                            eachdayofMonthduration[14][0] = 14;
                            day14_og+=1;
                            eachdayofMonthduration[14][1] =day14_og;
                            day14_ogd += Duration;
                            eachdayofMonthduration[14][2] = day14_ogd;
                            break;
                        case 15:
                            eachdayofMonthduration[15][0] = 15;
                            day15_og+=1;
                            eachdayofMonthduration[15][1] =day15_og;
                            day15_ogd += Duration;
                            eachdayofMonthduration[15][2] = day15_ogd;
                            break;
                        case 16:
                            eachdayofMonthduration[16][0] = 16;
                            day16_og+=1;
                            eachdayofMonthduration[16][1] =day16_og;
                            day16_ogd += Duration;
                            eachdayofMonthduration[16][2] = day16_ogd;
                            break;
                        case 17:
                            eachdayofMonthduration[17][0] = 17;
                            day17_og+=1;
                            eachdayofMonthduration[17][1] =day17_og;
                            day17_ogd += Duration;
                            eachdayofMonthduration[17][2] = day17_ogd;
                            break;
                        case 18:
                            eachdayofMonthduration[18][0] = 18;
                            day18_og+=1;
                            eachdayofMonthduration[18][1] =day18_og;
                            day18_ogd += Duration;
                            eachdayofMonthduration[18][2] = day18_ogd;
                            break;
                        case 19:
                            eachdayofMonthduration[19][0] = 19;
                            day19_og+=1;
                            eachdayofMonthduration[19][1] =day19_og;
                            day19_ogd += Duration;
                            eachdayofMonthduration[19][2] = day19_ogd;
                            break;
                        case 20:
                            eachdayofMonthduration[20][0] = 20;
                            day20_og+=1;
                            eachdayofMonthduration[20][1] =day20_og;
                            day20_ogd += Duration;
                            eachdayofMonthduration[20][2] = day20_ogd;
                            break;
                        case 21:
                            eachdayofMonthduration[21][0] = 21;
                            day21_og+=1;
                            eachdayofMonthduration[21][1] =day21_og;
                            day21_ogd += Duration;
                            eachdayofMonthduration[21][2] = day21_ogd;
                            break;
                        case 22:
                            eachdayofMonthduration[22][0] = 22;
                            day22_og+=1;
                            eachdayofMonthduration[22][1] =day22_og;
                            day22_ogd += Duration;
                            eachdayofMonthduration[22][2] = day22_ogd;
                            break;
                        case 23:
                            eachdayofMonthduration[23][0] = 23;
                            day23_og+=1;
                            eachdayofMonthduration[23][1] =day23_og;
                            day23_ogd += Duration;
                            eachdayofMonthduration[23][2] = day23_ogd;
                            break;
                        case 24:
                            eachdayofMonthduration[24][0] = 24;
                            day24_og+=1;
                            eachdayofMonthduration[24][1] =day24_og;
                            day24_ogd += Duration;
                            eachdayofMonthduration[24][2] = day24_ogd;
                            break;
                        case 25:
                            eachdayofMonthduration[25][0] = 25;
                            day25_og+=1;
                            eachdayofMonthduration[25][1] =day25_og;
                            day25_ogd += Duration;
                            eachdayofMonthduration[25][2] = day25_ogd;
                            break;
                        case 26:
                            eachdayofMonthduration[26][0] = 26;
                            day26_og+=1;
                            eachdayofMonthduration[26][1] =day26_og;
                            day26_ogd += Duration;
                            eachdayofMonthduration[26][2] = day26_ogd;
                            break;
                        case 27:
                            eachdayofMonthduration[27][0] = 27;
                            day27_og+=1;
                            eachdayofMonthduration[27][1] =day27_og;
                            day27_ogd += Duration;
                            eachdayofMonthduration[27][2] = day27_ogd;
                            break;
                        case 28:
                            eachdayofMonthduration[28][0] = 28;
                            day28_og+=1;
                            eachdayofMonthduration[28][1] =day28_og;
                            day28_ogd += Duration;
                            eachdayofMonthduration[28][2] = day28_ogd;
                            break;
                        case 29:
                            eachdayofMonthduration[29][0] = 29;
                            day29_og+=1;
                            eachdayofMonthduration[29][1] =day29_og;
                            day29_ogd += Duration;
                            eachdayofMonthduration[29][2] = day29_ogd;
                            break;
                        case 30:
                            eachdayofMonthduration[30][0] = 30;
                            day30_og+=1;
                            eachdayofMonthduration[30][1] =day30_og;
                            day30_ogd += Duration;
                            eachdayofMonthduration[30][2] = day30_ogd;
                            break;
                        case 31:
                            eachdayofMonthduration[31][0] = 31;
                            day31_og+=1;
                            eachdayofMonthduration[31][1] =day31_og;
                            day31_ogd += Duration;
                            eachdayofMonthduration[31][2] = day31_ogd;
                            break;
                    }
                }
            }
        }
        managedCursor.close();

        return eachdayofMonthduration;
    }
}
