package com.vipycm.mao.hack;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Message;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.hack.Hack.HackDeclaration.HackAssertionException;
import com.vipycm.mao.hack.Hack.HackedClass;
import com.vipycm.mao.hack.Hack.HackedMethod;

import java.lang.reflect.Field;

/**
 * Created by mao on 17-2-14.
 */

public class MaoHack {
    public static final int STOP_ACTIVITY_SHOW;
    public static final int STOP_ACTIVITY_HIDE;
    public static final int RECEIVER;
    public static final int CREATE_SERVICE;
    public static final int GC_WHEN_IDLE;
    public static final int LAUNCH_ACTIVITY;
    public static final int DESTROY_ACTIVITY;
    public static final int ENTER_ANIMATION_COMPLETE;

    static {
        Class clazz = null;
        try {
            clazz = Class.forName("android.app.ActivityThread$H");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int id;
        id = getHMember(clazz, "STOP_ACTIVITY_SHOW");
        STOP_ACTIVITY_SHOW = (id >= 0) ? id : 103;
        id = getHMember(clazz, "STOP_ACTIVITY_HIDE");
        STOP_ACTIVITY_HIDE = (id >= 0) ? id : 104;
        id = getHMember(clazz, "RECEIVER");
        RECEIVER = (id >= 0) ? id : 113;
        id = getHMember(clazz, "CREATE_SERVICE");
        CREATE_SERVICE = (id >= 0) ? id : 114;
        id = getHMember(clazz, "GC_WHEN_IDLE");
        GC_WHEN_IDLE = (id >= 0) ? id : 120;
        id = getHMember(clazz, "LAUNCH_ACTIVITY");
        LAUNCH_ACTIVITY = (id >= 0) ? id : 100;
        id = getHMember(clazz, "DESTROY_ACTIVITY");
        DESTROY_ACTIVITY = (id >= 0) ? id : 109;
        id = getHMember(clazz, "ENTER_ANIMATION_COMPLETE");
        ENTER_ANIMATION_COMPLETE = (id >= 0) ? id : 149;
    }

    private static int getHMember(Class clazz, String name) {
        int ret = -1;
        if (clazz == null) {
            return ret;
        }
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            ret = field.getInt(null);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static class HandlerHack implements Handler.Callback {

        MaoLog log = MaoLog.getLogger("MaoHack$HandlerHack");
        Handler mHandler;

        HandlerHack(Handler handler) {
            mHandler = handler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == LAUNCH_ACTIVITY) {
                try {
                    Field intentField = msg.obj.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent) intentField.get(msg.obj);
                    String component = intent.getComponent().getClassName();
                    log.i("LAUNCH_ACTIVITY:" + component);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == CREATE_SERVICE) {
                try {
                    Field infoField = msg.obj.getClass().getDeclaredField("info");
                    infoField.setAccessible(true);
                    ServiceInfo info = (ServiceInfo) infoField.get(msg.obj);

                    log.i("CREATE_SERVICE:" + info.name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == RECEIVER) {
                try {
                    Field intentField = msg.obj.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent intent = (Intent) intentField.get(msg.obj);
                    String component = intent.getComponent().getClassName();
                    log.i("RECEIVER:" + component);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mHandler.handleMessage(msg);
            return true;
        }
    }

    /**
     * hook ActivityThread中mH(Handler)的mCallback
     */
    public static void hackH() {
        try {
            HackedClass<Object> ActivityThread = Hack.into("android.app.ActivityThread");
            HackedMethod ActivityThread_currentActivityThread = ActivityThread.method("currentActivityThread");
            Object currentActivityThread = ActivityThread_currentActivityThread.invoke(null);
            Handler handler = (Handler) ActivityThread.field("mH").ofType(Hack.into("android.app.ActivityThread$H")
                    .getmClass()).get(currentActivityThread);
            Field declaredField = Handler.class.getDeclaredField("mCallback");
            declaredField.setAccessible(true);
            declaredField.set(handler, new HandlerHack(handler));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (HackAssertionException e) {
            e.printStackTrace();
        }
    }
}
