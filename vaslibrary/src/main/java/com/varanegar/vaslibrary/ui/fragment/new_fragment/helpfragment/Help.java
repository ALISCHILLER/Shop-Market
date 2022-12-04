package com.varanegar.vaslibrary.ui.fragment.new_fragment.helpfragment;

import java.util.ArrayList;
import java.util.List;

public class Help {
    public String name;
    public String url;

    public Help(){

    }
    public Help(String name, String url) {
        this.name = name;
        this.url = url;
    }


    public List<Help> getItems(int item){
        List<Help> helps=new ArrayList<>();
        switch (item) {
            case 0:
                Help b00=new Help("آموزش لاگین و دریافت تنظیمات",
                        "http://5.160.125.98:8080/content/videos/Login_Setting.mp4");
                Help b01=new Help("آموزش صفحه گرفتن تور",
                        "http://5.160.125.98:8080/content/videos/GetToure.mp4");
                Help b02=new Help("آموزش صفحه لیست مشتریان",
                        "http://5.160.125.98:8080/content/videos/List_customer.mp4");
                Help b03=new Help("آموزش صفحه مشتریان ",
                        "http://5.160.125.98:8080/content/videos/Customer_camera.mp4");
                Help b04=new Help("آموزش ثبت سفارش و ثبت برگشتی",
                        "http://5.160.125.98:8080/content/videos/Order registration and return registration.mp4");
                Help b05=new Help("آموزش پاک کردن داده کلیر کش",
                        "http://5.160.125.98:8080/content/videos/clear-data.mp4");
                Help b06=new Help("آموزش ویرایش مشتری",
                        "http://5.160.125.98:8080/content/videos/Edit_Customr_image_nationalCode.mp4");
                helps.add(b00);
                helps.add(b01);
                helps.add(b02);
                helps.add(b03);
                helps.add(b04);
                helps.add(b05);
                helps.add(b06);
                break;
        }

        return helps;
    }


}
