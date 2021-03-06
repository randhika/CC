package com.ly.cc.view.function;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.ly.cc.ConstantValue;
import com.ly.cc.manager.UniversalAdapter;
import com.ly.cc.manager.ViewHolder;
import com.ly.cc.utils.AppUtil;
import com.ly.cc.utils.T;

import org.androidannotations.annotations.App;

import java.util.List;

/**
 * Created by xzzz on 2015/1/9.
 */
public class FunctionFragment extends ListFragment {

    private UniversalAdapter<String> ccAdapter;
    private Context ctx;

    public static FunctionFragment newInstance(String[] ccList) {
        FunctionFragment fw = new FunctionFragment();
        if (ccList != null && ccList.length > 0) {
            Bundle b = new Bundle();
            b.putStringArray("ccList", ccList);
            fw.setArguments(b);
        }
        return fw;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ctx = getActivity();

        Bundle arg = getArguments();
        String[] ccLists = arg.getStringArray("ccList");
        if (ccLists == null || ccLists.length <= 0) return;

        ccAdapter = new UniversalAdapter<String>(getActivity(), ccLists, android.R.layout.simple_list_item_1) {
            @Override
            public void convert(ViewHolder vh, String item, int position) {
                if (item == null)
                    return;

                if (vh == null)
                    return;

                vh.setText(android.R.id.text1, item);
            }
        };

        setListAdapter(ccAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent();
        switch (position) {
            case 0://发送邮件
                goToSendEmail();
                break;
            case 1://读取手机联系人
                i.setClass(getActivity(), GravitySensorActivity.class);
                startActivity(i);
                break;
            case 2://跳转到网页
                AppUtil.goToWebsite(getActivity(), "https://github.com/LeeeYou");
                break;
            case 3://通用分享
                goToShare();
                break;
            case 4://重力感应
                i.setClass(getActivity(), GravitySensorActivity.class);
                startActivity(i);
                break;
            case 5://跳转到市场
                goToMarket();
                break;
            case 6://播放视频
                i.setClass(getActivity(), PlayVideoListActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void goToMarket() {
        final PackageManager packageManager = ctx.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        int packageInfoSize = pinfo.size();
        for (int i = 0; i < packageInfoSize; i++) {
            String pkname = pinfo.get(i).packageName;
            if (!TextUtils.isEmpty(pkname)) {
                for (int j = 0; j < ConstantValue.appStores.length; j++) {
                    if (pkname.contains(ConstantValue.appStores[j]) && ctx != null) {
                        String pkgName = ctx.getPackageName();
                        Uri uri = Uri.parse("market://details?id=" + pkgName);
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mIntent);
                        return;
                    }
                }
            }
        }

        T.showShort(ctx, "请先安装市场客户端");
    }

    /**
     * 通用的分享功能
     */
    private void goToShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "这是我分享给你的一段测试程序");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getActivity().getTitle()));
    }

    private void goToSendEmail() {
        final PackageManager packageManager = ctx.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        int packageInfoSize = pinfo.size();

        for (int j = 0; j < packageInfoSize; j++) {
            String pkname = pinfo.get(j).packageName;

            if (!TextUtils.isEmpty(pkname) //
                    && (pkname.contains("mail") || pkname.contains("outlook"))) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:leeeyou@163.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "【乐现宝宝】意见反馈" + System.currentTimeMillis());
                intent.putExtra(Intent.EXTRA_TEXT, "请告诉乐现你具体遇到的问题或想反馈的意见\n乐现会通过邮箱联系你哦\n");

                if (ctx.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    startActivity(intent);
                } else {
                    AppUtil.runApp(ctx, pkname);
                }

                return;
            }
        }

        T.showShort(ctx, "请先安装邮件客户端");
    }

}
