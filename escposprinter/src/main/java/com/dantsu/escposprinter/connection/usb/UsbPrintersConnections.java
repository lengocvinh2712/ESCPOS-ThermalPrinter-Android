package com.dantsu.escposprinter.connection.usb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

public class UsbPrintersConnections extends UsbConnections {

    /**
     * Create a new instance of UsbPrintersConnections
     *
     * @param context Application context
     */
    public UsbPrintersConnections(Context context) {
        super(context);
    }

    /**
     * Easy way to get the first USB printer paired / connected.
     *
     * @return a UsbConnection instance
     */
    @Nullable
    public static UsbConnection selectFirstConnected(Context context) {
        UsbPrintersConnections printers = new UsbPrintersConnections(context);
        UsbConnection[] usbPrinters = printers.getList();
        
        if (usbPrinters == null || usbPrinters.length == 0) {
            return null;
        }

        return usbPrinters[0];
    }

    @Nullable
    public static UsbConnection selectDeviceConnected(Context context, String deviceStr) {
        UsbPrintersConnections printers = new UsbPrintersConnections(context);
        UsbConnection[] usbPrinters = printers.getList();

        if (usbPrinters == null || usbPrinters.length == 0) {
            return null;
        }

        Gson gson = new Gson();
        UsbPrinterDevice device = gson.fromJson(deviceStr, UsbPrinterDevice.class);
        for (int idx = 0; idx < usbPrinters.length; idx++) {
            UsbDevice deviceSelect = usbPrinters[idx].getDevice();
            if (deviceSelect.getProductId() == device.getProduct_id() && deviceSelect.getVendorId() == device.getVendor_id()) {
                return usbPrinter[idx];
                break;
            }
        }

        return usbPrinters[0];
    }

    @Nullable
    public static UsbConnection[] selectAllConnected(Context context) {
        UsbPrintersConnections printers = new UsbPrintersConnections(context);
        UsbConnection[] usbPrinters = printers.getList();

        if (usbPrinters == null || usbPrinters.length == 0) {
            return null;
        }

        return usbPrinters;
    }
    
    
    /**
     * Get a list of USB printers.
     *
     * @return an array of UsbConnection
     */
    @Nullable
    public UsbConnection[] getList() {
        UsbConnection[] usbConnections = super.getList();

        if(usbConnections == null) {
            return null;
        }

        int i = 0;
        UsbConnection[] printersTmp = new UsbConnection[usbConnections.length];
        for (UsbConnection usbConnection : usbConnections) {
            UsbDevice device = usbConnection.getDevice();
            int usbClass = device.getDeviceClass();
            if((usbClass == UsbConstants.USB_CLASS_PER_INTERFACE || usbClass == UsbConstants.USB_CLASS_MISC ) && UsbDeviceHelper.findPrinterInterface(device) != null) {
                usbClass = UsbConstants.USB_CLASS_PRINTER;
            }
            if (usbClass == UsbConstants.USB_CLASS_PRINTER) {
                printersTmp[i++] = new UsbConnection(this.usbManager, device);
            }
        }

        UsbConnection[] usbPrinters = new UsbConnection[i];
        System.arraycopy(printersTmp, 0, usbPrinters, 0, i);
        return usbPrinters;
    }
    
}
