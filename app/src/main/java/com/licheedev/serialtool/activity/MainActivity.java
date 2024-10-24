package com.licheedev.serialtool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.serialport.SerialPortFinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.OnClick;
import com.licheedev.serialtool.R;
import com.licheedev.serialtool.activity.base.BaseActivity;
import com.licheedev.serialtool.comn.Device;
import com.licheedev.serialtool.comn.SerialPortManager;
import com.licheedev.serialtool.util.AllCapTransformationMethod;
import com.licheedev.serialtool.util.PrefHelper;
import com.licheedev.serialtool.util.ToastUtil;
import com.licheedev.serialtool.util.constant.PreferenceKeys;

import static com.licheedev.serialtool.R.array.baudrates;

public class MainActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.spinner_devices)
    Spinner mSpinnerDevices;
    @BindView(R.id.spinner_baudrate)
    Spinner mSpinnerBaudrate;
    @BindView(R.id.btn_open_device)
    Button mBtnOpenDevice;
    @BindView(R.id.btn_send_data)
    Button mBtnSendData;
    @BindView(R.id.btn_load_list)
    Button mBtnLoadList;
    @BindView(R.id.et_data)
    EditText mEtData;

    private Device mDevice;

    private int mDeviceIndex;
    private int mBaudrateIndex;

    private String[] mDevices;
    private String[] mBaudrates;

    private boolean mOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEtData.setTransformationMethod(new AllCapTransformationMethod(true));

        initDevice();
        initSpinners();
        updateViewState(mOpened);
    }

    @Override
    protected void onDestroy() {
        SerialPortManager.instance().close();
        super.onDestroy();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 初始化设备列表
     */
    private void initDevice() {

        SerialPortFinder serialPortFinder = new SerialPortFinder();

        // 设备
        mDevices = serialPortFinder.getAllDevicesPath();
        if (mDevices.length == 0) {
            mDevices = new String[] {
                getString(R.string.no_serial_device)
            };
        }
        // 波特率
        mBaudrates = getResources().getStringArray(baudrates);

        mDeviceIndex = PrefHelper.getDefault().getInt(PreferenceKeys.SERIAL_PORT_DEVICES, 0);
        mDeviceIndex = mDeviceIndex >= mDevices.length ? mDevices.length - 1 : mDeviceIndex;
        mBaudrateIndex = PrefHelper.getDefault().getInt(PreferenceKeys.BAUD_RATE, 0);

        mDevice = new Device(mDevices[mDeviceIndex], mBaudrates[mBaudrateIndex]);
    }

    /**
     * 初始化下拉选项
     */
    private void initSpinners() {

        ArrayAdapter<String> deviceAdapter =
            new ArrayAdapter<String>(this, R.layout.spinner_default_item, mDevices);
        deviceAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerDevices.setAdapter(deviceAdapter);
        mSpinnerDevices.setOnItemSelectedListener(this);

        ArrayAdapter<String> baudrateAdapter =
            new ArrayAdapter<String>(this, R.layout.spinner_default_item, mBaudrates);
        baudrateAdapter.setDropDownViewResource(R.layout.spinner_item);
        mSpinnerBaudrate.setAdapter(baudrateAdapter);
        mSpinnerBaudrate.setOnItemSelectedListener(this);

        mSpinnerDevices.setSelection(mDeviceIndex);
        mSpinnerBaudrate.setSelection(mBaudrateIndex);
    }

    @OnClick({ R.id.btn_open_device, R.id.btn_send_data, R.id.btn_load_list })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_open_device:
                switchSerialPort();
                break;
            case R.id.btn_send_data:
                sendData();
                break;
            case R.id.btn_load_list:
                startActivity(new Intent(this, LoadCmdListActivity.class));
                break;
        }
    }

    private void sendData() {

        String text = mEtData.getText().toString().trim();
        if (TextUtils.isEmpty(text) || text.length() % 2 != 0) {
            ToastUtil.showOne(this, "无效数据");
            return;
        }

        SerialPortManager.instance().sendCommand(text);
    }

    /**
     * 打开或关闭串口
     */
    private void switchSerialPort() {
        if (mOpened) {
            SerialPortManager.instance().close();
            mOpened = false;
        } else {

            // 保存配置
            PrefHelper.getDefault().saveInt(PreferenceKeys.SERIAL_PORT_DEVICES, mDeviceIndex);
            PrefHelper.getDefault().saveInt(PreferenceKeys.BAUD_RATE, mBaudrateIndex);

            mOpened = SerialPortManager.instance().open(mDevice) != null;
            if (mOpened) {
                ToastUtil.showOne(this, "成功打开串口");
            } else {
                ToastUtil.showOne(this, "打开串口失败");
            }
        }
        updateViewState(mOpened);
    }

    /**
     * 更新视图状态
     *
     * @param isSerialPortOpened
     */
    /**
     * 更新界面状态以反映串口的开闭状态
     *
     * @param isSerialPortOpen 表示串口是否已打开的布尔值
     */
    private void updateUIState(boolean isSerialPortOpen) {
        // 根据串口状态设置按钮文本
        int buttonLabelResourceId = isSerialPortOpen ? R.string.close_serial_port : R.string.open_serial_port;
        mBtnOpenDevice.setText(buttonLabelResourceId);

        // 根据串口状态启用或禁用控件
        mSpinnerDevices.setEnabled(!isSerialPortOpen);
        mSpinnerBaudrate.setEnabled(!isSerialPortOpen);
        mBtnSendData.setEnabled(isSerialPortOpen);
        mBtnLoadList.setEnabled(isSerialPortOpen);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // 处理下拉列表项的选择事件
        switch (parent.getId()) {
            case R.id.spinner_devices:
                mCurrentDeviceIndex = position;
                mCurrentDevice.setPath(mDevicePaths[mCurrentDeviceIndex]);
                break;
            case R.id.spinner_baudrate:
                mCurrentBaudrateIndex = position;
                mCurrentDevice.setBaudrate(mBaudrateOptions[mCurrentBaudrateIndex]);
                break;
            default:
                // 如果有其他 Spinner，可以在这里处理
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // 当没有任何选项被选中时，这里不做任何操作
        // 通常情况下，此方法可以保持为空实现
    }
