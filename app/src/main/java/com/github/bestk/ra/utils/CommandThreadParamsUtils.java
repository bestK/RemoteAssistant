package com.github.bestk.ra.utils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.github.bestk.ra.model.CommandResult;
import com.github.bestk.ra.model.RemoteCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommandThreadParamsUtils {

    private static final ShareThreadLocal shareData = new ShareThreadLocal();

    public static void push(RemoteCommand command, Properties context) {
        List<CommandResult> list = get();
        list.add(new CommandResult(command, context));
        shareData.set(list);
        LogUtils.d("线程id", Thread.currentThread().getId(), GsonUtils.toJson(shareData.get()));
    }

    public static List<CommandResult> get() {
        return shareData.get();
    }

    public static ShareThreadLocal instance() {
        return shareData;
    }

    public static void clear() {
        shareData.remove();
    }


    public static class ShareThreadLocal extends ThreadLocal<List<CommandResult>> {

        @Override
        public List<CommandResult> get() {
            if (super.get() == null) {
                return new ArrayList<>();
            }

            return super.get() == null ? new ArrayList<>() : super.get();
        }



    }
}
