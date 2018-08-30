package com.example.administrator.temperture;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private LineChart chart;
    private LineData lineData;
    private EditText day;
    private EditText count;
    private String path;
    private ArrayList<String> days = null;
    private List<Entry> entries = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //定义记录文件
        path = Environment.getExternalStorageDirectory().getPath() + "/temperture.txt";//Environment.getExternalStorageDirectory().getAbsolutePath();
        chart = (LineChart) findViewById(R.id.chart);
        day = (EditText) findViewById(R.id.editText);
        count = (EditText) findViewById(R.id.editText2);
        day.setText(getNow("yy/M/d"));
        count.setText("36.5");
        //调用加载
        testChart();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * 提示框
     *
     * @param s
     */
    public void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }

    /**
     * 添加记录按钮.
     *
     * @param e
     */
    public void button1_click(View e) {
        //新记录写入文件
        writeSDFile(day.getText() + "," + count.getText() + "|", path);
        //重新载入图表
        testChart();
        displayToast("添加完成!");
    }

    /**
     * 追加内容到文件
     *
     * @param str
     * @param fileName
     */
    public void writeSDFile(String str, String fileName) {
        try {
            File f = new File(fileName);
            FileOutputStream os = new FileOutputStream(f, true);
            os.write(str.getBytes());
        } catch (Exception e) {
        }
    }//end of write

    /**
     * 读取系统目录下的文件..
     *
     * @param filename
     * @return
     */
    private String readFile(String filename) {
        String str = null;
        StringBuilder ret = new StringBuilder(1024);

        File file = new File(filename);
        InputStream in = null;

        try {

            in = new FileInputStream(file);
            int len = 0;
            byte[] buff = new byte[1024];
            while ((len = in.read(buff)) != -1) {
                str = new String(buff);
                ret.append(str);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret.toString();
    }

    /**
     * 取得当前的日期
     *
     * @param pattern 指定日期的格式
     * @return
     */
    private String getNow(String pattern) {
        String now;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date date = new Date(System.currentTimeMillis());
        now = formatter.format(date);
        return now;
    }

    private void testChart() {
        //读取记录文件
        String data = readFile(path);
        //分割出日期和数据
        String[] dayAndTemp = data.split("\\|");
        days = new ArrayList<>();
        //图片列头起始数为1.所以填充0的数据.
        days.add("start");
        //图表数据
        entries = new ArrayList<Entry>();
        //循环读出每天的数据:18/8/5,36.6
        for (int i = 0; i < dayAndTemp.length - 1; i++) {
            //单独分割出日期和数据
            String[] temp = dayAndTemp[i].split(",");
            //日期加入list
            days.add(temp[0]);
            // turn your data into Entry objects,把数据加入到图片entry
            entries.add(new Entry(i+1 , Float.valueOf(temp[1])));
            //i++;
        }
        // add entries to dataset
        LineDataSet dataSet = new LineDataSet(entries, "体温记录.");
        dataSet.setColor(R.color.colorAccent);
        dataSet.setValueTextColor(R.color.colorAccent); // styling, ...

        //add dataset to lindata
        LineData lineData = new LineData(dataSet);
        //addlinedata to chart
        chart.setData(lineData);
        //添加描述
        Description ds = new Description();
        ds.setText("记录每一天的体温,监测排卵.");
        chart.setDescription(ds);

        //把list的内容添加到列头
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            public String getFormattedValue(float value, AxisBase axis) {
                if (value < days.size()) {
                    return days.get((int) value);
                }
                return "worng";
            }
        });
         // refresh
        chart.invalidate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
