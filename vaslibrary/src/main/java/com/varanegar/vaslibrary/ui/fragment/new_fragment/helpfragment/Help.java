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
                Help b00=new Help("آموزش کامل اپلیکیشن ویزیت (مخصوص ویزیتورها)",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help b01=new Help("آموزش پاک کردن داده ها کلیر کش کردن",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help b02=new Help("آموزش دریافت تنظیمات و لاگین کردن",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help b03=new Help("آموزش صفحه دریافت تور ",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                helps.add(b00);
                helps.add(b01);
                helps.add(b02);
                helps.add(b03);
                break;
            case 1:
                Help b10=new Help("آموزش کامل اپلیکیشن توزیع (مخصوص موزع)",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help b11=new Help("آموزش پاک کردن داده ها کلیر کش کردن",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help b12=new Help("آموزش دریافت تنظیمات و لاگین کردن",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help b13=new Help("آموزش صفحه دریافت تور ",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                helps.add(b10);
                helps.add(b11);
                helps.add(b12);
                helps.add(b13);
                break;
            case 2:
                Help he21=new Help("آموزش صفحه لیست مشتریان",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he22=new Help("آموزش ثبت مشتری جدید",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he23=new Help("آموزش صفحه کاربری",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                helps.add(he21);
                helps.add(he22);
                helps.add(he23);
                break;
            case 3:
                Help he30=new Help("آموزش صفحه مشتری",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he31=new Help("آموزش ویرایش مشتری",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he32=new Help("آموزش عکس مشتری",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he33=new Help("آموزش درخواست برگشتی",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he34=new Help("آموزش درخواست فروش",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he35=new Help("آموزش پرسشنامه",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                Help he36=new Help("آموزش ثبت موقعیت",
                        "http://5.160.125.98:8080/content/presale_full.mp4");
                helps.add(he30);
                helps.add(he31);
                helps.add(he32);
                helps.add(he33);
                helps.add(he34);
                helps.add(he35);
                helps.add(he36);
                break;
        }

        return helps;
    }


}
